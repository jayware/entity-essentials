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
package org.jayware.e2.binding.impl;


import org.jayware.e2.binding.api.Binding;
import org.jayware.e2.binding.api.ComponentBinding;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Disposable;
import org.jayware.e2.event.api.EventManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class BindingsTable
implements Disposable
{
    private final Context myContext;
    private final EventManager myEventManager;

    private final Set<Binding> myBindingsTable;

    private final ReadWriteLock myLock = new ReentrantReadWriteLock();
    private final Lock myReadLock = myLock.readLock();
    private final Lock myWriteLock = myLock.writeLock();

    public BindingsTable(Context context)
    {
        myContext = context;
        myEventManager = myContext.getEventManager();

        myBindingsTable = new HashSet<>();
    }

    public boolean addBinding(ComponentBinding binding)
    {
        boolean result = false;

        myWriteLock.lock();
        try
        {
            if (!myBindingsTable.contains(binding))
            {
                result = myBindingsTable.add(binding);
                myEventManager.subscribe(myContext, binding);
            }
        }
        finally
        {
            myWriteLock.unlock();
        }

        return result;
    }

    public boolean removeBinding(ComponentBinding binding)
    {
        myWriteLock.lock();
        try
        {
            myEventManager.unsubscribe(myContext, binding);
            return myBindingsTable.remove(binding);
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    @Override
    public void dispose(Context context)
    {
        myWriteLock.lock();
        try
        {
            for (Binding binding : myBindingsTable)
            {
                myEventManager.unsubscribe(myContext, binding);
            }

            myBindingsTable.clear();
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    Set<Binding> bindings()
    {
        return myBindingsTable;
    }

}
