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

import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;


public class SubscriptionImpl_StrongReference
implements Subscription
{
    private final Object mySubscriber;
    private final EventDispatcher myEventDispatcher;
    private final EventFilter[] myFilters;

    private final AtomicBoolean myIsValid;

    public SubscriptionImpl_StrongReference(Object subscriber, EventDispatcher eventDispatcher, EventFilter[] filters)
    {
        mySubscriber = subscriber;
        myEventDispatcher = eventDispatcher;
        myFilters = filters != null ? filters : new EventFilter[0];
        myIsValid = new AtomicBoolean(true);
    }

    @Override
    public Object getSubscriber()
    {
        return mySubscriber;
    }

    @Override
    public EventFilter[] getFilters()
    {
        return myFilters;
    }

    public EventDispatcher getEventDispatcher()
    {
        return myEventDispatcher;
    }

    @Override
    public void invalidate()
    {
        myIsValid.set(false);
    }

    @Override
    public boolean isValid()
    {
        return myIsValid.get();
    }
}