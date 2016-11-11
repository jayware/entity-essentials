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
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventDispatcherFactory;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.event.api.Subscription;
import org.jayware.e2.util.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.SECONDS;


public class EventBus
implements Disposable
{
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

    private final Context myContext;

    private final EventDispatcherFactory myEventDispatcherFactory;

    private final Map<Object, Subscription> mySubscriptionsMap;
    private final AtomicInteger mySubscriptionsMapHash;

    private final AtomicReference<Collection<Subscription>> myLastSubscriptionCollection;
    private final AtomicInteger myLastSubscriptionsMapHash;

    private final ReadWriteLock myReadWriteLock = new ReentrantReadWriteLock();
    private final Lock myReadLock = myReadWriteLock.readLock();
    private final Lock myWriteLock = myReadWriteLock.writeLock();

    private final ThreadPoolExecutor myWorkerPool;

    public EventBus(Context context)
    {
        myContext = context;

        myEventDispatcherFactory = new EventDispatcherFactoryImpl();

        mySubscriptionsMap = new HashMap<Object, Subscription>();
        mySubscriptionsMapHash = new AtomicInteger(mySubscriptionsMap.hashCode());
        myLastSubscriptionCollection = new AtomicReference<Collection<Subscription>>();
        myLastSubscriptionsMapHash = new AtomicInteger();

        myLastSubscriptionCollection.set(new HashSet<Subscription>(mySubscriptionsMap.values()));
        myLastSubscriptionsMapHash.set(mySubscriptionsMapHash.get());

        myWorkerPool = new ThreadPoolExecutor(4, 4, 0L, SECONDS, new ArrayBlockingQueue<Runnable>(128), new EventBusThreadFactory());
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
        final EventDispatch dispatch = createEventDispatch(event);
        dispatch.run();
    }

    public void post(Event event)
    {
        final EventDispatch dispatch = createEventDispatch(event);
        myWorkerPool.execute(dispatch);
    }

    public ResultSet query(Query query)
    {
        final QueryDispatch dispatch = createQueryDispatch(query);
        myWorkerPool.execute(dispatch);
        return dispatch.getResult();
    }

    @Override
    public void dispose(Context context)
    {
        final List<Runnable> neverCommencedRunnables = myWorkerPool.shutdownNow();

        for (Runnable runnable : neverCommencedRunnables)
        {
            log.warn("Due to disposing the event bus, never commenced the dispatch of: {}", runnable);
        }

        mySubscriptionsMap.clear();
        updateLastSubscriptionCollection();
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
                myLastSubscriptionCollection.set(new HashSet<Subscription>(mySubscriptionsMap.values()));
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

        return new EventDispatch(myContext, event, myLastSubscriptionCollection.get());
    }

    private QueryDispatch createQueryDispatch(Query query)
    {
        updateLastSubscriptionCollection();

        return new QueryDispatch(myContext, (QueryImpl) query, myLastSubscriptionCollection.get());
    }

    private static class EventBusThreadFactory
    implements ThreadFactory
    {
        private final ThreadGroup myThreadGroup = new ThreadGroup("entity-essentials");
        private final AtomicInteger nextWorkerId = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable)
        {
            return new Thread(myThreadGroup, runnable, myThreadGroup.getName() + "-worker-" + nextWorkerId.getAndIncrement());
        }
    }
}
