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
package org.jayware.e2.interest.impl;

import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent;
import org.jayware.e2.entity.api.EntityPath;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.interest.api.Interest;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityPathParam;


public class InterestImpl
implements Interest
{
    private final Context myContext;

    private final EventManager myEventManager;

    private final Set<EntityRef> myInterestSet;
    private final Set<Object> mySubscriberSet;

    private final AtomicBoolean myIsEnabled;

    private final InterestFilter myFilter;

    public InterestImpl(Context context)
    {
        myContext = context;
        myEventManager = context.getEventManager();
        myInterestSet = new CopyOnWriteArraySet<>();
        mySubscriberSet = new CopyOnWriteArraySet<>();
        myIsEnabled = new AtomicBoolean(false);
        myFilter = new InterestFilter();
    }

    @Override
    public Context getContext()
    {
        return myContext;
    }

    @Override
    public void add(EntityRef ref)
    {
        myInterestSet.add(ref);
    }

    @Override
    public void remove(EntityRef ref)
    {
        myInterestSet.remove(ref);
    }

    @Override
    public void subscribe(Object subscriber)
    {
        mySubscriberSet.add(subscriber);
    }

    @Override
    public void unsubscribe(Object subscriber)
    {
        mySubscriberSet.remove(subscriber);
    }

    @Override
    public void enable()
    {
        for (Object subscriber : mySubscriberSet)
        {
            myEventManager.subscribe(myContext, subscriber, myFilter);
        }

        myIsEnabled.set(true);
    }

    @Override
    public void disable()
    {
        for (Object subscriber : mySubscriberSet)
        {
            myEventManager.unsubscribe(myContext, subscriber);
        }

        myIsEnabled.set(false);
    }

    @Override
    public void setEnabled(boolean enable)
    {
        if (enable)
        {
            enable();
        }
        else
        {
            disable();
        }
    }

    @Override
    public void setDisabled(boolean disabled)
    {
        setEnabled(!disabled);
    }

    @Override
    public boolean isEnabled()
    {
        return myIsEnabled.get();
    }

    @Override
    public boolean isDisabled()
    {
        return !isEnabled();
    }

    private class InterestFilter
    implements EventFilter
    {
        @Override
        public boolean accepts(Context context, Event event)
        {
            if (myIsEnabled.get() && myContext.equals(context))
            {
                if (event.matches(EntityChangedEvent.class))
                {
                    final EntityRef ref = event.getParameter(EntityRefParam);
                    return myInterestSet.contains(ref);
                }

                if (event.matches(EntityEvent.class))
                {
                    final EntityPath path = event.getParameter(EntityPathParam);
                    for (EntityRef ref : myInterestSet)
                    {
                        if (ref.isValid() && ref.getPath().equals(path))
                        {
                            return true;
                        }
                    }
                }
            }

            return false;
        }
    }
}
