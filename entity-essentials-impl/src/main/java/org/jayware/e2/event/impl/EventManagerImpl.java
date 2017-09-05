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
import org.jayware.e2.context.api.Context.ValueProvider;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventBuilder;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.QueryBuilder;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.event.api.SanityCheck;
import org.jayware.e2.event.api.SanityCheckFailedException;
import org.jayware.e2.event.api.SanityChecker;
import org.jayware.e2.util.Consumer;
import org.jayware.e2.util.Key;
import org.jayware.e2.util.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.util.UUID.randomUUID;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Preconditions.checkEventNotNull;
import static org.jayware.e2.event.impl.EventBuilderImpl.createEventBuilder;
import static org.jayware.e2.event.impl.QueryBuilderImpl.createQueryBuilder;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.ReferenceType.WEAK;


public class EventManagerImpl
implements EventManager
{
    static final Key<EventBus> EVENT_BUS = Key.createKey("org.jayware.e2.EventBus");
    static final EventFilter[] EMPTY_FILTER_ARRAY = new EventFilter[0];
    static final ValueProvider<EventBus> EVENT_BUS_VALUE_PROVIDER = new ValueProvider<EventBus>()
    {
        @Override
        public EventBus provide(Context context)
        {
            return new EventBus(context);
        }
    };

    private static final Logger log = LoggerFactory.getLogger(EventManagerImpl.class);

    @Override
    public EventBuilder createEvent(Class<? extends RootEvent> type)
    {
        return createEventBuilder(type);
    }

    @Override
    public Event createEvent(Class<? extends RootEvent> type, Parameter... parameters)
    {
        checkNotNull(type);
        checkNotNull(parameters);

        return new EventImpl(randomUUID(), type, parameters);
    }

    @Override
    public Event createEvent(Class<? extends RootEvent> type, Parameters parameters)
    {
        checkNotNull(type);
        checkNotNull(parameters);

        return new EventImpl(randomUUID(), type, parameters);
    }

    @Override
    public QueryBuilder createQuery(Class<? extends RootEvent> type)
    {
        checkNotNull(type);

        return createQueryBuilder(type);
    }

    @Override
    public Query createQuery(Class<? extends RootEvent> type, Parameter... parameters)
    {
        checkNotNull(type);
        checkNotNull(parameters);

        return new QueryImpl(randomUUID(), type, parameters, Collections.<Query.State, Consumer<ResultSet>>emptyMap());
    }

    @Override
    public Query createQuery(Class<? extends RootEvent> type, Parameters parameters)
    {
        return new QueryImpl(randomUUID(), type, parameters, Collections.<Query.State, Consumer<ResultSet>>emptyMap());
    }

    @Override
    public void subscribe(Context context, Object subscriber)
    {
        subscribe(context, subscriber, WEAK, EMPTY_FILTER_ARRAY);
    }

    @Override
    public void subscribe(Context context, Object subscriber, ReferenceType referenceType)
    {
        subscribe(context, subscriber, referenceType, EMPTY_FILTER_ARRAY);
    }

    @Override
    public void subscribe(Context context, Object subscriber, EventFilter[] filters)
    {
        subscribe(context, subscriber, WEAK, filters);
    }

    @Override
    public void subscribe(Context context, Object subscriber, ReferenceType referenceType, EventFilter[] filters)
    {
        final EventBus eventBus;

        checkNotNull(context);
        checkNotNull(subscriber);
        checkNotNull(referenceType);
        checkNotNull(filters);

        eventBus = context.getOrCreate(EVENT_BUS, EVENT_BUS_VALUE_PROVIDER);

        eventBus.subscribe(subscriber, referenceType, filters);
    }

    @Override
    public void unsubscribe(Context context, Object subscriber)
    {
        final EventBus eventBus;

        checkNotNull(context);
        checkNotNull(subscriber);

        eventBus = context.getOrCreate(EVENT_BUS, EVENT_BUS_VALUE_PROVIDER);

        eventBus.unsubscribe(subscriber);
    }

    @Override
    public void send(Class<? extends RootEvent> type, Parameter... parameters)
    {
        send(createEvent(type, parameters));
    }

    @Override
    public void send(Class<? extends RootEvent> type, Parameters parameters)
    {
        send(createEvent(type, parameters));
    }

    @Override
    public void send(EventBuilder builder)
    {
        send(builder.build());
    }

    @Override
    public void send(Event event)
    {
        final Context context;
        final EventBus eventBus;

        checkEventNotNull(event);
        sanityCheck(event);

        context = (Context) checkNotNull(event.getParameter(ContextParam));
        eventBus = context.getOrCreate(EVENT_BUS, EVENT_BUS_VALUE_PROVIDER);

        eventBus.send(event);
    }

    @Override
    public void post(Class<? extends RootEvent> type, Parameter... parameters)
    {
        post(createEvent(type, parameters));
    }

    @Override
    public void post(Class<? extends RootEvent> type, Parameters parameters)
    {
        post(createEvent(type, parameters));
    }

    @Override
    public void post(EventBuilder builder)
    {
        checkNotNull(builder);
        post(builder.build());
    }

    @Override
    public void post(Event event)
    {
        final Context context;
        final EventBus eventBus;

        checkEventNotNull(event);
        sanityCheck(event);

        context = (Context) checkNotNull(event.getParameter(ContextParam));
        eventBus = context.getOrCreate(EVENT_BUS, EVENT_BUS_VALUE_PROVIDER);

        eventBus.post(event);
    }

    @Override
    public ResultSet query(Class<? extends RootEvent> type, Parameter... parameters)
    {
        return query(createQuery(type, parameters));
    }

    @Override
    public ResultSet query(Class<? extends RootEvent> type, Parameters parameters)
    {
        return query(createQuery(type, parameters));
    }

    @Override
    public ResultSet query(QueryBuilder builder)
    {
        checkNotNull(builder);

        return query(builder.build());
    }

    @Override
    public ResultSet query(Query query)
    {
        checkEventNotNull(query);
        sanityCheck(query);

        final Context context = (Context) checkNotNull(query.getParameter(ContextParam));
        final EventBus eventBus = context.getOrCreate(EVENT_BUS, EVENT_BUS_VALUE_PROVIDER);

        return eventBus.query(query);
    }

    private static void sanityCheck(Event event)
    {
        final Queue<Class<? extends EventType>> queue = new LinkedList<Class<? extends EventType>>();
        final List<SanityChecker> checkerList = new LinkedList<SanityChecker>();

        queue.add(event.getType());

        try
        {
            while (!queue.isEmpty())
            {
                final Class<? extends EventType> type = queue.poll();
                final SanityCheck annotation = type.getAnnotation(SanityCheck.class);

                if (annotation != null)
                {
                    checkerList.add(annotation.value().newInstance());
                }

                for (Class<?> clazz : type.getInterfaces())
                {
                    if (EventType.class.isAssignableFrom(clazz))
                    {
                        queue.add((Class<? extends EventType>) clazz);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Failed to determine all SanityCheckers for: " + event.getType(), e);
            return;
        }

        for (SanityChecker checker : checkerList)
        {
            try
            {
                checker.check(event);
            }
            catch (SanityCheckFailedException e)
            {
                log.error("", e);
                throw e;
            }
        }
    }
}