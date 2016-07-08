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


import org.jayware.e2.entity.api.EntityRef;


/**
 * Components are designed to store a piece of data. This interface defines the default operations every
 * component has to have.
 * <p>
 * To get a custom component, create a new interface and extend this one. Then define properties by writing appropriate
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