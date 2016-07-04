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
import org.jayware.e2.entity.api.EntityRef;


@Deprecated
public interface ComponentBindingBuilder
{
    <T extends Component> ComponentBindingBuilderTo<T> bind(EntityRef targetRef, Class<T> targetComponent);

    <T> ComponentBindingBuilderTo<T> bind(T target);

    interface ComponentBindingBuilderTo<T>
    {
        <S extends Component> ComponentBindingBuilderOf<S, T> to(Class<S> component);
    }

    interface ComponentBindingBuilderOf<S, T>
    {
        ComponentBindingBuilderBy<S, T> of(EntityRef ref);
    }

    interface ComponentBindingBuilderBy<S, T>
    {
        ComponentBinding<S, T> by(BindingRule<S, T> rule);
    }
}
