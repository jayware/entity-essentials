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


import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntityEvent;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityNotFoundException;
import org.jayware.e2.entity.api.EntityPath;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventBuilder;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.util.Filter;
import org.jayware.e2.util.Key;
import org.jayware.e2.util.Traversal;

import java.util.List;

import static org.jayware.e2.component.api.Aspect.ANY;
import static org.jayware.e2.entity.api.EntityEvent.EntityPathParam;
import static org.jayware.e2.entity.api.EntityPath.EMPTY_PATH;
import static org.jayware.e2.entity.api.EntityPath.ROOT_PATH;
import static org.jayware.e2.entity.api.EntityPathFilter.ALL;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.Traversal.Unordered;


public class EntityManagerImpl
implements EntityManager
{
    private static final Key<EntityTree> ENTITY_TREE = Key.createKey("org.jayware.e2.EntityTree");

    private static final Context.ValueProvider<EntityTree> ENTITY_TREE_VALUE_PROVIDER = new Context.ValueProvider<EntityTree>()
    {
        @Override
        public EntityTree provide(Context context)
        {
            final EntityTree entityTree = new EntityTree(context);
            context.getEventManager().subscribe(context, entityTree);
            return entityTree;
        }
    };

    @Override
    public EntityRef createEntity(Context context, EntityPath path)
    throws IllegalArgumentException
    {
        checkNotNull(context);
        checkNotNull(path);

        if (path.isRelative())
        {
            throw new IllegalArgumentException();
        }

        final EventManager eventManager = context.getEventManager();
        getOrCreateEntityTree(context);

        final EventBuilder eventBuilder = eventManager.createEvent(CreateEntityEvent.class);
        eventBuilder.set(param(ContextParam, context));

        EntityPath segmentedPath = ROOT_PATH;
        for (String segment : path.segments())
        {
            segmentedPath = segmentedPath.append(segment);
            if (!existsEntity(context, segmentedPath))
            {
                eventBuilder.set(param(EntityPathParam, segmentedPath));
                eventManager.send(eventBuilder);
            }
        }

        return getEntity(context, path);
    }

    @Override
    public EntityRef createEntity(EntityRef parentRef, EntityPath path)
    throws IllegalArgumentException
    {
        checkNotNull(parentRef);
        checkNotNull(path);

        return createEntity(parentRef.getContext(), parentRef.getPath().resolve(path));
    }

    @Override
    public void deleteEntity(EntityRef ref)
    {
        checkNotNull(ref);

        if (ref.isInvalid())
        {
            return;
        }

        if (ref.getPath().equals(ROOT_PATH))
        {
            throw new IllegalArgumentException("It is prohibited to delete the root entity!");
        }

        final Context context = ref.getContext();
        final EventManager eventManager = context.getEventManager();

        eventManager.send(DeleteEntityEvent.class, param(ContextParam, context), param(EntityPathParam, ref.getPath()));
    }

    @Override
    public void moveEntity(EntityRef entity, EntityRef destination)
    throws IllegalContextException
    {
        // TODO: Implementation: EntityManagerImpl.moveEntityTo()
        throw new UnsupportedOperationException("EntityManagerImpl.moveEntity()");
    }

    @Override
    public EntityRef getEntity(Context context, EntityPath path)
    throws EntityNotFoundException, IllegalArgumentException
    {
        checkNotNull(context);
        checkNotNull(path);

        if (path.equals(EMPTY_PATH))
        {
            throw new IllegalArgumentException("EntityPath mustn't be empty!");
        }

        final EntityRef result = findEntity(context, path);

        if (result == null)
        {
            throw new EntityNotFoundException(path);
        }

        return result;
    }

    @Override
    public EntityRef findEntity(Context context, EntityPath path)
    throws IllegalArgumentException
    {
        checkNotNull(context);
        checkNotNull(path);

        final EntityTree entityTree = getOrCreateEntityTree(context);
        return entityTree != null ? entityTree.find(path) : null;
    }

    @Override
    public List<EntityRef> findEntities(Context context)
    {
        return findEntities(context, Unordered, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Aspect aspect)
    {
        return findEntities(context, Unordered, aspect, ALL);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Filter<EntityRef>... filters)
    {
        return findEntities(context, Unordered, ANY, filters);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Aspect aspect, Filter<EntityRef>... filters)
    {
        return findEntities(context, Unordered, aspect, filters);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Traversal traversal)
    {
        return findEntities(context, traversal, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Traversal traversal, Aspect aspect)
    {
        return findEntities(context, traversal, aspect, ALL);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Traversal traversal, Filter<EntityRef>... filters)
    {
        return findEntities(context, traversal, ANY, filters);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Traversal traversal, Aspect aspect, Filter<EntityRef>... filters)
    {
        checkNotNull(context);
        checkNotNull(traversal);
        checkNotNull(aspect);

        filters = filters != null ? filters : new Filter[0];

        final EntityTree entityTree = getOrCreateEntityTree(context);
        return entityTree.find(traversal, aspect, filters);
    }

    @Override
    public List<EntityRef> findEntityAncestors(Context context, EntityPath path)
    {
        return findEntityAncestors(context, path, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntityAncestors(Context context, EntityPath path, Aspect aspect)
    {
        return findEntityAncestors(context, path, aspect, ALL);
    }

    @Override
    public List<EntityRef> findEntityAncestors(Context context, EntityPath path, Filter<EntityRef>... filters)
    {
        return findEntityAncestors(context, path, ANY, filters);
    }

    @Override
    public List<EntityRef> findEntityAncestors(Context context, EntityPath path, Aspect aspect, Filter<EntityRef>... filters)
    {
        checkNotNull(context);
        checkNotNull(path);
        checkNotNull(aspect);

        filters = filters != null ? filters : new Filter[0];

        final EntityTree entityTree = getOrCreateEntityTree(context);
        return entityTree.findAncestors(entityTree.find(path), aspect, filters);
    }

    @Override
    public List<EntityRef> findEntityAncestors(EntityRef ref)
    {
        return findEntityAncestors(ref, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntityAncestors(EntityRef ref, Aspect aspect)
    {
        return findEntityAncestors(ref, aspect, ALL);
    }

    @Override
    public List<EntityRef> findEntityAncestors(EntityRef ref, Filter<EntityRef>... filters)
    {
        return findEntityAncestors(ref, ANY, filters);
    }

    @Override
    public List<EntityRef> findEntityAncestors(EntityRef ref, Aspect aspect, Filter<EntityRef>... filters)
    {
        checkNotNull(ref);
        checkNotNull(aspect);

        filters = filters != null ? filters : new Filter[0];

        final EntityTree entityTree = getOrCreateEntityTree(ref.getContext());
        return entityTree.findAncestors(ref, aspect, filters);
    }

    @Override
    public List<EntityRef> findEntityDescendants(Context context, EntityPath path)
    {
        return findEntityDescendants(context, path, Unordered, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(Context context, EntityPath path, Aspect aspect)
    {
        return findEntityDescendants(context, path, Unordered, aspect, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(Context context, EntityPath path, Traversal traversal)
    {
        return findEntityDescendants(context, path, traversal, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(Context context, EntityPath path, Filter<EntityRef>... filters)
    {
        return findEntityDescendants(context, path, Unordered, ANY, filters);
    }

    @Override
    public List<EntityRef> findEntityDescendants(Context context, EntityPath path, Aspect aspect, Filter<EntityRef>... filters)
    {
        return findEntityDescendants(context, path, Unordered, aspect, filters);
    }
    @Override
    public List<EntityRef> findEntityDescendants(Context context, EntityPath path, Traversal traversal, Aspect aspect)
    {
        return findEntityDescendants(context, path, traversal, aspect, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(Context context, EntityPath path, Traversal traversal, Filter<EntityRef>... filters)
    {
        return findEntityDescendants(context, path, traversal, ANY, filters);
    }

    @Override
    public List<EntityRef> findEntityDescendants(Context context, EntityPath path, Traversal traversal, Aspect aspect, Filter<EntityRef>... filters)
    {
        checkNotNull(context);
        checkNotNull(path);
        checkNotNull(traversal);
        checkNotNull(aspect);

        filters = filters != null ? filters : new Filter[0];

        final EntityTree entityTree = getOrCreateEntityTree(context);
        return entityTree.findDescendants(entityTree.find(path), traversal, aspect, filters);
    }

    @Override
    public List<EntityRef> findEntityDescendants(EntityRef ref)
    {
        return findEntityDescendants(ref, Unordered, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(EntityRef ref, Aspect aspect)
    {
        return findEntityDescendants(ref, Unordered, aspect, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(EntityRef ref, Traversal traversal)
    {
        return findEntityDescendants(ref, traversal, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(EntityRef ref, Filter<EntityRef>... filters)
    {
        return findEntityDescendants(ref, Unordered, ANY, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(EntityRef ref, Aspect aspect, Filter<EntityRef>... filters)
    {
        return findEntityDescendants(ref, Unordered, aspect, filters);
    }

    @Override
    public List<EntityRef> findEntityDescendants(EntityRef ref, Traversal traversal, Aspect aspect)
    {
        return findEntityDescendants(ref, traversal, aspect, ALL);
    }

    @Override
    public List<EntityRef> findEntityDescendants(EntityRef ref, Traversal traversal, Filter<EntityRef>... filters)
    {
        return findEntityDescendants(ref, traversal, ANY, filters);
    }

    @Override
    public List<EntityRef> findEntityDescendants(EntityRef ref, Traversal traversal, Aspect aspect, Filter<EntityRef>... filters)
    {
        checkNotNull(ref);
        checkNotNull(traversal);
        checkNotNull(aspect);

        filters = filters != null ? filters : new Filter[0];

        final EntityTree entityTree = getOrCreateEntityTree(ref.getContext());
        return entityTree.findDescendants(ref, traversal, aspect, filters);
    }

    @Override
    public boolean existsEntity(Context context, EntityPath path)
    {
        checkNotNull(context);
        checkNotNull(path);

        final EntityTree entityTree = getOrCreateEntityTree(context);
        return entityTree != null && entityTree.existsEntity(path);
    }

    private EntityTree getOrCreateEntityTree(Context context)
    {
        context.putIfAbsent(ENTITY_TREE, ENTITY_TREE_VALUE_PROVIDER);
        return context.get(ENTITY_TREE);
    }
}
