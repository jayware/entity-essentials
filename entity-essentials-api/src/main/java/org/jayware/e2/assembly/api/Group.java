/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2016 Elmar Schug <elmar.schug@jayware.org>,
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
package org.jayware.e2.assembly.api;


import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.Entity;
import org.jayware.e2.entity.api.EntityRef;

import java.util.List;


/**
 * A <code>Group</code> is set of {@link Entity Entities}.
 *
 * @see Context
 * @see Entity
 * @see EntityRef
 * @see GroupManager
 */
public interface Group
extends EntityRef, Iterable<EntityRef>
{
    /**
     * Returns the name of this {@link Group}.
     *
     * @return this {@link Group}'s name.
     *
     * @throws InvalidGroupException if this {@link Group} is invalid.
     */
    String getName() throws InvalidGroupException;

    /**
     * Sets the name of this {@link Group} to the specified name.
     *
     * @param name a {@link String}.
     *
     * @throws InvalidGroupException if this {@link Group} is invalid.
     */
    void setName(String name) throws InvalidGroupException;

    /**
     * Adds the {@link Entity} designated by the specified {@link EntityRef} to this {@link Group}.
     *
     * @param ref an {@link EntityRef}.
     *
     * @throws IllegalArgumentException if the specified {@link EntityRef} is <code>null</code>.
     * @throws IllegalStateException if the specified {@link EntityRef} is invalid.
     * @throws InvalidGroupException if this {@link Group} is invalid.
     * @throws IllegalContextException if the specified {@link EntityRef} does not belong to the same
     *                                 {@link Context} as this {@link Group}.
     */
    void add(EntityRef ref) throws IllegalArgumentException, IllegalStateException, InvalidGroupException, IllegalContextException;

    /**
     * Removes the {@link Entity} designated by the specified {@link EntityRef} from this {@link Group}.
     *
     * @param ref an {@link EntityRef}.
     *
     * @throws IllegalArgumentException if the specified {@link EntityRef} is <code>null</code>.
     * @throws IllegalStateException if the specified {@link EntityRef} is invalid.
     * @throws InvalidGroupException if this {@link Group} is invalid.
     * @throws IllegalContextException if the specified {@link EntityRef} does not belong to the same
     *                                 {@link Context} as this {@link Group}.
     */
    void remove(EntityRef ref) throws IllegalArgumentException, IllegalStateException, InvalidGroupException, IllegalContextException;

    /**
     * Returns a {@link List} containing the {@link EntityRef}s of all entities which are part of this {@link Group}.
     *
     * @return a {@link List} of {@link EntityRef}s.
     *
     * @throws InvalidGroupException if this {@link Group} is invalid.
     */
    List<EntityRef> members() throws InvalidGroupException;
}
