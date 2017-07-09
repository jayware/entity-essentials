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

import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.ReadOnlyParameters;

import java.util.UUID;

import static org.jayware.e2.event.api.Parameters.Parameter;


class EventImpl
implements Event
{
    private final UUID myId;
    private final Class<? extends EventType> myType;
    private final Parameters myParameters;

    EventImpl(UUID id, Class<? extends EventType> type, Parameters parameters)
    {
        myId = id;
        myType = type;
        myParameters = new Parameters(parameters);
    }

    EventImpl(UUID id,Class<? extends EventType> type, Parameter[] parameters)
    {
        this(id, type, new Parameters(parameters));
    }

    @Override
    public UUID getId()
    {
        return myId;
    }

    @Override
    public Class<? extends EventType> getType()
    {
        return myType;
    }

    @Override
    public boolean matches(Class<? extends EventType> type)
    {
        return type.isAssignableFrom(myType);
    }

    public <V> V getParameter(String parameter)
    {
        return (V) myParameters.get(parameter);
    }

    @Override
    public boolean hasParameter(String parameter)
    {
        return myParameters.contains(parameter);
    }

    public ReadOnlyParameters getParameters()
    {
        return myParameters;
    }

    @Override
    public boolean isQuery()
    {
        return false;
    }

    @Override
    public boolean isNotQuery()
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "Event { " + myId + " [ " + myType.getSimpleName() + " ]" + '}';
    }
}
