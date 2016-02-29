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
import org.jayware.e2.event.api.Result;
import org.jayware.e2.event.api.SanityCheck;
import org.jayware.e2.event.api.SanityCheckFailedException;
import org.jayware.e2.event.api.SanityChecker;
import org.jayware.e2.util.Key;
import org.jayware.e2.util.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.util.Collections.emptyMap;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.impl.EventBuilderImpl.createEventBuilder;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.ReferenceType.Weak;


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
        return new EventImpl(type, parameters);
    }

    @Override
    public Event createEvent(Class<? extends RootEvent> type, Parameters parameters)
    {
        checkNotNull(type);
        checkNotNull(parameters);
        return new EventImpl(type, parameters);
    }

    @Override
    public QueryBuilder createQuery(Class<? extends RootEvent> type)
    throws IllegalArgumentException
    {
        throw new UnsupportedOperationException("EventManagerImpl.createQuery");
    }

    @Override
    public Query createQuery(Class<? extends RootEvent> type, Parameter... parameters)
    {
        checkNotNull(type);
        checkNotNull(parameters);
        return new QueryImpl(type, parameters, emptyMap());
    }

    @Override
    public Query createQuery(Class<? extends RootEvent> type, Parameters parameters)
    {
        return new QueryImpl(type, parameters, emptyMap());
    }

    @Override
    public void subscribe(Context context, Object subscriber)
    {
        subscribe(context, subscriber, Weak, EMPTY_FILTER_ARRAY);
    }

    @Override
    public void subscribe(Context context, Object subscriber, ReferenceType referenceType)
    {
        subscribe(context, subscriber, referenceType, EMPTY_FILTER_ARRAY);
    }

    @Override
    public void subscribe(Context context, Object subscriber, EventFilter[] filters)
    {
        subscribe(context, subscriber, Weak, filters);
    }

    @Override
    public void subscribe(Context context, Object subscriber, ReferenceType referenceType, EventFilter[] filters)
    {
        checkNotNull(context);
        checkNotNull(subscriber);
        checkNotNull(referenceType);
        checkNotNull(filters);

        final EventBus eventBus = getOrCreateEventBus(context);
        eventBus.subscribe(subscriber, referenceType, filters);
    }

    @Override
    public void unsubscribe(Context context, Object subscriber)
    {
        checkNotNull(context);
        checkNotNull(subscriber);

        final EventBus eventBus = getOrCreateEventBus(context);
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
        checkNotNull(event);
        sanityCheck(event);

        final Context context = checkNotNull(event.getParameter(ContextParam));
        final EventBus eventBus = getOrCreateEventBus(context);
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
        checkNotNull(event);
        sanityCheck(event);

        final Context context = checkNotNull(event.getParameter(ContextParam));
        final EventBus eventBus = getOrCreateEventBus(context);

        eventBus.post(event);
    }

    @Override
    public Result query(Class<? extends RootEvent> type, Parameter... parameters)
    {
        return query(createQuery(type, parameters));
    }

    @Override
    public Result query(Class<? extends RootEvent> type, Parameters parameters)
    {
        return query(createQuery(type, parameters));
    }

    @Override
    public Result query(QueryBuilder builder)
    {
        checkNotNull(builder);

        return query(builder.build());
    }

    @Override
    public Result query(Query query)
    {
        checkNotNull(query);
        sanityCheck(query);

        final Context context = checkNotNull(query.getParameter(ContextParam));
        final EventBus eventBus = getOrCreateEventBus(context);

        return eventBus.query(query);
    }

    private EventBus getOrCreateEventBus(Context context)
    {
        context.putIfAbsent(EVENT_BUS, EVENT_BUS_VALUE_PROVIDER);
        return context.get(EVENT_BUS);
    }

    private static void sanityCheck(Event event)
    {
        final Queue<Class<? extends EventType>> queue = new LinkedList<>();
        final List<SanityChecker> checkerList = new LinkedList<>();

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