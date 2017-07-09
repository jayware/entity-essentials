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


import org.jayware.e2.entity.api.EntityRef;


/**
 * Components are designed to store a piece of data. This interface defines the default operations every
 * component has to have.
 * <p>
 * To get a custom component, create a new interface and extend this one. Then define getPropertyNames by writing appropriate
 * getters and setters.
 *
 * @see ComponentManager
 */
public interface Component
{
    /**
     *
     * <p>
     * <b>Note:</b> This operation behaves in the same way as
     * {@link ComponentManager#pullComponent(EntityRef, Component)} does.
     *
     * @param ref an {@link EntityRef}.
     *
     * @see ComponentManager#pushComponent(EntityRef, Component)
     */
    void pullFrom(EntityRef ref);

    /**
     *
     * <p>
     * <b>Note:</b> This operation behaves in the same way as
     * {@link ComponentManager#pushComponent(EntityRef, Component)} does.
     *
     * @param ref an {@link EntityRef}.
     *
     * @see ComponentManager#pushComponent(EntityRef, Component)
     */
    void pushTo(EntityRef ref);

    /**
     *
     * <p>
     * <b>Note:</b> This operation behaves in the same way as
     * {@link ComponentManager#addComponent(EntityRef, Component)} does.
     *
     * @param ref an {@link EntityRef}.
     *
     * @see ComponentManager#addComponent(EntityRef, Component)
     */
    void addTo(EntityRef ref);

    /**
     * Returns this component's type.
     * <p>
     * <b>Note:</b> The type of a {@link Component} cannot be retrieved by a components <code>getClass()</code> operation.
     *
     * @return a {@link Class} representing the component's type.
     */
    Class<? extends Component> type();
}