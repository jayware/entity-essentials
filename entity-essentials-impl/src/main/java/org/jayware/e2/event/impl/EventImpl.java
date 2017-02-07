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
