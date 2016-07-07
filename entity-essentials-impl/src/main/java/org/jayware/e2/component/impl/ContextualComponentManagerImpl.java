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
package org.jayware.e2.component.impl;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentFactoryException;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.ComponentNotFoundException;
import org.jayware.e2.component.api.ContextualComponentManager;
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityRef;

import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.context.api.Preconditions.checkContextualNotNullAndBelongsToContext;
import static org.jayware.e2.util.Preconditions.checkNotNull;


public class ContextualComponentManagerImpl
implements ContextualComponentManager
{
    private final Context myContext;
    private final ComponentManager myDelegate;

    public ContextualComponentManagerImpl(Context context, ComponentManager delegate)
    {
        myContext = context;
        myDelegate = delegate;
    }

    @Override
    public <T extends Component> void prepareComponent(Class<T> component)
    throws IllegalArgumentException, IllegalStateException, ComponentFactoryException, MalformedComponentException
    {
        checkNotNull(component);
        checkContextNotNullAndNotDisposed(myContext);

        myDelegate.prepareComponent(myContext, component);
    }

    @Override
    public <T extends Component> T createComponent(Class<T> component)
    {
        checkNotNull(component);
        checkContextNotNullAndNotDisposed(myContext);

        return myDelegate.createComponent(myContext, component);
    }

    @Override
    public <T extends Component> T addComponent(EntityRef ref, Class<T> component)
    throws IllegalArgumentException, IllegalStateException, ComponentNotFoundException, ComponentFactoryException, MalformedComponentException, IllegalContextException
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(component);

        return myDelegate.addComponent(ref, component);
    }

    @Override
    public <T extends Component> void removeComponent(EntityRef ref, Class<T> component)
    throws IllegalArgumentException, IllegalStateException, IllegalContextException
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(component);

        myDelegate.removeComponent(ref, component);
    }

    @Override
    public <T extends Component> T getComponent(EntityRef ref, Class<T> component)
    throws ComponentNotFoundException, IllegalArgumentException, IllegalStateException, IllegalContextException
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(component);

        return myDelegate.getComponent(ref, component);
    }

    @Override
    public <T extends Component> T findComponent(EntityRef ref, Class<T> component)
    throws IllegalArgumentException, IllegalStateException, IllegalContextException
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(component);

        return myDelegate.findComponent(ref, component);
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
