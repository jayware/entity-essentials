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

import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ReadOnlyParameters;
import org.jayware.e2.util.Key;

import java.util.UUID;


public class QueryWrapper
implements Query
{
    private final Query myQuery;
    private final QueryResultSet myResult;

    QueryWrapper(Query query, QueryResultSet result)
    {
        myQuery = query;
        myResult = result;
    }

    @Override
    public <V> void result(String name, V value)
    {
        myResult.put(name, value);
    }

    @Override
    public <V> void result(Key<V> key, V value)
    {
        myResult.put(key, value);
    }

    @Override
    public UUID getId()
    {
        return myQuery.getId();
    }

    @Override
    public Class<? extends EventType> getType()
    {
        return myQuery.getType();
    }

    @Override
    public boolean matches(Class<? extends EventType> type)
    {
        return myQuery.matches(type);
    }

    @Override
    public <V> V getParameter(String parameter)
    {
        return myQuery.getParameter(parameter);
    }

    @Override
    public boolean hasParameter(String parameter)
    {
        return myQuery.hasParameter(parameter);
    }

    @Override
    public ReadOnlyParameters getParameters()
    {
        return myQuery.getParameters();
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

    public QueryResultSet getResult()
    {
        return myResult;
    }
}
