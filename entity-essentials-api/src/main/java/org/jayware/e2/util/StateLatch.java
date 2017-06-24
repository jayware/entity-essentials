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
package org.jayware.e2.util;


import java.util.EnumMap;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.currentThread;
import static org.jayware.e2.util.Preconditions.checkNotNull;


public class StateLatch<S extends Enum<S>>
{
    private final Lock myLock = new ReentrantLock();

    private final Class<S> myStateType;
    private final EnumMap<S, Condition> myConditions;

    private S myCurrentState;

    public StateLatch(Class<S> type)
    {
        this(checkNotNull(type), type.getEnumConstants()[0]);
    }

    public StateLatch(Class<S> type, S initial)
    {
        checkNotNull(type);
        checkNotNull(initial);

        myStateType = type;
        myCurrentState = initial;

        myConditions = new EnumMap<S, Condition>(type);
        for (S constant : type.getEnumConstants())
        {
            myConditions.put(constant, myLock.newCondition());
        }
    }

    public boolean await(S state)
    {
        checkNotNull(state);

        myLock.lock();
        try
        {
            while (myCurrentState.compareTo(state) < 0)
            {
                myConditions.get(state).await();
            }

            return myCurrentState == state;
        }
        catch (InterruptedException e)
        {
            currentThread().interrupt();
            return false;
        }
        finally
        {
            myLock.unlock();
        }
    }

    public boolean await(S state, long time, TimeUnit unit)
    {
        checkNotNull(state);

        myLock.lock();
        try
        {
            if (myCurrentState.compareTo(state) < 0)
            {
                boolean elapsed = myConditions.get(state).await(time, unit);
                return myCurrentState == state || elapsed;
            }

            return true;
        }
        catch (InterruptedException e)
        {
            currentThread().interrupt();
            return false;
        }
        finally
        {
            myLock.unlock();
        }
    }

    public void signal(S first)
    {
        checkNotNull(first);

        final S[] constants = myStateType.getEnumConstants();
        final EnumSet<S> states = EnumSet.noneOf(myStateType);

        for (S constant : constants)
        {
            if (constant.compareTo(first) <= 0)
            {
                states.add(constant);
            }
        }

        myLock.lock();
        try
        {
            myCurrentState = first;

            for (S  state : states)
            {
                myConditions.get(state).signalAll();
            }
        }
        finally
        {
            myLock.unlock();
        }
    }

    public S getState()
    {
        myLock.lock();
        try
        {
            return myCurrentState;
        }
        finally
        {
            myLock.unlock();
        }
    }

    public boolean hasState(S state)
    {
        myLock.lock();
        try
        {
            return myCurrentState == state;
        }
        finally
        {
            myLock.unlock();
        }
    }

    @Override
    public String toString()
    {
        return "StateLatch{" +
        "type=" + myStateType +
        ", current-state=" + myCurrentState +
        '}';
    }
}
