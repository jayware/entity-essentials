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
import org.jayware.e2.component.api.Component;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Context.ValueProvider;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.util.Key;

import static org.jayware.e2.util.Preconditions.checkNotNull;


public class BindingManagerImpl
implements BindingManager
{
    private static final Key<BindingsTable> OUR_BINDING_TABLE_KEY = Key.createKey("org.jayware.e2.binding.BindingsTable");
    private static final ValueProvider<BindingsTable> OUR_BINDING_TABLE_PROVIDER = new ValueProvider<BindingsTable>()
    {
        @Override
        public BindingsTable provide(Context context)
        {
            final BindingsTable bindingsTable = new BindingsTable(context);
            return bindingsTable;
        }
    };

    @Override
    public ComponentBindingBuilder createComponentBinding(Context context)
    {
        return new ComponentBindingBuilderImpl(context, false);
    }

    @Override
    public <S extends Component, T extends Component> ComponentBinding<S, T> createComponentBinding(Context context, EntityRef sourceRef, Class<S> sourceComponent, EntityRef targetRef, Class<T> targetComponent, BindingRule<S, T> rule)
    {
        checkNotNull(sourceRef, "The binding source entity mustn't be null!");
        checkNotNull(sourceComponent, "The binding source component mustn't be null!");
        checkNotNull(targetRef, "The binding target entity mustn't be null!");
        checkNotNull(targetComponent, "The binding target component mustn't be null!");
        checkNotNull(rule, "The binding rule mustn't be null!");

        return new ComponentBindingImpl<>(sourceRef, sourceComponent, targetRef, targetComponent, rule);
    }

    @Override
    public <S extends Component, T> ComponentBinding<S, T> createComponentBinding(Context context, EntityRef sourceRef, Class<S> sourceComponent, T target, BindingRule<S, T> rule)
    {
        checkNotNull(sourceRef, "The binding source entity mustn't be null!");
        checkNotNull(sourceComponent, "The binding source component mustn't be null!");
        checkNotNull(target, "The binding target mustn't be null!");
        checkNotNull(rule, "The binding rule mustn't be null!");

        return new CustomComponentBindingImpl<>(sourceRef, sourceComponent, target, rule);
    }

    @Override
    public ComponentBindingBuilder addComponentBinding(Context context)
    {
        return new ComponentBindingBuilderImpl(context, true);
    }

    @Override
    public <S extends Component, T extends Component> ComponentBinding<S, T> addComponentBinding(Context context, EntityRef sourceRef, Class<S> sourceComponent, EntityRef targetRef, Class<T> targetComponent, BindingRule<S, T> rule)
    {
        final ComponentBinding<S, T> binding = createComponentBinding(context, sourceRef, sourceComponent, targetRef, targetComponent, rule);
        addBinding(context, binding);
        return binding;
    }

    @Override
    public <S extends Component, T> ComponentBinding<S, T> addComponentBinding(Context context, EntityRef sourceRef, Class<S> sourceComponent, T target, BindingRule<S, T> rule)
    {
        final ComponentBinding<S, T> binding = createComponentBinding(context, sourceRef, sourceComponent, target, rule);
        addBinding(context, binding);
        return binding;
    }

    @Override
    public boolean addBinding(Context context, ComponentBinding binding)
    {
        checkNotNull(context, "To add a binding to a context, the context mustn't be null!");
        checkNotNull(binding, "The binding to add mustn't be null!");

        final BindingsTable bindingsTable = getOrCreateBindingTable(context);
        return bindingsTable.addBinding(binding);
    }

    @Override
    public boolean removeBinding(Context context, ComponentBinding binding)
    {
        checkNotNull(context, "To remove a binding from a context, the context mustn't be null!");
        checkNotNull(binding, "The binding to remove mustn't be null!");

        final BindingsTable bindingsTable = getOrCreateBindingTable(context);
        return bindingsTable.removeBinding(binding);
    }

    private BindingsTable getOrCreateBindingTable(Context context)
    {
        context.putIfAbsent(OUR_BINDING_TABLE_KEY, OUR_BINDING_TABLE_PROVIDER);
        return context.get(OUR_BINDING_TABLE_KEY);
    }
}
