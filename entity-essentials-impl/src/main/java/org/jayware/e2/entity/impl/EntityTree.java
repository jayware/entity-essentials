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
package org.jayware.e2.entity.impl;

import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.context.api.Disposable;
import org.jayware.e2.entity.api.Entity;
import org.jayware.e2.entity.api.EntityEvent.ChildAddedEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteAllEntitiesEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityCreatedEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityDeletedEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityDeletingEvent;
import org.jayware.e2.entity.api.EntityPath;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.entity.api.InvalidEntityRefException;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.util.Filter;
import org.jayware.e2.util.Traversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Objects.equal;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.jayware.e2.entity.api.EntityEvent.ChildAddedEntityEvent.ChildRemovedEntityEvent;
import static org.jayware.e2.entity.api.EntityEvent.ChildrenEntityEvent.ChildRefParam;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityPathParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefListParam;
import static org.jayware.e2.entity.api.EntityPath.EMPTY_PATH;
import static org.jayware.e2.entity.api.EntityPath.ROOT_PATH;
import static org.jayware.e2.entity.api.EntityPath.SEPARATOR;
import static org.jayware.e2.entity.api.EntityPath.path;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Presence.Optional;


public class EntityTree
implements Disposable
{
    private final Context myContext;
    private final EventManager myEventManager;

    private final Map<UUID, EntityImpl> myEntities;
    private final EntityImpl myRoot;

    private final ReadWriteLock myLock;
    private final Lock myReadLock;
    private final Lock myWriteLock;

    protected EntityTree(Context context)
    {
        myContext = context;
        myEventManager = context.getService(EventManager.class);
        myEventManager.subscribe(context, this);

        myEntities = new HashMap<>();
        myRoot = new EntityImpl(new UUID(0, 0));
        myEntities.put(myRoot.identifier(), myRoot);

        myLock = new ReentrantReadWriteLock();
        myReadLock = myLock.readLock();
        myWriteLock = myLock.writeLock();
    }

    @Handle(CreateEntityEvent.class)
    public void createEntity(Event event,
                             @Param(ContextParam) Context context,
                             @Param(value = EntityPathParam, presence = Optional) EntityPath entityPath,
                             @Param(value = EntityIdParam, presence = Optional) UUID id)
    {
        if (!myContext.equals(context))
        {
            return;
        }

        myWriteLock.lock();
        try
        {
            if (entityPath == null)
            {
                entityPath = path("/" + UUID.randomUUID().toString());
            }

            final EntityPath path = entityPath;
            final int depth = path.depth();

            EntityImpl lastEntity = myRoot;
            EntityImpl currentEntity;
            EntityPath currentPath = ROOT_PATH;
            int count = 0;

            for (String segment : path.segments())
            {
                currentPath = currentPath.append(segment);
                currentEntity = lastEntity.get(currentPath.getName());

                if (currentEntity == null)
                {
                    if (count == depth && id != null)
                    {
                        currentEntity = new EntityImpl(id);
                    }
                    else
                    {
                        currentEntity = new EntityImpl(randomUUID());
                    }

                    lastEntity.add(currentEntity, currentPath.getName());
                    myEntities.put(currentEntity.identifier(), currentEntity);

                    myEventManager.post(EntityCreatedEvent.class,
                        param(ContextParam, myContext),
                        param(EntityPathParam, currentPath),
                        param(EntityRefParam, currentEntity.getRef()),
                        param(EntityIdParam, currentEntity.identifier())
                    );

                    myEventManager.post(ChildAddedEntityEvent.class,
                        param(ContextParam, myContext),
                        param(EntityPathParam, currentPath),
                        param(EntityRefParam, lastEntity.getRef()),
                        param(ChildRefParam, currentEntity.getRef())
                    );
                }

                lastEntity = currentEntity;
                ++count;
            }

            if (event.isQuery())
            {
                ((Query) event).result(CreateEntityEvent.EntityRefParam, lastEntity.getRef());
            }
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    @Handle(DeleteEntityEvent.class)
    public void deleteEntity(@Param(ContextParam) Context context,
                             @Param(EntityIdParam) UUID id)
    {
        if (!myContext.equals(context))
        {
            return;
        }

        myWriteLock.lock();
        try
        {
            final EntityImpl entity = myEntities.get(id);
            final EntityPath entityPath = entity.getPath();
            final EntityImpl parent = entity.getParent();

            fireEntityDeletingEvent(entity.getRef());

            parent.remove(entity.getName());
            myEntities.remove(entity.identifier());

            myEventManager.post(ChildRemovedEntityEvent.class,
                param(ContextParam, myContext),
                param(EntityPathParam, parent.getPath()),
                param(EntityRefParam, parent.getRef()),
                param(ChildRefParam, entity.getRef())
            );

            myEventManager.post(EntityDeletedEvent.class,
                param(ContextParam, myContext),
                param(EntityPathParam, entityPath),
                param(EntityRefParam, parent.getRef()),
                param(EntityIdParam, entity.identifier())
            );
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    @Handle(DeleteAllEntitiesEvent.class)
    public void deleteAllEntities(Event event, @Param(ContextParam) Context context)
    {
        if (!myContext.equals(context))
        {
            return;
        }

        final List<EntityRef> deletedEntities = new CopyOnWriteArrayList<EntityRef>();

        myWriteLock.lock();
        try
        {
            for (EntityImpl entity : new ArrayList<EntityImpl>(myEntities.values()))
            {
                final EntityRef ref = entity.getRef();
                if (!entity.getPath().equals(EntityPath.ROOT_PATH))
                {
                    fireEntityDeletingEvent(ref);
                    myEntities.remove(entity.identifier());
                    fireEntityDeletedEvent(ref);
                    deletedEntities.add(ref);
                }
            }
        }
        finally
        {
            myWriteLock.unlock();
        }

        if (event.isQuery())
        {
            ((Query) event).result(EntityRefListParam, deletedEntities);
        }
    }

    public EntityRef find(EntityPath path)
    {
        if (path.equals(EMPTY_PATH))
        {
            return null;
        }

        myReadLock.lock();
        try
        {
            EntityImpl lastEntity = myRoot;
            for (String segment : path.segments())
            {
                if (lastEntity != null)
                {
                    lastEntity = lastEntity.get(segment);
                }
            }

            if (lastEntity != null)
            {
                return lastEntity.getRef();
            }

            return null;
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    public List<EntityRef> find(Traversal traversal, Aspect aspect, Filter<EntityRef>[] filters)
    {
        return find(singletonList(myRoot), traversal, aspect, filters);
    }

    public List<EntityRef> findAncestors(EntityRef ref, Aspect aspect, Filter<EntityRef>[] filters)
    {
        final List<EntityRef> result = new ArrayList<>();

        if (ref != null)
        {
            EntityImpl entity = myEntities.get(((EntityRefImpl) ref).myIdentifier);

            entity = entity.myParent;
            while (entity != null)
            {
                ref = entity.getRef();

                if (aspect.matches(ref))
                {
                    for (Filter<EntityRef> filter : filters)
                    {
                        if (filter.accepts(myContext, ref))
                        {
                            result.add(ref);
                        }
                    }
                }

                entity = entity.myParent;
            }
        }

        return result;
    }

    public List<EntityRef> findDescendants(EntityRef ref, Traversal traversal, Aspect aspect, Filter<EntityRef>[] filters)
    {
        if (ref != null)
        {
            final EntityImpl root = myEntities.get(((EntityRefImpl) ref).myIdentifier);
            final List<EntityImpl> startSet = new ArrayList<>(root.myChildren.values());
            final List<EntityRef> results = find(startSet, traversal, aspect, filters);

            return results;
        }
        else
        {
            return new ArrayList<>();
        }
    }

    private List<EntityRef> find(List<EntityImpl> startSet, Traversal traversal, Aspect aspect, Filter<EntityRef>[] filters)
    {
        switch (traversal)
        {
            case BreadthFirstLeftToRight:
                return findBreadthFirst(startSet, aspect, filters);
            case DepthFirstPreOrder:
                return findDepthFirstPreOrder(startSet, aspect, filters);
            case Unordered:
            default:
                if (!startSet.contains(myRoot))
                {
                    return findBreadthFirst(startSet, aspect, filters);
                }
                else
                {
                    return findUnordered(aspect, filters);
                }
        }
    }

    private List<EntityRef> findBreadthFirst(List<EntityImpl> startSet, Aspect aspect, Filter<EntityRef>[] filters)
    {
        final List<EntityRef> result = new ArrayList<>();
        final Queue<EntityImpl> queue = new LinkedList<>();
        EntityImpl current;

        myReadLock.lock();
        try
        {
            queue.addAll(startSet);
            while (!queue.isEmpty())
            {
                current = queue.poll();
                queue.addAll(current.myChildren.values());

                final EntityRef ref = current.getRef();

                try
                {
                    if (aspect.matches(ref))
                    {
                        for (Filter<EntityRef> filter : filters)
                        {
                            if (filter.accepts(myContext, ref))
                            {
                                result.add(ref);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            return result;
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    private List<EntityRef> findDepthFirstPreOrder(List<EntityImpl> startSet, Aspect aspect, Filter<EntityRef>[] filters)
    {
        final List<EntityRef> result = new ArrayList<>();
        final Stack<EntityImpl> stack = new Stack<>();
        EntityImpl current;

        myReadLock.lock();
        try
        {
            stack.addAll(startSet);
            while (!stack.isEmpty())
            {
                current = stack.pop();
                stack.addAll(current.myChildren.values());

                final EntityRef ref = current.getRef();

                try
                {
                    if (aspect.matches(ref))
                    {
                        for (Filter<EntityRef> filter : filters)
                        {
                            if (filter.accepts(myContext, ref))
                            {
                                result.add(ref);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            return result;
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    private List<EntityRef> findUnordered(Aspect aspect, Filter<EntityRef>[] filters)
    {
        final List<EntityRef> result = new ArrayList<>();

        myReadLock.lock();
        try
        {
            for (EntityImpl entity : myEntities.values())
            {
                EntityRef ref = entity.getRef();
                if (aspect.matches(ref))
                {
                    for (Filter<EntityRef> filter : filters)
                    {
                        if (filter.accepts(myContext, ref))
                        {
                            result.add(ref);
                        }
                    }
                }
            }

            return result;
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    public boolean existsEntity(EntityPath path)
    {
        myReadLock.lock();
        try
        {
            return find(path) != null;
        }
        finally
        {
            myReadLock.unlock();
        }
    }

    public EntityRef resolveEntity(UUID id)
    {
        return new EntityRefImpl(id);
    }

    @Override
    public void dispose(Context context)
    {
        myWriteLock.lock();
        try
        {
            for (EntityImpl entity : myEntities.values())
            {
                entity.myParent = null;
                entity.myChildren.clear();
            }

            myEntities.clear();
        }
        finally
        {
            myWriteLock.unlock();
        }
    }

    private void fireEntityDeletingEvent(EntityRef ref)
    {
        myEventManager.send(EntityDeletingEvent.class,
            param(ContextParam, myContext),
            param(EntityPathParam, ref.getPath()),
            param(EntityRefParam, ref),
            param(EntityIdParam, ref.getId())
        );
    }

    private void fireEntityDeletedEvent(EntityRef ref)
    {
        myEventManager.post(EntityDeletedEvent.class,
            param(ContextParam, myContext),
            param(EntityRefParam, ref),
            param(EntityIdParam, ref.getId())
        );
    }

    private class EntityImpl
    implements Entity
    {
        private final EntityRefImpl myRef;

        private EntityImpl myParent = null;

        private final BiMap<String, EntityImpl> myChildren;
        private final BiMap<EntityImpl, String> myChildrenInverse;

        public EntityImpl(UUID identifier)
        {
            myRef = new EntityRefImpl(identifier);
            myChildren = HashBiMap.create();
            myChildrenInverse = myChildren.inverse();
        }

        public EntityImpl getParent()
        {
            return myParent;
        }

        public void add(EntityImpl entity, String name)
        {
            if (entity.myParent != null)
            {
                entity.myParent.myChildrenInverse.remove(entity);
            }

            entity.myParent = this;
            myChildren.put(name, entity);
        }

        public void remove(String name)
        {
            EntityImpl entity = myChildren.remove(name);

            if (entity != null)
            {
                entity.myParent = null;
            }
        }

        public EntityImpl get(String name)
        {
            if (name.equals(SEPARATOR))
            {
                return myRoot;
            }

            return myChildren.get(name);
        }

        public UUID identifier()
        {
            return myRef.myIdentifier;
        }

        public EntityRef getRef()
        {
            return myRef;
        }

        public EntityPath getPath()
        {
            if (myParent == null)
            {
                return ROOT_PATH;
            }

            EntityPath result = EMPTY_PATH;
            EntityImpl current = this;

            while (current != null)
            {
                result = result.prepend(current.getName());
                current = current.myParent;
            }

            return result;
        }

        public String getName()
        {
            if (myParent == null)
            {
                return ROOT_PATH.getName();
            }

            return myParent.myChildrenInverse.get(this);
        }

        @Override
        public String toString()
        {
            return "EntityImpl{" +
            "uuid=" + identifier() +
            ", path=" + getPath() +
            '}';
        }
    }

    private class EntityRefImpl
    implements EntityRef
    {
        private final UUID myIdentifier;

        public EntityRefImpl(UUID identifier)
        {
            myIdentifier = identifier;
        }

        @Override
        public UUID getId()
        {
            return myIdentifier;
        }

        @Override
        public EntityPath getPath()
        {
            if (isInvalid())
            {
                throw new InvalidEntityRefException(this);
            }

            myReadLock.lock();
            try
            {
                return myEntities.get(myIdentifier).getPath();
            }
            finally
            {
                myReadLock.unlock();
            }
        }

        @Override
        public Context getContext()
        {
            return myContext;
        }

        @Override
        public boolean belongsTo(Context context)
        {
            return context != null && myContext.equals(context);
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
                return !myContext.isDisposed() && myEntities.containsKey(myIdentifier);
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

        public UUID identifier()
        {
            return myIdentifier;
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
            {
                return true;
            }

            if (other == null || getClass() != other.getClass())
            {
                return false;
            }

            return equal(myIdentifier, ((EntityRefImpl) other).myIdentifier) &&
                   equal(getContext(), ((EntityRefImpl) other).getContext());
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(myIdentifier, getContext());
        }

        @Override
        public String toString()
        {
            String toString =
                "Ref { " + myIdentifier;

            if (isInvalid())
            {
                toString += " | <invalid>";
            }

            return toString + " }";
        }
    }
}