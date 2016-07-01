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
package org.jayware.e2.util;


import java.util.EnumMap;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
