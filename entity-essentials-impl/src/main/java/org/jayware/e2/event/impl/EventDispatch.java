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
