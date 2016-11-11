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

import org.jayware.e2.event.api.MissingResultException;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.Result;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.util.Consumer;
import org.jayware.e2.util.Key;
import org.jayware.e2.util.StateLatch;
import org.jayware.e2.util.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jayware.e2.event.api.Query.State.Success;
import static org.jayware.e2.util.NotationUtil.shortNotationOf;


public class QueryResultSet
implements ResultSet
{
    private static final Logger log = LoggerFactory.getLogger(QueryResultSet.class);

    private final Query myQuery;

    private final StateLatch<Query.State> myStateLatch;

    private final Map<Object, Object> myResultMap;
    private final Map<Query.State, Consumer<ResultSet>> myConsumers;

    QueryResultSet(QueryImpl query)
    {
        myQuery = query;

        myStateLatch = new StateLatch<Query.State>(Query.State.class);
        myResultMap = new ConcurrentHashMap<Object, Object>();
        myConsumers = query.getConsumers();
    }

    @Override
    public Query getQuery()
    {
        return myQuery;
    }

    @Override
    public boolean await(Query.State state)
    {
        return myStateLatch.await(state);
    }

    @Override
    public boolean await(Query.State state, long time, TimeUnit unit)
    {
        return myStateLatch.await(state, time, unit);
    }

    @Override
    public void timeout(Query.State state, long timeInMilliseconds)
    {
        timeout(state, timeInMilliseconds, MILLISECONDS);
    }

    @Override
    public void timeout(Query.State state, long time, TimeUnit unit)
    {
        timeout(state, time, unit, "Query (%s) did not reach state: '" + state + "' within %s%s", myQuery.getType().getSimpleName(), time, shortNotationOf(unit));
    }

    @Override
    public void timeout(Query.State state, long timeInMilliseconds, String message, Object... args)
    {
        timeout(state, timeInMilliseconds, MILLISECONDS, message, args);
    }

    @Override
    public void timeout(Query.State state, long time, TimeUnit unit, String message, Object... args)
    {
        if (!await(state, time, unit))
        {
            throw new TimeoutException(message, args);
        }
    }

    @Override
    public boolean hasStatus(Query.State state)
    {
        return myStateLatch.hasState(state);
    }

    @Override
    public boolean hasResult()
    {
        return myStateLatch.hasState(Success);
    }

    public void put(Object key, Object value)
    {
        myResultMap.put(key, value);
    }

    @Override
    public <V> V get(String name)
    {
        final V value = find(name);

        if (value == null)
        {
            throw new MissingResultException("ResultSet does not contain a value associated to the name: '" + name + "'", this);
        }

        return value;
    }

    @Override
    public <V> V get(Key<V> key)
    {
        final V value = find(key);

        if (value == null)
        {
            throw new MissingResultException("ResultSet does not contain a value associated to the key: '" + key + "'", this);
        }

        return value;
    }

    @Override
    public <V> V find(String name)
    {
        await(Success);
        return (V) myResultMap.get(name);
    }

    @Override
    public <V> V find(Key<V> key)
    {
        await(Success);
        return (V) myResultMap.get(key);
    }

    @Override
    public boolean has(String name)
    {
        return myResultMap.containsKey(name);
    }

    @Override
    public boolean has(Key<?> key)
    {
        return myResultMap.containsKey(key);
    }

    @Override
    public <T> Result<T> resultOf(Key<T> key)
    {
        return new KeyQueryResult<T>(key);
    }

    @Override
    public <T> Result<T> resultOf(String name)
    {
        return new NameQueryResult<T>(name);
    }

    public void signal(Query.State state)
    {
        myStateLatch.signal(state);

        try
        {
            final Consumer<ResultSet> consumer = myConsumers.get(state);
            if (consumer != null)
            {
                consumer.accept(this);
            }
        }
        catch (Exception e)
        {
            log.error("Failed to signal query state-change!", e);
        }
    }

    private abstract class AbstractQueryResult<T>
    implements Result<T>
    {
        @Override
        public Query getQuery()
        {
            return myQuery;
        }

        @Override
        public boolean await(Query.State state)
        {
            return QueryResultSet.this.await(state);
        }

        @Override
        public boolean await(Query.State state, long time, TimeUnit unit)
        {
            return QueryResultSet.this.await(state, time, unit);
        }

        @Override
        public boolean hasStatus(Query.State state)
        {
            return QueryResultSet.this.hasStatus(state);
        }
    }

    private class KeyQueryResult<T>
    extends AbstractQueryResult<T>
    {
        private final Key<T> myKey;

        private KeyQueryResult(Key<T> key)
        {
            myKey = key;
        }

        @Override
        public boolean hasResult()
        {
            return QueryResultSet.this.has(myKey);
        }

        @Override
        public T get()
        {
            return QueryResultSet.this.get(myKey);
        }

        @Override
        public T find()
        {
            return QueryResultSet.this.find(myKey);
        }
    }

    private class NameQueryResult<T>
    extends AbstractQueryResult<T>
    {
        private final String myName;

        private NameQueryResult(String name)
        {
            myName = name;
        }

        @Override
        public boolean hasResult()
        {
            return QueryResultSet.this.has(myName);
        }

        @Override
        public T get()
        {
            return QueryResultSet.this.get(myName);
        }

        @Override
        public T find()
        {
            return QueryResultSet.this.find(myName);
        }
    }
}
