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
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.event.api.Subscription;

import static org.jayware.e2.event.api.EventDispatchException.throwEventDispatchExceptionWithReport;
import static org.jayware.e2.event.api.Query.State.Failed;
import static org.jayware.e2.event.api.Query.State.Running;
import static org.jayware.e2.event.api.Query.State.Success;


public class QueryDispatch
extends EventDispatch
{
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
                if (!queryFailed)
                {
                    queryFailed = runQueryDispatch(subscription.getEventDispatcher(), subscription.getSubscriber(), subscription.getFilters());
                }
                else
                {
                    runQueryDispatch(subscription.getEventDispatcher(), subscription.getSubscriber(), subscription.getFilters());
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

    private boolean runQueryDispatch(final EventDispatcher dispatcher, final Object subscriber, final EventFilter[] filters)
    {
        if (acceptedEvent(dispatcher) && passedFilters(filters))
        {
            try
            {
                dispatcher.dispatch(myQuery, subscriber);
            }
            catch (Exception cause)
            {
                throwEventDispatchExceptionWithReport(cause, myEvent, "Failed to dispatch event to: %s", subscriber);
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString()
    {
        return "QueryDispatch { " + myQuery.toString() + " }";
    }
}
