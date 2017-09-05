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
package org.jayware.e2.component.impl;

import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.ContextualComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.entity.api.EntityRef;

import java.util.Arrays;
import java.util.Collection;

import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.context.api.Preconditions.checkContextualNotNullAndBelongsToContext;
import static org.jayware.e2.entity.api.Preconditions.checkRefNotNullAndValid;
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
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(component);

        return myDelegate.addComponent(ref, component);
    }

    @Override
    public <T extends Component> T addComponent(EntityRef ref, T component)
    {
        checkNotNull(component);
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkContextualNotNullAndBelongsToContext((AbstractComponent) component, myContext);

        return myDelegate.addComponent(ref, component);
    }

    @Override
    public <T extends Component> void removeComponent(EntityRef ref, Class<T> component)
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(component);

        myDelegate.removeComponent(ref, component);
    }

    @Override
    public <T extends Component> T getComponent(EntityRef ref, Class<T> component)
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(component);

        return myDelegate.getComponent(ref, component);
    }

    @Override
    public <T extends Component> T findComponent(EntityRef ref, Class<T> component)
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(component);

        return myDelegate.findComponent(ref, component);
    }

    @Override
    public Collection<Component> getComponents(EntityRef ref)
    {
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);

        return myDelegate.getComponents(ref);
    }

    @Override
    public boolean hasComponent(EntityRef ref, Class<? extends Component>... components)
    {
        checkRefNotNullAndValid(ref);
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(components);

        return hasComponent(ref, Arrays.<Class<? extends Component>>asList(components));
    }

    @Override
    public boolean hasComponent(EntityRef ref, Collection<Class<? extends Component>> components)
    {
        checkRefNotNullAndValid(ref);
        checkContextNotNullAndNotDisposed(myContext);
        checkContextualNotNullAndBelongsToContext(ref, myContext);
        checkNotNull(components);

        return myDelegate.hasComponents(ref, components);
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
