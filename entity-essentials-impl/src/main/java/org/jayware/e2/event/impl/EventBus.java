/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2017 Elmar Schug <elmar.schug@jayware.org>,
 *                    Markus Neubauer <markus.neubauer@jayware.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.jayware.e2.event.api.SubscriptionBookkeeper;
import org.jayware.e2.event.api.SubscriptionFactory;
import org.jayware.e2.util.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jayware.e2.util.ObjectUtil.getClassNameOf;


public class EventBus
implements Disposable
{
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

    private final Context myContext;

    private final EventDispatcherFactory myEventDispatcherFactory;
    private final SubscriptionFactory mySubscriptionFactory;

    private final SubscriptionBookkeeper myBookkeeper;

    private final ThreadPoolExecutor myWorkerPool;

    public EventBus(Context context)
    {
        myContext = context;
        myEventDispatcherFactory = new EventDispatcherFactoryImpl();
        mySubscriptionFactory = new SubscriptionFactoryImpl();
        myBookkeeper = new SubscriptionBookkeeperImpl();
        myWorkerPool = new ThreadPoolExecutor(4, 4, 0L, SECONDS, new ArrayBlockingQueue<Runnable>(256), new EventBusThreadFactory(), new CallerRunsPolicy());
    }

    public void subscribe(Object subscriber, ReferenceType referenceType, EventFilter[] filters)
    {
        final EventDispatcher eventDispatcher;
        final Subscription subscription;

        if (myBookkeeper.isSubscribed(subscriber))
        {
            return;
        }

        eventDispatcher = myEventDispatcherFactory.createEventDispatcher(subscriber.getClass());
        subscription = mySubscriptionFactory.createSubscription(subscriber, referenceType, filters, eventDispatcher);

        myBookkeeper.subscribe(subscription);

        log.debug("Subscribe: [ {} ] {} Dispatcher: {}", subscription.getReferenceType(), getClassNameOf(subscription), getClassNameOf(subscription.getEventDispatcher()));
    }

    public void unsubscribe(Object subscriber)
    {
        myBookkeeper.unsubscribe(subscriber);

        log.debug("Unsubscribe: {}", getClassNameOf(subscriber));
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

        myBookkeeper.clear();
    }

    private EventDispatch createEventDispatch(Event event)
    {
        return new EventDispatch(myContext, event, myBookkeeper.subscriptions());
    }

    private QueryDispatch createQueryDispatch(Query query)
    {
        return new QueryDispatch(myContext, (QueryImpl) query, myBookkeeper.subscriptions());
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
