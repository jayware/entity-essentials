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
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.MissingResultException;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.event.api.QueryException;
import org.jayware.e2.event.api.ReadOnlyParameters;
import org.jayware.e2.event.api.Result;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.event.api.Subscription;
import org.jayware.e2.util.Key;
import org.jayware.e2.util.ReferenceType;
import org.jayware.e2.util.StateLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

import static java.lang.Thread.currentThread;
import static org.jayware.e2.event.api.Query.State.Failed;
import static org.jayware.e2.event.api.Query.State.Running;
import static org.jayware.e2.event.api.Query.State.Success;


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

        myLastSubscriptionCollection.set(new HashSet<>(mySubscriptionsMap.values()));
        myLastSubscriptionsMapHash.set(mySubscriptionsMapHash.get());

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

    public ResultSet query(Query query)
    {
        return myWorkerPool.query(query);
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

    private QueryDispatch createQueryDispatch(Query query)
    {
        updateLastSubscriptionCollection();

        return new QueryDispatch((QueryImpl) query, myLastSubscriptionCollection.get());
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

        public ResultSet query(Query query)
        {
            return nextWorker().query(query);
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
                catch (InterruptedException ignored) {}
            }
            else
            {
                dispatch(dispatch);
            }
        }

        public void post(Event event)
        {
            final EventDispatch dispatch = createEventDispatch(event);

            try
            {
                myDispatchQueue.put(dispatch);
            }
            catch (InterruptedException ignored) {}
        }

        public ResultSet query(Query query)
        {
            final QueryDispatch dispatch = createQueryDispatch(query);

            try
            {
                myDispatchQueue.put(dispatch);
                return dispatch.getResult();
            }
            catch (InterruptedException e)
            {
                throw new QueryException("Failed to dispatch query!", query, e);
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
            catch (InterruptedException ignored) {}
        }

        @Override
        public void run()
        {
            keepRunning.set(true);
            while (keepRunning.get())
            {
                try
                {
                    dispatch(myDispatchQueue.take());
                }
                catch (InterruptedException ignored) {}
                catch (Exception e)
                {
                    log.error("Failed to dispatch event!", e);
                }
            }
        }

        private void dispatch(EventDispatch dispatch)
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

                dispatch.execute();
            }
            finally
            {
                myExecutionStack.pop();
            }
        }
    }

    private class EventDispatch
    {
        protected final Event myEvent;
        protected final Collection<Subscription> mySubscriptions;
        protected final CountDownLatch isDispatched;

        private EventDispatch(Event event, Collection<Subscription> subscriptions)
        {
            myEvent = event;
            mySubscriptions = subscriptions;
            isDispatched = new CountDownLatch(1);
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
            catch (InterruptedException ignored) {}
        }

        public void execute()
        {
            try
            {
                for (Subscription subscription : mySubscriptions)
                {
                    final EventFilter[] filters = subscription.getFilters();
                    final EventDispatcher eventDispatcher = subscription.getEventDispatcher();
                    boolean doDispatch = true;

                    if (eventDispatcher.accepts(myEvent.getType()))
                    {
                        for (EventFilter filter : filters)
                        {
                            if (!filter.accepts(myContext, myEvent))
                            {
                                doDispatch = false;
                                break;
                            }
                        }

                        if (doDispatch)
                        {
                            try
                            {
                                eventDispatcher.dispatch(myEvent, subscription.getSubscriber());
                            }
                            catch (Exception exception)
                            {
                                log.error("", new EventDispatchException("Failed to dispatch event!", myEvent, exception));
                            }
                        }
                    }
                }
            }
            finally
            {
                isDispatched.countDown();
            }
        }
    }

    private class QueryDispatch
    extends EventDispatch
    {
        private final QueryWrapper myQuery;

        public QueryDispatch(QueryImpl query, Collection<Subscription> subscriptions)
        {
            super(new QueryWrapper(query, new QueryResultSet(query)), subscriptions);
            myQuery = (QueryWrapper) myEvent;
        }

        public Query getQuery()
        {
            return myQuery;
        }

        public ResultSet getResult()
        {
            return myQuery.getResult();
        }

        @Override
        public void execute()
        {
            final QueryResultSet result = myQuery.getResult();
            result.signal(Running);

            try
            {
                boolean queryFailed = false;

                for (Subscription subscription : mySubscriptions)
                {
                    final EventFilter[] filters = subscription.getFilters();
                    final EventDispatcher eventDispatcher = subscription.getEventDispatcher();
                    boolean doDispatch = true;

                    if (eventDispatcher.accepts(myQuery.getType()))
                    {
                        for (EventFilter filter : filters)
                        {
                            if (!filter.accepts(myContext, myQuery))
                            {
                                doDispatch = false;
                                break;
                            }
                        }

                        if (doDispatch)
                        {
                            try
                            {
                                eventDispatcher.dispatch(myQuery, subscription.getSubscriber());
                            }
                            catch (Exception exception)
                            {
                                log.error("", new EventDispatchException("Failed to dispatch event!", myQuery, exception));
                                queryFailed = true;
                            }
                        }
                    }
                }

                if (queryFailed)
                {
                    result.signal(Failed);
                }
                else
                {
                    result.signal(Success);
                }
            }
            finally
            {
                isDispatched.countDown();
            }
        }
    }

    private static class QueryWrapper
    implements Query
    {
        private final Query myQuery;
        private final QueryResultSet myResult;

        private QueryWrapper(Query query, QueryResultSet result)
        {
            myQuery = query;
            myResult = result;
        }

        @Override
        public <V> void result(String name, V value)
        {
            myResult.put(name, value);
        }

        @Override
        public <V> void result(Key<V> key, V value)
        {
            myResult.put(key, value);
        }

        @Override
        public Class<? extends EventType> getType()
        {
            return myQuery.getType();
        }

        @Override
        public boolean matches(Class<? extends EventType> type)
        {
            return myQuery.matches(type);
        }

        @Override
        public <V> V getParameter(String parameter)
        {
            return myQuery.getParameter(parameter);
        }

        @Override
        public boolean hasParameter(String parameter)
        {
            return myQuery.hasParameter(parameter);
        }

        @Override
        public ReadOnlyParameters getParameters()
        {
            return myQuery.getParameters();
        }

        @Override
        public boolean isQuery()
        {
            return true;
        }

        public QueryResultSet getResult()
        {
            return myResult;
        }
    }

    private static class QueryResultSet
    implements ResultSet
    {
        private final Query myQuery;

        private final StateLatch<State> myStateLatch;

        private final Map<Object, Object> myResultMap;
        private final Map<State, Consumer<ResultSet>> myConsumers;

        private QueryResultSet(QueryImpl query)
        {
            myQuery = query;

            myStateLatch = new StateLatch<>(State.class);
            myResultMap = new ConcurrentHashMap<>();
            myConsumers = query.getConsumers();
        }

        @Override
        public Query getQuery()
        {
            return myQuery;
        }

        @Override
        public boolean await(State state)
        {
            return myStateLatch.await(state);
        }

        @Override
        public boolean await(State state, long time, TimeUnit unit)
        {
            return myStateLatch.await(state, time, unit);
        }

        @Override
        public boolean hasStatus(State state)
        {
            return myStateLatch.hasState(state);
        }

        @Override
        public boolean hasResult()
        {
            return myStateLatch.hasState(Success);
        }

        public void put(Object key, Object value)
        {
            myResultMap.put(key, value);
        }

        @Override
        public <V> V get(String name)
        {
            final V value = find(name);

            if (value == null)
            {
                throw new MissingResultException("ResultSet does not contain a value associated to to the name: '" + name + "'", this);
            }

            return value;
        }

        @Override
        public <V> V get(Key<V> key)
        {
            final V value = find(key);

            if (value == null)
            {
                throw new MissingResultException("ResultSet does not contain a value associated to to the key: '" + key + "'", this);
            }

            return value;
        }

        @Override
        public <V> V find(String name)
        {
            await(Success);
            return (V) myResultMap.get(name);
        }

        @Override
        public <V> V find(Key<V> key)
        {
            await(Success);
            return (V) myResultMap.get(key);
        }

        @Override
        public boolean has(String name)
        {
            return myResultMap.containsKey(name);
        }

        @Override
        public boolean has(Key<?> key)
        {
            return myResultMap.containsKey(key);
        }

        @Override
        public <T> Result<T> resultOf(Key<T> key)
        {
            return new KeyQueryResult<>(key);
        }

        @Override
        public <T> Result<T> resultOf(String name)
        {
            return new NameQueryResult<>(name);
        }

        public void signal(State state)
        {
            myStateLatch.signal(state);

            try
            {
                final Consumer<ResultSet> consumer = myConsumers.get(state);
                if (consumer != null)
                {
                    consumer.accept(this);
                }
            }
            catch (Exception e)
            {
                log.error("Failed to signal query state-change!", e);
            }
        }

        private abstract class AbstractQueryResult<T>
        implements Result<T>
        {
            @Override
            public Query getQuery()
            {
                return myQuery;
            }

            @Override
            public boolean await(State state)
            {
                return QueryResultSet.this.await(state);
            }

            @Override
            public boolean await(State state, long time, TimeUnit unit)
            {
                return QueryResultSet.this.await(state, time, unit);
            }

            @Override
            public boolean hasStatus(State state)
            {
                return QueryResultSet.this.hasStatus(state);
            }
        }

        private class KeyQueryResult<T>
        extends AbstractQueryResult<T>
        {
            private final Key<T> myKey;

            private KeyQueryResult(Key<T> key)
            {
                myKey = key;
            }

            @Override
            public boolean hasResult()
            {
                return QueryResultSet.this.has(myKey);
            }

            @Override
            public T get()
            {
                return QueryResultSet.this.get(myKey);
            }

            @Override
            public T find()
            {
                return QueryResultSet.this.find(myKey);
            }
        }

        private class NameQueryResult<T>
        extends AbstractQueryResult<T>
        {
            private final String myName;

            private NameQueryResult(String name)
            {
                myName = name;
            }

            @Override
            public boolean hasResult()
            {
                return QueryResultSet.this.has(myName);
            }

            @Override
            public T get()
            {
                return QueryResultSet.this.get(myName);
            }

            @Override
            public T find()
            {
                return QueryResultSet.this.find(myName);
            }
        }
    }
}
