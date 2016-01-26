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
package org.jayware.e2.context.impl;

import org.jayware.e2.assembly.api.AssemblyManager;
import org.jayware.e2.binding.api.BindingManager;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Disposable;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.template.api.TemplateManager;
import org.jayware.e2.util.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.jayware.e2.util.Preconditions.checkNotNull;


public class ContextImpl
implements Context
{
    private Context myContextState;

    private final ReadWriteLock myLock = new ReentrantReadWriteLock();
    private final Lock myReadLock = myLock.readLock();
    private final Lock myWriteLock = myLock.writeLock();

    public ContextImpl(EntityManager entityManager, ComponentManager componentManager, TemplateManager templateManager, EventManager eventManager, AssemblyManager assemblyManager, BindingManager bindingManager)
    {
        myContextState = new DefaultContext(entityManager, componentManager, templateManager, eventManager, assemblyManager, bindingManager);
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    @Override
    public void dispose()
    {
        myWriteLock.lock();
        try
        {
            myContextState.dispose();
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    @Override
    public boolean isDisposed()
    {
        myReadLock.lock();
        try
        {
            return myContextState.isDisposed();
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public <T> void put(Key<T> key, T value)
    {
        myWriteLock.lock();
        try
        {
            myContextState.put(key, value);
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    @Override
    public <T> boolean putIfAbsent(Key<T> key, T value)
    {
        myWriteLock.lock();
        try
        {
            return myContextState.putIfAbsent(key, value);
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    @Override
    public <T> boolean putIfAbsent(Key<T> key, ValueProvider<T> valueProvider)
    {
        myWriteLock.lock();
        try
        {
            return myContextState.putIfAbsent(key, valueProvider);
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    @Override
    public <T> void remove(Key<T> key)
    {
        myWriteLock.lock();
        try
        {
            myContextState.remove(key);
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    @Override
    public <T> T get(Key<T> key)
    {
        myReadLock.lock();
        try
        {
            return myContextState.get(key);
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public <T> T get(Key<T> key, T defaultValue)
    {
        myReadLock.lock();
        try
        {
            return myContextState.get(key, defaultValue);
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public boolean contains(Key key)
    {
        myReadLock.lock();
        try
        {
            return myContextState.contains(key);
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public EntityManager getEntityManager()
    {
        myReadLock.lock();
        try
        {
            return myContextState.getEntityManager();
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public ComponentManager getComponentManager()
    {
        myReadLock.lock();
        try
        {
            return myContextState.getComponentManager();
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public BindingManager getBindingManager()
    throws IllegalStateException
    {
        myReadLock.lock();
        try
        {
            return myContextState.getBindingManager();
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public TemplateManager getTemplateManager()
    {
        myReadLock.lock();
        try
        {
            return myContextState.getTemplateManager();
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public EventManager getEventManager()
    {
        myReadLock.lock();
        try
        {
            return myContextState.getEventManager();
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    @Override
    public AssemblyManager getAssemblyManager()
    {
        myReadLock.lock();
        try
        {
            return myContextState.getAssemblyManager();
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    private class DefaultContext
    implements Context
    {
        private final EventManager myEventManager;
        private final AssemblyManager myAssemblyManager;
        private final EntityManager myEntityManager;
        private final ComponentManager myComponentManager;
        private final BindingManager myBindingManager;
        private final TemplateManager myTemplateManager;

        private final Map<Key, Object> myMap;

        public DefaultContext(EntityManager entityManager, ComponentManager componentManager, TemplateManager templateManager, EventManager eventManager, AssemblyManager assemblyManager, BindingManager bindingManager)
        {
            myEntityManager = entityManager;
            myComponentManager = componentManager;
            myTemplateManager = templateManager;
            myEventManager = eventManager;
            myAssemblyManager = assemblyManager;
            myBindingManager = bindingManager;

            myMap = new HashMap<>();
        }

        @Override
        public void dispose()
        {
            for (Object obj : myMap.values())
            {
                if (obj instanceof Disposable)
                {
                    ((Disposable) obj).dispose(ContextImpl.this);
                }
            }

            myMap.clear();
            myContextState = new DisposedContext();
        }

        @Override
        public boolean isDisposed()
        {
            return false;
        }

        @Override
        public <T> void put(Key<T> key, T value)
        {
            checkNotNull(key, "Key mustn't be null!");
            myMap.put(key, value);
        }

        @Override
        public <T> boolean putIfAbsent(Key<T> key, T value)
        {
            checkNotNull(key, "Key mustn't be null!");

            if (!myMap.containsKey(key))
            {
                myMap.put(key, value);
                return true;
            }

            return false;
        }

        @Override
        public <T> boolean putIfAbsent(Key<T> key, ValueProvider<T> valueProvider)
        {
            checkNotNull(key, "Key mustn't be null!");
            checkNotNull(valueProvider, "ValueProvider mustn't be null!");

            if (!myMap.containsKey(key))
            {
                myMap.put(key, valueProvider.provide(ContextImpl.this));
                return true;
            }

            return false;
        }

        @Override
        public <T> void remove(Key<T> key)
        {
            checkNotNull(key, "Key mustn't be null!");
            myMap.remove(key);
        }

        @Override
        public <T> T get(Key<T> key)
        {
            checkNotNull(key, "Key mustn't be null!");
            return (T) myMap.get(key);
        }

        @Override
        public <T> T get(Key<T> key, T defaultValue)
        {
            checkNotNull(key, "Key mustn't be null!");
            return (T) myMap.getOrDefault(key, defaultValue);
        }

        @Override
        public boolean contains(Key key)
        {
            return myMap.containsKey(key);
        }

        @Override
        public EntityManager getEntityManager()
        {
            return myEntityManager;
        }

        @Override
        public ComponentManager getComponentManager()
        {
            return myComponentManager;
        }

        @Override
        public BindingManager getBindingManager()
        {
            return myBindingManager;
        }

        @Override
        public TemplateManager getTemplateManager()
        {
            return myTemplateManager;
        }

        @Override
        public EventManager getEventManager()
        {
            return myEventManager;
        }

        @Override
        public AssemblyManager getAssemblyManager()
        {
            return myAssemblyManager;
        }
    }

    private class DisposedContext
    implements Context
    {
        @Override
        public void dispose()
        {
        }

        @Override
        public boolean isDisposed()
        {
            return true;
        }

        @Override
        public <T> void put(Key<T> key, T value)
        {
            throw new IllegalStateException("Context is disposed!");
        }

        @Override
        public <T> boolean putIfAbsent(Key<T> key, T value)
        {
            throw new IllegalStateException("Context is disposed!");
        }

        @Override
        public <T> boolean putIfAbsent(Key<T> key, ValueProvider<T> valueProvider)
        {
            throw new IllegalStateException("Context is disposed!");
        }

        @Override
        public <T> void remove(Key<T> key)
        {
            throw new IllegalStateException("Context is disposed!");
        }

        @Override
        public <T> T get(Key<T> key)
        {
            throw new IllegalStateException("Context is disposed!");
        }

        @Override
        public <T> T get(Key<T> key, T defaultValue)
        {
            throw new IllegalStateException("Context is disposed!");
        }

        @Override
        public boolean contains(Key key)
        {
            throw new IllegalStateException("Context is disposed!");
        }

        @Override
        public EntityManager getEntityManager()
        {
            throw new IllegalStateException("No EntityManager available. Context is disposed!");
        }

        @Override
        public ComponentManager getComponentManager()
        {
            throw new IllegalStateException("No ComponentManager available. Context is disposed!");
        }

        @Override
        public BindingManager getBindingManager()
        throws IllegalStateException
        {
            throw new IllegalStateException("No BindingManager available. Context is disposed!");
        }

        @Override
        public TemplateManager getTemplateManager()
        {
            throw new IllegalStateException("No TemplateManager available. Context is disposed!");
        }

        @Override
        public EventManager getEventManager()
        {
            throw new IllegalStateException("No EventManager available. Context is disposed!");
        }

        @Override
        public AssemblyManager getAssemblyManager()
        {
            throw new IllegalStateException("No AssemblyManager available. Context is disposed!");
        }
    }

    private class ShutdownHook
    extends Thread
    {
        @Override
        public void run()
        {
            dispose();
        }
    }
}