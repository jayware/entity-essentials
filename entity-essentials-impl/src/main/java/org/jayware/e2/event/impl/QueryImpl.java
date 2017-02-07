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
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ReadOnlyParameters;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.util.Consumer;
import org.jayware.e2.util.Key;

import java.util.Map;
import java.util.UUID;

import static java.util.Collections.unmodifiableMap;


class QueryImpl
implements Query
{
    private final Event myEvent;
    private final Map<State, Consumer<ResultSet>> myConsumers;

    QueryImpl(UUID id, Class<? extends EventType> type, Parameters parameters, Map<State, Consumer<ResultSet>> consumers)
    {
        myEvent = new EventImpl(id, type, parameters);
        myConsumers = unmodifiableMap(consumers);
    }

    QueryImpl(UUID id, Class<? extends EventType> type, Parameter[] parameters, Map<State, Consumer<ResultSet>> consumers)
    {
        this(id, type, new Parameters(parameters), consumers);
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
    public UUID getId()
    {
        return myEvent.getId();
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

    @Override
    public boolean isNotQuery()
    {
        return false;
    }

    public Map<State, Consumer<ResultSet>> getConsumers()
    {
        return myConsumers;
    }

    @Override
    public String toString()
    {
        return "Query { " + getId() + " [ " + getType().getSimpleName() + " ]" + '}';
    }
}
