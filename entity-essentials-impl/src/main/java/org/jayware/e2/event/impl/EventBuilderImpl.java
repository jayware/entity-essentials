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

import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventBuilder;
import org.jayware.e2.event.api.EventBuilder.EventBuilderTo;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.ReadOnlyParameters;

import static java.util.UUID.randomUUID;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.Preconditions.checkStringNotEmpty;


class EventBuilderImpl
implements EventBuilder, EventBuilderTo
{
    private final Class<? extends EventType> myEventType;
    private final Parameters myEventParameters;

    private String myLastParameter;

    private EventBuilderImpl(Class<? extends EventType> type)
    {
        myEventType = type;
        myEventParameters = new Parameters();
    }

    static EventBuilder createEventBuilder(Class<? extends EventType> type)
    {
        checkNotNull(type);

        return new EventBuilderImpl(type);
    }

    @Override
    public EventBuilder reset()
    {
        myEventParameters.clear();
        return this;
    }

    @Override
    public EventBuilderTo set(String parameter)
    {
        checkStringNotEmpty(parameter);
        myLastParameter = parameter;
        return this;
    }

    @Override
    public EventBuilder set(Parameter parameter)
    {
        checkNotNull(parameter);
        myEventParameters.set(parameter);
        return this;
    }

    @Override
    public EventBuilder setAll(ReadOnlyParameters parameters)
    {
        checkNotNull(parameters);
        myEventParameters.set(parameters);
        return this;
    }

    @Override
    public EventBuilder to(Object value)
    {
        myEventParameters.set(myLastParameter, value);
        return this;
    }

    @Override
    public Event build()
    {
        return new EventImpl(randomUUID(), myEventType, myEventParameters);
    }
}
