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
