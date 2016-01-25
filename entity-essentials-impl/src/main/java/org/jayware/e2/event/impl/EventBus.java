/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
 *                    Markus Neubauer <markus.neubauer@jayware.org>
 *
 *     This file is part of Entity Essentials.
 *
 *     Entity Essentials is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     Entity Essentials is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jayware.e2.event.impl;

import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Disposable;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventDispatchException;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventDispatcherFactory;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Subscription;
import org.jayware.e2.util.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntUnaryOperator;

import static java.lang.Thread.currentThread;


public class EventBus
implements Disposable
{
    private static final int DISPATCH_CIRCUIT_BREAKER_THRESHOLD = 5;

    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

    private final Context myContext;

    private final EventDispatcherFactory myEventDispatcherFactory;
    private final EventBusWorkerPool myWorkerPool;

    private final Map<Object, Subscription> mySubscriptionsMap;
    private final AtomicInteger mySubscriptionsMapHash;

    private final AtomicReference<Collection<Subscription>> myLastSubscriptionCollection;
    private final AtomicInteger myLastSubscriptionsMapHash;

    private final ReadWriteLock myReadWriteLock = new ReentrantReadWriteLock();
    private final Lock myReadLock = myReadWriteLock.readLock();
    private final Lock myWriteLock = myReadWriteLock.writeLock();

    public EventBus(Context context)
    {
        myContext = context;

        myEventDispatcherFactory = new EventDispatcherFactoryImpl();

        mySubscriptionsMap = new HashMap<>();
        mySubscriptionsMapHash = new AtomicInteger(mySubscriptionsMap.hashCode());
        myLastSubscriptionCollection = new AtomicReference<>();
        myLastSubscriptionsMapHash = new AtomicInteger();

        updateLastSubscriptionCollection();

        myWorkerPool = new EventBusWorkerPool(4, 64);
    }

    public void subscribe(Object subscriber, ReferenceType referenceType, EventFilter[] filters)
    {
        final EventDispatcher eventDispatcher = myEventDispatcherFactory.createEventDispatcher(subscriber.getClass());

        myWriteLock.lock();
        try
        {
            if (!mySubscriptionsMap.containsKey(subscriber))
            {
                switch (referenceType)
                {
                    case Strong:
                        mySubscriptionsMap.put(subscriber, new SubscriptionImpl_StrongReference(subscriber, eventDispatcher, filters));
                        break;
                    case Weak:
                    default:
                        mySubscriptionsMap.put(subscriber, new SubscriptionImpl_WeakReference(subscriber, eventDispatcher, filters));
                        break;
                }

                mySubscriptionsMapHash.set(mySubscriptionsMap.hashCode());
                log.debug("Subscribe: [ {} ] {} Dispatcher: {}", referenceType, subscriber.getClass().getName(), eventDispatcher.getClass().getName());
            }
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    public void unsubscribe(Object subscriber)
    {
        myWriteLock.lock();
        try
        {
            Object result = mySubscriptionsMap.remove(subscriber);

            if (result != null)
            {
                mySubscriptionsMapHash.set(mySubscriptionsMap.hashCode());
                log.debug("Unsubscribe: {}", subscriber.getClass().getName());
            }
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    public void send(Event event)
    {
        myWorkerPool.send(event);
    }

    public void post(Event event)
    {
        myWorkerPool.post(event);
    }

    @Override
    public void dispose(Context context)
    {
        myWorkerPool.shutdown();
        mySubscriptionsMap.clear();
    }

    protected Collection<Subscription> subscriptions()
    {
        return mySubscriptionsMap.values();
    }

    private void updateLastSubscriptionCollection()
    {
        while (myLastSubscriptionsMapHash.get() != mySubscriptionsMapHash.get())
        {
            myReadLock.lock();
            try
            {
                myLastSubscriptionCollection.set(new HashSet<>(mySubscriptionsMap.values()));
                myLastSubscriptionsMapHash.set(mySubscriptionsMapHash.get());
            }
            finally
            {
                myReadLock.unlock();
            }
        }
    }

    private EventDispatch createEventDispatch(Event event)
    {
        updateLastSubscriptionCollection();
        return new EventDispatch(event, myLastSubscriptionCollection.get());
    }

    private class EventBusWorkerPool
    {
        private final ThreadGroup myThreadGroup;
        private final List<EventBusWorker> myWorkerList;
        private final AtomicInteger nextWorkerIndex = new AtomicInteger(0);

        private EventBusWorkerPool(int workers, int workersQueueSize)
        {
            myThreadGroup = new ThreadGroup("event-bus");
            myWorkerList = new CopyOnWriteArrayList<>();
            for (int i = 0; i < workers; ++i)
            {
                final EventBusWorker worker = new EventBusWorker(myThreadGroup, myThreadGroup.getName() + "-worker-" + i, workersQueueSize);
                myWorkerList.add(worker);
            }
        }

        public void send(Event event)
        {
            final Thread currentThread = currentThread();
            EventBusWorker worker = null;

            // To send an event synchronously from a worker thread, the event can be dispatched
            // within the same worker thread. This way avoids blocking of workers.
            for (EventBusWorker aWorker : myWorkerList)
            {
                if (aWorker.myThread.equals(currentThread))
                {
                    worker = aWorker;
                    break;
                }
            }

            // It has to be a foreign thread, because no worker was found...
            if (worker == null)
            {
                // ...therefore choose simply the next worker.
                worker = nextWorker();
            }

            worker.send(event);
        }

        public void post(Event event)
        {
            nextWorker().post(event);
        }

        public void shutdown()
        {
            for (EventBusWorker eventBusWorker : myWorkerList)
            {
                eventBusWorker.shutdown();
            }

            myWorkerList.clear();
        }

        private EventBusWorker nextWorker()
        {
            // Simple round robin. Get the worker at the given index and than increment the index.
            // If the index is greater then the number of workers reset the index to zero.
            return myWorkerList.get(nextWorkerIndex.getAndUpdate(new IntUnaryOperator()
            {
                @Override
                public int applyAsInt(int index)
                {
                    return index < myWorkerList.size() - 1 ? index + 1 : 0;
                }
            }));
        }
    }

    private class EventBusWorker
    implements Runnable
    {
        private final Logger log = LoggerFactory.getLogger(EventBusWorker.class);

        private final Thread myThread;
        private final BlockingQueue<EventDispatch> myDispatchQueue;
        private final Stack<EventDispatch> myExecutionStack;

        private final AtomicBoolean keepRunning = new AtomicBoolean(false);

        public EventBusWorker(ThreadGroup threadGroup, String threadName, int queueSize)
        {
            myDispatchQueue = new ArrayBlockingQueue<>(queueSize);
            myExecutionStack = new Stack<>();

            myThread = new Thread(threadGroup, this, threadName);
            myThread.start();
        }

        public void send(Event event)
        {
            final EventDispatch dispatch = createEventDispatch(event);

            // Distinguish between the worker's thread and a foreign
            // thread, because the worker cannot wait on itself.
            if (myThread != currentThread())
            {
                try
                {
                    myDispatchQueue.put(dispatch);
                    dispatch.await();
                }
                catch (InterruptedException e)
                {
                    log.warn("Interrupted while waiting for send!", e);
                }
            }
            else
            {
                execute(dispatch);
            }
        }

        public void post(Event event)
        {
            final EventDispatch dispatch = createEventDispatch(event);

            try
            {
                myDispatchQueue.put(dispatch);
            }
            catch (InterruptedException e)
            {
                log.warn("Interrupted while waiting for post!", e);
            }
        }

        public void shutdown()
        {
            keepRunning.set(false);
            myThread.interrupt();

            try
            {
                myThread.join();
            }
            catch (InterruptedException e)
            {
                log.warn("Interrupted while waiting for shutdown!", e);
            }
        }

        @Override
        public void run()
        {
            keepRunning.set(true);
            while (keepRunning.get())
            {
                try
                {
                    execute(myDispatchQueue.take());
                }
                catch (InterruptedException e)
                {
                    log.warn("Interrupted while waiting for take!", e);
                }
                catch (Exception e)
                {
                    log.error("Failed to dispatch event!", e);
                }
            }
        }

        private void execute(EventDispatch dispatch)
        {
            final Event event = dispatch.getEvent();
            myExecutionStack.push(dispatch);

            try
            {
                if (myExecutionStack.size() >= DISPATCH_CIRCUIT_BREAKER_THRESHOLD)
                {
                    throw new EventDispatchException(
                        "Circuit Breaker aborts event dispatch! " +
                        "This may be an evidence for a bug, because the number of synchronously dispatched events reached " +
                        "the circuit breaker's threshold ( " + DISPATCH_CIRCUIT_BREAKER_THRESHOLD + " ). " +
                        "To many synchronously dispatched events may exhaust the stack of the JVM and lead to StackOverflowErrors.",
                        event
                    );
                }

                for (Subscription subscription : dispatch.getSubscriptions())
                {
                    final EventFilter[] filters = subscription.getFilters();
                    final EventDispatcher eventDispatcher = subscription.getEventDispatcher();
                    boolean doDispatch = true;

                    if (eventDispatcher.accepts(event.getType()))
                    {
                        for (EventFilter filter : filters)
                        {
                            if (!filter.accepts(myContext, event))
                            {
                                doDispatch = false;
                                break;
                            }
                        }

                        if (doDispatch)
                        {
                            try
                            {
                                eventDispatcher.dispatch(event, subscription.getSubscriber());
                            }
                            catch (Exception exception)
                            {
                                log.error("", new EventDispatchException("Failed to dispatch event!", event, exception));
                            }
                        }
                    }
                }
            }
            finally
            {
                dispatch.setDispatched();
                myExecutionStack.pop();
            }
        }
    }

    private class EventDispatch
    {
        private final Event myEvent;
        private final Collection<Subscription> mySubscriptions;
        private final CountDownLatch isDispatched;

        private EventDispatch(Event event, Collection<Subscription> subscriptions)
        {
            myEvent = event;
            mySubscriptions = subscriptions;
            isDispatched = new CountDownLatch(1);
        }

        public void setDispatched()
        {
            isDispatched.countDown();
        }

        public boolean isDispatched()
        {
            return isDispatched.getCount() == 0;
        }

        public Event getEvent()
        {
            return myEvent;
        }

        public Collection<Subscription> getSubscriptions()
        {
            return mySubscriptions;
        }

        public void await()
        {
            try
            {
                isDispatched.await();
            }
            catch (InterruptedException e)
            {
                log.warn("Interrupted while waiting for dispatch!", e);
            }
        }
    }
}
