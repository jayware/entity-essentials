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
package org.jayware.e2.component.api;


import org.jayware.e2.entity.api.Entity;
import org.jayware.e2.entity.api.EntityRef;


/**
 * A <code>ComponentWrapper</code> wraps up a {@link Component} and the {@link Entity} ({@link EntityRef}) to which the
 * component belongs to.
 * <p>
 * Due to the fact that {@link Component Components} are just data-objects created from interfaces with simple getters
 * and setters only. A developer often has to write a lot of logic at different locations multiple times to make a
 * {@link Component} handy. At this point <code>ComponentWrappers</code> come into play to rise the ease of use by
 * allowing developers to wrap up a {@link Component Component} to augment it with additional logic.
 * <p>
 * A <code>ComponentWrapper</code> instance is completely controlled by the developer. Neither does the framework take
 * care of the lifecycle nor does it store any reference of a <code>ComponentWrapper</code>.
 *
 * @param <W> the type of the wrapper-class.
 * @param <C> the type of the wrapped component.
 *
 * @see Component
 * @see ComponentManager
 * @see AbstractComponentWrapper
 *
 * @since 1.0
 */
public interface ComponentWrapper<W extends AbstractComponentWrapper, C extends Component>
{
    /**
     * Wraps the specified {@link EntityRef} and {@link Component}.
     *
     * @param ref an {@link EntityRef} to wrap.
     * @param component a {@link Component} to wrap.
     *
     * @return <code>this</code>.
     */
    W wrap(EntityRef ref, C component);

    /**
     * Returns an {@link EntityRef} of the {@link Entity} to which the wrapped {@link Component} of <code>this</code>
     * {@link AbstractComponentWrapper} belongs to.
     *
     * @return the wrapped {@link Component Component's} owner.
     */
    EntityRef getEntity();

    /**
     * Returns the wrapped {@link Component} of <code>this</code> {@link AbstractComponentWrapper}.
     *
     * @return the wrapped {@link Component}.
     */
    C getComponent();

    /**
     * Returns the type ({@link Class}) of the components <code>this</code> {@link AbstractComponentWrapper} wraps.
     *
     * @return the {@link Component}'s type.
     */
    Class<C> getComponentType();

    /**
     * Returns whether or not <code>this</code> {@link AbstractComponentWrapper} is valid and can operate as expected.
     *
     * @return <code>true</code> if <code>this</code> {@link AbstractComponentWrapper} is valid, otherwise <code>false</code>.
     */
    boolean isValid();

    /**
     * Returns whether or not <code>this</code> {@link AbstractComponentWrapper} is invalid and cannot operate as expected.
     *
     * @return <code>true</code> if <code>this</code> {@link AbstractComponentWrapper} is invalid, otherwise <code>false</code>.
     */
    boolean isInvalid();

    W update();

    W deliver();
}
