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
import org.jayware.e2.entity.api.Entity;
import org.jayware.e2.entity.api.EntityRef;

import java.util.List;


/**
 * The <code>GroupManager</code> provides operations to manage {@link Group}s of {@link Entity Entities}.
 * <p>
 * The <code>GroupManager</code> is context-unaware. By default every {@link Context} offers an
 * <code>GroupManager</code> instance but any instance can be used.
 *
 * @see Context
 * @see Entity
 * @see EntityRef
 * @see Group
 */
public interface GroupManager
{
    /**
     * Creates a {@link Group} in the specified {@link Context}.
     *
     * @param context a {@link Context} to use.
     *
     * @return the newly created {@link Group}
     *
     * @throws IllegalArgumentException if the specified {@link Context} is <code>null</code>.
     * @throws IllegalStateException if the specified {@link Context} is disposed.
     */
    Group createGroup(Context context) throws IllegalArgumentException, IllegalStateException;

    /**
     * Creates a {@link Group} with the given name in the specified {@link Context}.
     *
     * @param context a {@link Context} to use.
     * @param name a {@link String} to use as name.
     *
     * @return the newly created {@link Group}
     *
     * @throws IllegalArgumentException if the specified {@link Context} is <code>null</code>.
     * @throws IllegalArgumentException if the specified name ({@link String}) is <code>null</code> or empty.
     * @throws IllegalStateException if the specified {@link Context} is disposed.
     */
    Group createGroup(Context context, String name) throws IllegalArgumentException, IllegalStateException;

    /**
     * Deletes the specified {@link Group}.
     *
     * @param group the {@link Group} to delete.
     *
     * @throws IllegalArgumentException if the specified {@link Group} is <code>null</code>.
     * @throws IllegalStateException if the specified {@link Group} is invalid.
     */
    void deleteGroup(Group group) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns the {@link Group} with the given name in the specified {@link Context}.
     * <p>
     * <b>Note:</b> In contrast to {@link GroupManager#findGroup(Context, String)} this operation
     * never returns <code>null</code>. Instead this operations throws an {@link GroupNotFoundException}
     * if a {@link Group} with the specified name doesn't exist in the given {@link Context}.
     * </p>
     *
     * @param context a {@link Context} to use.
     * @param name the name of {@link Group}.
     *
     * @return the {@link Group} with the specified name.
     *
     * @throws IllegalArgumentException if the passed Context is <code>null</code>.
     * @throws IllegalArgumentException if the passed name is <code>null</code> or empty.
     * @throws IllegalStateException if the passed Context is disposed.
     * @throws GroupNotFoundException if a group with the specified name does not exist.
     */
    Group getGroup(Context context, String name) throws IllegalArgumentException, IllegalStateException, GroupNotFoundException;

    /**
     * Returns the {@link Group} with the given name in the specified {@link Context}.
     * <p>
     * <b>Note:</b> In contrast to {@link GroupManager#findGroup(Context, String)}
     * this operation returns <code>null</code> if a {@link Group} with the specified
     * name doesn't exist in the given {@link Context}.
     * </p>
     *
     * @param context a {@link Context} to use.
     * @param name the name of {@link Group}.
     *
     * @return the {@link Group} with the specified name or <code>null</code> if there is no {@link Group} with the given name.
     *
     * @throws IllegalArgumentException if the passed Context is <code>null</code>.
     * @throws IllegalArgumentException if the passed name is <code>null</code> or empty.
     * @throws IllegalStateException if the passed Context is disposed.
     */
    Group findGroup(Context context, String name) throws IllegalArgumentException, IllegalStateException;

    /**
     * Adds the {@link Entity} referenced by the passed {@link EntityRef} to the specified {@link Group}.
     *
     * @param ref an {@link EntityRef}
     * @param group a {@link Group}
     *
     * @throws IllegalArgumentException if the specified {@link EntityRef} is <code>null</code>.
     * @throws IllegalArgumentException if the specified {@link Group} is <code>null</code>.
     * @throws IllegalStateException if the specified {@link EntityRef} is invalid.
     * @throws InvalidGroupException if the specified {@link Group} is invalid.
     */
    void addEntityToGroup(EntityRef ref, Group group) throws IllegalArgumentException, IllegalStateException;

    /**
     * Removes the {@link Entity} referenced by the passed {@link EntityRef} from the specified {@link Group}.
     *
     * @param ref an {@link EntityRef}
     * @param group a {@link Group}
     *
     * @throws IllegalArgumentException if the specified {@link EntityRef} is <code>null</code>.
     * @throws IllegalArgumentException if the specified {@link Group} is <code>null</code>.
     * @throws IllegalStateException if the specified {@link EntityRef} is invalid.
     * @throws InvalidGroupException if the specified {@link Group} is invalid.
     */
    void removeEntityFromGroup(EntityRef ref, Group group) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns a unmodifiable {@link List} of {@link EntityRef EntityRefs} referencing the {@link Entity Entities}
     * which are members of the specified {@link Group}.
     *
     * @param group a {@link Group}.
     *
     * @return a unmodifiable List of {@link EntityRef EntityRefs}, maybe empty if the group
     *         does not have any members, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if the specified {@link Group} is <code>null</code>.
     * @throws IllegalStateException if the specified {@link Group} is invalid.
     */
    List<EntityRef> getEntitiesOfGroup(Group group) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns whether the {@link Entity} referenced by the passed {@link EntityRef} is a member of
     * the specified {@link Group}.
     *
     * @param ref an {@link EntityRef}
     * @param group a {@link Group}
     *
     * @return <code>true</code> if the {@link Entity} is a member of the specified {@link Group},
     *         otherwise <code>false</code>
     *
     * @throws IllegalArgumentException if the specified {@link EntityRef} is <code>null</code>.
     * @throws IllegalArgumentException if the specified {@link Group} is <code>null</code>.
     * @throws IllegalStateException if the specified {@link EntityRef} is invalid.
     * @throws IllegalStateException if the specified {@link Group} is invalid.
     */
    boolean isEntityMemberOfGroup(EntityRef ref, Group group) throws IllegalArgumentException, IllegalStateException;
}
