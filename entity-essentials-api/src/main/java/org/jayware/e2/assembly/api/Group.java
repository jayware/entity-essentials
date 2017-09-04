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
    String getName();

    /**
     * Sets the name of this {@link Group} to the specified name.
     *
     * @param name a {@link String}.
     *
     * @throws InvalidGroupException if this {@link Group} is invalid.
     */
    void setName(String name);

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
    void add(EntityRef ref);

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
    void remove(EntityRef ref);

    /**
     * Returns a {@link List} containing the {@link EntityRef}s of all entities which are part of this {@link Group}.
     *
     * @return a {@link List} of {@link EntityRef}s.
     *
     * @throws InvalidGroupException if this {@link Group} is invalid.
     */
    List<EntityRef> members();
}
