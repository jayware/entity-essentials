/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2016 Elmar Schug <elmar.schug@jayware.org>,
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
import org.jayware.e2.event.api.EventDispatchException;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.event.api.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static org.jayware.e2.event.api.Query.State.Failed;
import static org.jayware.e2.event.api.Query.State.Running;
import static org.jayware.e2.event.api.Query.State.Success;


public class QueryDispatch
extends EventDispatch
{
    private static final Logger log = LoggerFactory.getLogger(QueryDispatch.class);

    private final QueryWrapper myQuery;

    public QueryDispatch(Context context, QueryImpl query, Collection<Subscription> subscriptions)
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
