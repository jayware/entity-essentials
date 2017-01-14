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
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.entity.api.ContextualEntityManager;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.util.Filter;

import java.util.List;

import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.context.api.Preconditions.checkContextualNotNullAndBelongsToContext;
import static org.jayware.e2.util.Preconditions.checkNotNull;


public class ContextualEntityManagerImpl
implements ContextualEntityManager
{
    private final Context myContext;
    private final EntityManager myDelegate;

    public ContextualEntityManagerImpl(Context context, EntityManager delegate)
    {
        checkNotNull(delegate);
        checkContextNotNullAndNotDisposed(context);

        myContext = context;
        myDelegate = delegate;
    }

    @Override
    public EntityRef createEntity()
    {
        checkContextNotNullAndNotDisposed(myContext);

        return myDelegate.createEntity(myContext);
    }

    @Override
    public void deleteEntity(EntityRef ref)
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);

        myDelegate.deleteEntity(ref);
    }

    @Override
    public List<EntityRef> deleteEntities()
    {
        checkContextNotNullAndNotDisposed(myContext);

        return myDelegate.deleteEntities(myContext);
    }

    @Override
    public List<EntityRef> findEntities()
    {
        checkContextNotNullAndNotDisposed(myContext);

        return myDelegate.findEntities(myContext);
    }

    @Override
    public List<EntityRef> findEntities(Aspect aspect)
    {
        checkNotNull(aspect);
        checkContextNotNullAndNotDisposed(myContext);

        return myDelegate.findEntities(myContext, aspect);
    }

    @Override
    public List<EntityRef> findEntities(Filter<EntityRef>... filters)
    {
        checkNotNull(filters);
        checkContextNotNullAndNotDisposed(myContext);

        return myDelegate.findEntities(myContext, filters);
    }

    @Override
    public List<EntityRef> findEntities(Aspect aspect, Filter<EntityRef>... filters)
    {
        checkNotNull(aspect);
        checkNotNull(filters);
        checkContextNotNullAndNotDisposed(myContext);

        return myDelegate.findEntities(myContext, aspect, filters);
    }

    @Override
    public Context getContext()
    {
        return myContext;
    }

    @Override
    public boolean belongsTo(Context context)
    {
        return myContext.equals(context);
    }

    @Override
    public boolean belongsTo(Contextual contextual)
    {
        return myContext.equals(contextual.getContext());
    }
}
