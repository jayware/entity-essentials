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
package org.jayware.e2.storage.impl;

import com.google.common.base.Objects;
import com.googlecode.concurentlocks.ReadWriteUpdateLock;
import com.googlecode.concurentlocks.ReentrantReadWriteUpdateLock;
import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.context.api.Disposable;
import org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityCreatedEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityDeletedEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityDeletingEvent;
import org.jayware.e2.entity.api.EntityEvent.FindEntitiesEvent;
import org.jayware.e2.entity.api.EntityEvent.ResolveEntityEvent;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.storage.api.ComponentDatabase;
import org.jayware.e2.storage.api.Storage;
import org.jayware.e2.util.Filter;
import org.jayware.e2.util.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;

import static org.jayware.e2.entity.api.EntityEvent.AspectParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefListParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEvent.FilterListParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Presence.Optional;
import static org.jayware.e2.util.Key.createKey;


public class StorageImpl
implements Storage, Disposable
{
    public static final Key<Storage> STORAGE_KEY = createKey("org.jayware.e2.Storage");

    private final Logger log = LoggerFactory.getLogger(StorageImpl.class);

    private final Context myContext;

    private final EventManager myEventManager;

    private final Map<UUID, EntityRef> myEntities;
    private final ComponentDatabase myComponentDatabase;

    private final Lock myReadLock;
    private final Lock myUpdateLock;
    private final Lock myWriteLock;

    public StorageImpl(Context context, Map<UUID, EntityRef> entities, ComponentDatabase database)
    {
        myContext = context;
        myEventManager = context.getService(EventManager.class);
        myEntities = entities;
        myComponentDatabase = database;

        final ReadWriteUpdateLock myLock = new ReentrantReadWriteUpdateLock();
        myReadLock = myLock.readLock();
        myUpdateLock = myLock.updateLock();
        myWriteLock = myLock.writeLock();
    }

    @Handle(CreateEntityEvent.class)
    public void handleCreateEntityEvent(Event event, @Param(value = EntityIdParam, presence = Optional) UUID id)
    {
        final UUID entityId = next(id);

        EntityRef resultRef;
        boolean fireEntityCreatedEvent = false;

        myUpdateLock.lock();
        try
        {
            resultRef = myEntities.get(entityId);

            if (resultRef == null)
            {
                resultRef = new EntityRefImpl(entityId);

                myWriteLock.lock();
                try
                {
                    myEntities.put(entityId, resultRef);
                }
                finally
                {
                    myWriteLock.unlock();
                }

                fireEntityCreatedEvent = true;
            }
        }
        finally
        {
            myUpdateLock.unlock();
        }

        if (fireEntityCreatedEvent)
        {
            postEntityCreatedEvent(resultRef);
        }

        if (event.isQuery())
        {
            ((Query) event).result(EntityRefParam, resultRef);
        }
    }

    @Handle(DeleteEntityEvent.class)
    public void handleDeleteEntityEvent(@Param(value = EntityIdParam) UUID id)
    {
        boolean fireEntityDeletedEvent = false;
        EntityRef ref;

        myUpdateLock.lock();
        try
        {
            ref = myEntities.get(id);

            if (ref != null)
            {
                sendEntityDeletingEvent(ref);

                myWriteLock.lock();
                try
                {
                    myComponentDatabase.clear(ref);
                    myEntities.remove(id);
                }
                finally
                {
                    myWriteLock.unlock();
                }

                fireEntityDeletedEvent = true;
            }
        }
        finally
        {
            myUpdateLock.unlock();
        }

        if (fireEntityDeletedEvent)
        {
            postEntityDeletedEvent(ref);
        }
        else
        {
            log.warn("The entity '{}' does not exist in '{}'!", id, myContext);
        }
    }

    @Handle(FindEntitiesEvent.class)
    public void handleFindEntityEvent(Query query, @Param(value = AspectParam, presence = Optional) Aspect aspect,
                                                   @Param(value = FilterListParam, presence = Optional) List<Filter<EntityRef>> filters)
    {
        final List<EntityRef> result = new CopyOnWriteArrayList<EntityRef>();

        myReadLock.lock();
        try
        {
            for (EntityRef ref : myEntities.values())
            {
                boolean addToResult = true;

                if (aspect != null)
                {
                    if (!aspect.matches(ref))
                    {
                        continue;
                    }
                }

                if (filters != null)
                {
                    for (Filter<EntityRef> filter : filters)
                    {
                        if (!filter.accepts(myContext, ref))
                        {
                            addToResult = false;
                            break;
                        }
                    }
                }

                if (addToResult)
                {
                    result.add(ref);
                }
            }
        }
        finally
        {
            myReadLock.unlock();
        }

        query.result(EntityRefListParam, Collections.<EntityRef>unmodifiableList(result));
    }

    @Handle(ResolveEntityEvent.class)
    public void handleResolveEntityEvent(Query query, @Param(EntityIdParam) UUID id)
    {
        EntityRef resolvedEntity = null;

        myReadLock.lock();
        try
        {
            resolvedEntity = myEntities.get(id);
            if (resolvedEntity == null)
            {
                resolvedEntity = new EntityRefImpl(id);
            }
        }
        finally
        {
            myReadLock.unlock();
        }

        query.result(EntityRefParam, resolvedEntity);
    }

    @Override
    public void dispose(Context context)
    {
        myEventManager.unsubscribe(myContext, this);
    }

    private void postEntityCreatedEvent(EntityRef ref)
    {
        myEventManager.post(EntityCreatedEvent.class,
            param(ContextParam, myContext),
            param(EntityRefParam, ref),
            param(EntityIdParam, ref.getId())
        );
    }

    private void postEntityDeletedEvent(EntityRef ref)
    {
        myEventManager.post(EntityDeletedEvent.class,
            param(ContextParam, myContext),
            param(EntityIdParam, ref.getId()),
            param(EntityRefParam, ref)
        );
    }

    private void sendEntityDeletingEvent(EntityRef ref)
    {
        myEventManager.send(EntityDeletingEvent.class,
            param(ContextParam, myContext),
            param(EntityRefParam, ref),
            param(EntityIdParam, ref.getId())
        );
    }

    private UUID next(UUID id)
    {
        return id != null ? id : UUID.randomUUID();
    }

    private class EntityRefImpl
    implements EntityRef
    {
        private final UUID myId;

        public EntityRefImpl(UUID id)
        {
            myId = id;
        }

        @Override
        public Context getContext()
        {
            return myContext;
        }

        @Override
        public UUID getId()
        {
            return myId;
        }

        @Override
        public boolean belongsTo(Context context)
        {
            return myContext.equals(context);
        }

        @Override
        public boolean belongsTo(Contextual contextual)
        {
            return contextual != null && myContext.equals(contextual.getContext());
        }

        @Override
        public boolean isValid()
        {
            myReadLock.lock();
            try
            {
                return !myContext.isDisposed() && myEntities.containsKey(myId);
            }
            finally
            {
                myReadLock.unlock();
            }
        }

        @Override
        public boolean isInvalid()
        {
            return !isValid();
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
            {
                return true;
            }

            if (!(other instanceof EntityRef))
            {
                return false;
            }

            final EntityRef ref = (EntityRef) other;
            return Objects.equal(myId, ref.getId()) && belongsTo(ref);
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(myContext, myId);
        }

        @Override
        public String toString()
        {
            String toString =
            "Ref { " + myId;

            if (isInvalid())
            {
                toString += " | <invalid>";
            }

            return toString + " }";
        }
    }

}
