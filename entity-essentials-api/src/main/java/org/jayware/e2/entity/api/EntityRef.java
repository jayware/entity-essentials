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
package org.jayware.e2.entity.api;

import org.jayware.e2.context.api.Contextual;


/**
 *
 *
 * @see Entity
 * @see EntityPath
 *
 * @since 1.0
 */
public interface EntityRef
extends Contextual
{
    /**
     * Returns the ID of the {@link Entity} this {@link EntityRef} points to.
     * @return the ID of an {@link Entity}
     */
    String getId();

    /**
     * Returns the {@link EntityPath} of the {@link Entity} this {@link EntityRef} points to.
     * <p>
     * <b>Note:</b> This operation may throw an {@link InvalidEntityRefException} if this {@link EntityRef}
     * becomes invalid.
     * </p>
     *
     * @return the referenced {@link Entity Entity's} {@link EntityPath}.
     *
     * @throws InvalidEntityRefException if the {@link EntityRef} is not valid
     */
    EntityPath getPath() throws InvalidEntityRefException;

    /**
     * Returns whether this {@link EntityRef} is valid.
     * <p>
     * This operation returns the opposite of {@link EntityRef#isInvalid()}.
     * </p>
     *
     * @return <code>true</code> if this {@link EntityRef} is valid, otherwise <code>false</code>.
     */
    boolean isValid();

    /**
     * Returns whether this {@link EntityRef} is invalid.
     * <p>
     * This operation returns the opposite of {@link EntityRef#isValid()}.
     * </p>
     *
     * @return <code>true</code> if this {@link EntityRef} is invalid, otherwise <code>false</code>.
     */
    boolean isInvalid();
}