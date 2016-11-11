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
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventDispatchException;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;


public class EventDispatch
implements Runnable
{
    private static final Logger log = LoggerFactory.getLogger(EventDispatch.class);

    protected final Context myContext;
    protected final Event myEvent;
    protected final Collection<Subscription> mySubscriptions;
    protected final CountDownLatch isDispatched;

    EventDispatch(Context context, Event event, Collection<Subscription> subscriptions)
    {
        myContext = context;
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

    public void run()
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

    @Override
    public String toString()
    {
        return "EventDispatch { " + myEvent.toString() + " }";
    }
}
