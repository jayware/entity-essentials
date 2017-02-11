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
import org.jayware.e2.event.api.EventDispatchException;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.event.api.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jayware.e2.event.api.Query.State.Failed;
import static org.jayware.e2.event.api.Query.State.Running;
import static org.jayware.e2.event.api.Query.State.Success;


public class QueryDispatch
extends EventDispatch
{
    private static final Logger log = LoggerFactory.getLogger(QueryDispatch.class);

    private final QueryWrapper myQuery;

    public QueryDispatch(Context context, QueryImpl query, Iterable<Subscription> subscriptions)
    {
        super(context, new QueryWrapper(query, new QueryResultSet(query)), subscriptions);
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
    public void run()
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

    @Override
    public String toString()
    {
        return "QueryDispatch { " + myQuery.toString() + " }";
    }
}
