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
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.currentThread;
import static org.jayware.e2.event.api.EventDispatchException.throwEventDispatchException;
import static org.jayware.e2.event.api.EventDispatchException.throwEventDispatchExceptionWithReport;


public class EventDispatch
implements Runnable
{
    private static final Logger log = LoggerFactory.getLogger(EventDispatch.class);

    protected final Context myContext;
    protected final Event myEvent;
    protected final Iterable<Subscription> mySubscriptions;
    protected final CountDownLatch isDispatched;

    EventDispatch(Context context, Event event, Iterable<Subscription> subscriptions)
    {
        myContext = context;
        myEvent = event;
        mySubscriptions = subscriptions;
        isDispatched = new CountDownLatch(1);
    }

    public Event getEvent()
    {
        return myEvent;
    }

    public Iterable<Subscription> getSubscriptions()
    {
        return mySubscriptions;
    }

    public void await()
    {
        try
        {
            isDispatched.await();
        }
        catch (InterruptedException ignored)
        {
            currentThread().interrupt();
        }
    }

    public void run()
    {
        try
        {
            for (Subscription subscription : mySubscriptions)
            {
                try
                {
                    runEventDispatch(subscription.getEventDispatcher(), subscription.getSubscriber(), subscription.getFilters());
                }
                catch (Exception e)
                {
                    log.error("Failed to dispatch event!", e);
                }
            }
        }
        finally
        {
            isDispatched.countDown();
        }
    }

    protected void runEventDispatch(final EventDispatcher dispatcher, final Object subscriber, final EventFilter[] filters)
    {
        if (acceptedEvent(dispatcher) && passedFilters(filters))
        {
            try
            {
                dispatcher.dispatch(myEvent, subscriber);
            }
            catch (Exception cause)
            {
                throwEventDispatchExceptionWithReport(cause, myEvent, "Failed to dispatch event to: %s", subscriber);
            }
        }
    }

    protected boolean acceptedEvent(final EventDispatcher dispatcher)
    {
        return dispatcher.accepts(myEvent.getType());
    }

    protected boolean passedFilters(final EventFilter[] filters)
    {
        for (int index = 0; index < filters.length; ++index)
        {
            final EventFilter filter = filters[index];

            try
            {
                if (!filter.accepts(myContext, myEvent))
                {
                    return false;
                }
            }
            catch (Exception cause)
            {
                throwEventDispatchException(cause, "Failed to apply filter #%s: %s", index + 1, filter);
            }
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "EventDispatch { " + myEvent.toString() + " }";
    }
}
