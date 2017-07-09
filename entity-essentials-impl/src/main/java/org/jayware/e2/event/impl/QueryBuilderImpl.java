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
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.event.api.QueryBuilder;
import org.jayware.e2.event.api.QueryBuilder.QueryBuilderTo;
import org.jayware.e2.event.api.ReadOnlyParameters;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.util.Consumer;

import java.util.EnumMap;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static org.jayware.e2.event.api.Query.State.Ready;
import static org.jayware.e2.util.Preconditions.checkArgument;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.Preconditions.checkStringNotEmpty;


class QueryBuilderImpl
implements QueryBuilder, QueryBuilderTo
{
    private final Class<? extends EventType> myEventType;
    private final Parameters myEventParameters;
    private final Map<State, Consumer<ResultSet>> myResultConsumers;

    private String myLastParameter;

    private QueryBuilderImpl(Class<? extends EventType> type)
    {
        myEventType = type;
        myEventParameters = new Parameters();
        myResultConsumers = new EnumMap<State, Consumer<ResultSet>>(State.class);
    }

    static QueryBuilder createQueryBuilder(Class<? extends EventType> type)
    {
        checkNotNull(type);

        return new QueryBuilderImpl(type);
    }

    @Override
    public QueryBuilderTo set(String parameter)
    {
        checkStringNotEmpty(parameter);
        myLastParameter = parameter;
        return this;
    }

    @Override
    public QueryBuilder set(Parameter parameter)
    {
        checkNotNull(parameter);
        myEventParameters.set(parameter);
        return this;
    }

    @Override
    public QueryBuilder setAll(ReadOnlyParameters parameters)
    {
        checkNotNull(parameters);
        myEventParameters.set(parameters);
        return this;
    }

    @Override
    public QueryBuilder on(State state, Consumer<ResultSet> consumer)
    {
        checkNotNull(state);
        checkArgument(state != Ready);
        myResultConsumers.put(state, consumer);
        return this;
    }

    @Override
    public QueryBuilder to(Object value)
    {
        myEventParameters.set(myLastParameter, value);
        return this;
    }

    @Override
    public Query build()
    {
        return new QueryImpl(randomUUID(), myEventType, myEventParameters, myResultConsumers);
    }

    @Override
    public QueryBuilder reset()
    {
        myEventParameters.clear();
        myResultConsumers.clear();
        return this;
    }
}
