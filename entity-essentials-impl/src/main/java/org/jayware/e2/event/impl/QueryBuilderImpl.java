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

import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.event.api.QueryBuilder;
import org.jayware.e2.event.api.QueryBuilder.QueryBuilderTo;
import org.jayware.e2.event.api.Result;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.jayware.e2.event.api.Query.State.Ready;
import static org.jayware.e2.util.Preconditions.checkArgument;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.Preconditions.checkStringNotEmpty;


class QueryBuilderImpl
implements QueryBuilder, QueryBuilderTo
{
    private final Class<? extends EventType> myEventType;
    private final Parameters myEventParameters;
    private final Map<State, Consumer<Result>> myResultConsumers;

    private String myLastParameter;

    private QueryBuilderImpl(Class<? extends EventType> type)
    {
        myEventType = type;
        myEventParameters = new Parameters();
        myResultConsumers = new EnumMap<>(State.class);
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
    public QueryBuilder setAll(Parameters parameters)
    {
        checkNotNull(parameters);
        myEventParameters.set(parameters);
        return this;
    }

    @Override
    public QueryBuilder on(State state, Consumer<Result> consumer)
    {
        checkNotNull(state);
        checkArgument(() -> state != Ready);
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
        return new QueryImpl(myEventType, myEventParameters, myResultConsumers);
    }

    @Override
    public QueryBuilder reset()
    {
        myEventParameters.clear();
        myResultConsumers.clear();
        return this;
    }
}
