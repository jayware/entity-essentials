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


public class EventBuilderImpl
implements EventBuilder, EventBuilderTo
{
    private final Class<? extends EventType> myEventType;
    private final Parameters myEventParameters;

    private String myLastParameter;

    public EventBuilderImpl(Class<? extends EventType> type)
    {
        myEventType = type;
        myEventParameters = new Parameters();
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
        myLastParameter = parameter;
        return this;
    }

    @Override
    public EventBuilder set(Parameter parameter)
    {
        myEventParameters.set(parameter);
        return this;
    }

    @Override
    public EventBuilder setAll(Parameters parameters)
    {
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
        return new EventImpl(myEventType, myEventParameters);
    }
}
