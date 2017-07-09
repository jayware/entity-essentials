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
package org.jayware.e2.entity.impl;


import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.ContextualEntityManager;
import org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntitiesEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.FindEntitiesEvent;
import org.jayware.e2.entity.api.EntityEvent.ResolveEntityEvent;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityManagerException;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.QueryBuilder;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.util.Filter;
import org.jayware.e2.util.TimeoutException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.UUID.randomUUID;
import static org.jayware.e2.component.api.Aspect.ANY;
import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.entity.api.EntityEvent.AspectParam;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefListParam;
import static org.jayware.e2.entity.api.EntityEvent.FilterListParam;
import static org.jayware.e2.entity.api.Preconditions.checkRefNotNullAndValid;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Query.State.Success;
import static org.jayware.e2.util.Preconditions.checkNotNull;


public class EntityManagerImpl
implements EntityManager
{
    private static final long TIMEOUT_IN_MILLISECONDS = 5000;

    @Override
    public EntityRef createEntity(Context context)
    {
        return createEntity(context, randomUUID());
    }

    @Override
    public EntityRef createEntity(Context context, UUID id)
    {
        final EventManager eventManager;
        final ResultSet resultSet;

        checkContextNotNullAndNotDisposed(context);
        checkNotNull(id, "UUID mustn't be null!");

        try
        {
            eventManager = context.getService(EventManager.class);
            resultSet = eventManager.query(CreateEntityEvent.class,
                param(ContextParam, context),
                param(EntityIdParam, id)
            );

            resultSet.timeout(Success, TIMEOUT_IN_MILLISECONDS, "Failed to create entity '%s' within %sms", id, TIMEOUT_IN_MILLISECONDS);

            return resultSet.get(EntityRefParam);
        }
        catch (TimeoutException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new EntityManagerException(e, "Failed to create entity with id: %s", id);
        }
    }

    @Override
    public void deleteEntity(EntityRef ref)
    {
        final Context context;
        final EventManager eventManager;
        final ResultSet resultSet;

        checkRefNotNullAndValid(ref);

        try
        {
            context = ref.getContext();
            eventManager = context.getService(EventManager.class);

            resultSet = eventManager.query(DeleteEntityEvent.class,
                param(ContextParam, context),
                param(EntityRefParam, ref),
                param(EntityIdParam, ref.getId())
            );

            resultSet.timeout(Success, TIMEOUT_IN_MILLISECONDS, "Failed to delete entity '%s' within %sms", ref.getId(), TIMEOUT_IN_MILLISECONDS);
        }
        catch (TimeoutException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new EntityManagerException(e, "Failed to delete entity with id: %s", ref.getId());
        }
    }

    @Override
    public List<EntityRef> deleteEntities(Context context)
    {
        final EventManager eventManager;
        final ResultSet resultSet;

        checkContextNotNullAndNotDisposed(context);

        try
        {
            eventManager = context.getService(EventManager.class);
            resultSet = eventManager.query(DeleteEntitiesEvent.class, param(ContextParam, context));

            resultSet.timeout(Success, TIMEOUT_IN_MILLISECONDS, "Failed to delete all entities within %sms", TIMEOUT_IN_MILLISECONDS);

            return resultSet.get(EntityRefListParam);
        }
        catch (TimeoutException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new EntityManagerException(e, "Failed to delete all entities!");
        }
    }

    @Override
    public List<EntityRef> findEntities(Context context)
    {
        return findEntities(context, ANY);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Aspect aspect)
    {
        return findEntities(context, aspect, (Filter<EntityRef>[]) null);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Filter<EntityRef>... filters)
    {
        return findEntities(context, ANY, filters);
    }

    @Override
    public List<EntityRef> findEntities(Context context, Aspect aspect, Filter<EntityRef>... filters)
    {
        final EventManager eventManager;
        final ResultSet resultSet;
        final QueryBuilder builder;

        checkContextNotNullAndNotDisposed(context);
        checkNotNull(aspect, "Aspect mustn' t be null");

        try
        {
            eventManager = context.getService(EventManager.class);
            builder = eventManager.createQuery(FindEntitiesEvent.class);
            builder.set(ContextParam).to(context)
                   .set(AspectParam).to(aspect);

            if (filters != null)
            {
                final List<Filter<EntityRef>> filterList = new CopyOnWriteArrayList<Filter<EntityRef>>();
                for (Filter<EntityRef> filter : filters)
                {
                    filterList.add(filter);
                }

                builder.set(FilterListParam).to(filterList);
            }

            resultSet = eventManager.query(builder);

            resultSet.timeout(Success, TIMEOUT_IN_MILLISECONDS, "Failed to find entities within %sms", TIMEOUT_IN_MILLISECONDS);

            return resultSet.get(EntityRefListParam);
        }
        catch (TimeoutException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new EntityManagerException(e, "Failed to find entities!");
        }
    }

    @Override
    public EntityRef resolveEntity(Context context, UUID id)
    {
        final EventManager eventManager;
        final ResultSet resultSet;

        checkContextNotNullAndNotDisposed(context);
        checkNotNull(id);

        try
        {
            eventManager = context.getService(EventManager.class);
            resultSet = eventManager.query(ResolveEntityEvent.class,
                param(ContextParam, context),
                param(EntityIdParam, id)
            );

            resultSet.timeout(Success, TIMEOUT_IN_MILLISECONDS, "Failed to resolve '%s' to an entity within %sms", id, TIMEOUT_IN_MILLISECONDS);

            return resultSet.get(EntityRefParam);
        }
        catch (TimeoutException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new EntityManagerException(e, "Failed to resolve entity with id: %s", id);
        }
    }

    @Override
    public ContextualEntityManager asContextual(Context context)
    {
        checkContextNotNullAndNotDisposed(context);
        return new ContextualEntityManagerImpl(context, this);
    }
}
