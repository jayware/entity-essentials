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
package org.jayware.e2.binding.api;


import org.jayware.e2.component.api.Component;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;


/**
 * The <code>BindingManager</code>.
 *
 * @see Binding
 * @see BindingRule
 *
 * @since 1.0
 */
public interface BindingManager
{
    ComponentBindingBuilder createComponentBinding(Context context);

    <S extends Component,T extends Component> ComponentBinding<S, T> createComponentBinding(Context context, EntityRef sourceRef, Class<S> sourceComponent, EntityRef targetRef, Class<T> targetComponent, BindingRule<S, T> rule);

    <S extends Component,T> ComponentBinding<S, T> createComponentBinding(Context context, EntityRef sourceRef, Class<S> sourceComponent, T target, BindingRule<S, T> rule);

    ComponentBindingBuilder addComponentBinding(Context context);

    <S extends Component,T extends Component> ComponentBinding<S, T> addComponentBinding(Context context, EntityRef sourceRef, Class<S> sourceComponent, EntityRef targetRef, Class<T> targetComponent, BindingRule<S, T> rule);

    <S extends Component,T> ComponentBinding<S, T> addComponentBinding(Context context, EntityRef sourceRef, Class<S> sourceComponent, T target, BindingRule<S, T> rule);

    boolean addBinding(Context context, ComponentBinding binding);

    boolean removeBinding(Context context, ComponentBinding binding);
}
