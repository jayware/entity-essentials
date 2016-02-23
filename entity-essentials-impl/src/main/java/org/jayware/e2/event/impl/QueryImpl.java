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

import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ReadOnlyParameters;
import org.jayware.e2.util.Key;


public class QueryImpl
implements Query
{
    private final Event myEvent;

    public QueryImpl(Class<? extends EventType> type, Parameters parameters)
    {
        myEvent = new EventImpl(type, parameters);
    }

    public QueryImpl(Class<? extends EventType> type, Parameters.Parameter[] parameters)
    {
        myEvent = new EventImpl(type, parameters);
    }

    @Override
    public <V> void result(String name, V value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> void result(Key<V> key, V value)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<? extends EventType> getType()
    {
        return myEvent.getType();
    }

    @Override
    public boolean matches(Class<? extends EventType> type)
    {
        return myEvent.matches(type);
    }

    @Override
    public <V> V getParameter(String parameter)
    {
        return myEvent.getParameter(parameter);
    }

    @Override
    public boolean hasParameter(String parameter)
    {
        return myEvent.hasParameter(parameter);
    }

    @Override
    public ReadOnlyParameters getParameters()
    {
        return myEvent.getParameters();
    }

    @Override
    public boolean isQuery()
    {
        return true;
    }
}
