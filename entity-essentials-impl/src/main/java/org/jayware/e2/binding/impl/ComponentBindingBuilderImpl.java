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

import org.jayware.e2.binding.api.BindingManager;
import org.jayware.e2.binding.api.BindingRule;
import org.jayware.e2.binding.api.ComponentBinding;
import org.jayware.e2.binding.api.ComponentBindingBuilder;
import org.jayware.e2.binding.api.ComponentBindingBuilder.ComponentBindingBuilderBy;
import org.jayware.e2.binding.api.ComponentBindingBuilder.ComponentBindingBuilderOf;
import org.jayware.e2.binding.api.ComponentBindingBuilder.ComponentBindingBuilderTo;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;


public class ComponentBindingBuilderImpl
implements ComponentBindingBuilder, ComponentBindingBuilderTo, ComponentBindingBuilderOf, ComponentBindingBuilderBy
{
    private final Context myContext;
    private final BindingManager myBindingManager;
    private final boolean myDoAdd;

    private EntityRef mySourceRef;
    private EntityRef myTargetRef;
    private Class<? extends Component> mySourceComponent;
    private Class<? extends Component> myTargetComponent;

    private Object myCustomTarget;

    public ComponentBindingBuilderImpl(Context context, boolean doAdd)
    {
        myContext = context;
        myBindingManager = context.getBindingManager();
        myDoAdd = doAdd;
    }

    @Override
    public <T extends Component> ComponentBindingBuilderTo<T> bind(EntityRef ref, Class<T> component)
    {
        myTargetRef = ref;
        myTargetComponent = component;
        return this;
    }

    @Override
    public <T> ComponentBindingBuilderTo<T> bind(T target)
    {
        myCustomTarget = target;
        return this;
    }

    @Override
    public ComponentBindingBuilderOf to(Class component)
    {
        mySourceComponent = component;
        return this;
    }

    @Override
    public ComponentBindingBuilderBy of(EntityRef ref)
    {
        mySourceRef = ref;
        return this;
    }

    @Override
    public ComponentBinding by(BindingRule rule)
    {
        final ComponentBinding binding;

        if (myCustomTarget != null)
        {
            binding = myBindingManager.createComponentBinding(myContext, mySourceRef, mySourceComponent, myCustomTarget, rule);
        }
        else
        {
            binding = myBindingManager.createComponentBinding(myContext, mySourceRef, mySourceComponent, myTargetRef, myTargetComponent, rule);
        }

        if (myDoAdd)
        {
            myBindingManager.addBinding(myContext, binding);
        }

        mySourceRef = myTargetRef = null;
        mySourceComponent = myTargetComponent = null;
        myCustomTarget = null;

        return binding;
    }
}
