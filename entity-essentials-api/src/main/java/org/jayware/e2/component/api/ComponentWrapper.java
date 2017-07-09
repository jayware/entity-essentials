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
    Class<C> type();

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

    W pull();

    W push();
}
