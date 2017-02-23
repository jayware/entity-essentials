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
import org.jayware.e2.util.TimeoutException;

import java.util.List;


public interface ContextualGroupManager
{
    /**
     * Creates a {@link Group} in the {@link Context} of this {@link ContextualGroupManager}.
     *
     * @return the newly created {@link Group}.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualGroupManager} has been disposed.
     */
    Group createGroup() throws IllegalArgumentException, IllegalStateException;

    /**
     * Creates a {@link Group} with the given name in the {@link Context} of this {@link ContextualGroupManager}.
     *
     * @param name a {@link String} to use as name.
     *
     * @return the newly created {@link Group}.
     *
     * @throws IllegalArgumentException if the specified name ({@link String}) is <code>null</code> or empty.
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualGroupManager} has been disposed.
     */
    Group createGroup(String name) throws IllegalArgumentException, IllegalStateException;

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
     * Returns the {@link Group} with the given name in the {@link Context} of this {@link ContextualGroupManager}.
     * <p>
     * <b>Note:</b> In contrast to {@link ContextualGroupManager#findGroup(String)} this operation
     * never returns <code>null</code>. Instead this operations throws an {@link GroupNotFoundException}
     * if a {@link Group} with the specified name doesn't exist in the {@link Context} of this {@link ContextualGroupManager}.
     * </p>
     *
     * @param name the name of {@link Group}.
     *
     * @return the {@link Group} with the specified name.
     *
     * @throws IllegalArgumentException if the passed name is <code>null</code> or empty.
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualGroupManager} has been disposed.
     * @throws GroupNotFoundException if a group with the specified name does not exist.
     */
    Group getGroup(String name) throws IllegalArgumentException, IllegalStateException, GroupNotFoundException;

    /**
     * Returns the {@link Group} with the given name in the {@link Context} of this {@link ContextualGroupManager}.
     * <p>
     * <b>Note:</b> In contrast to {@link ContextualGroupManager#getGroup(String)}
     * this operation returns <code>null</code> if a {@link Group} with the specified
     * name doesn't exist in the {@link Context} of this {@link ContextualGroupManager}.
     * </p>
     *
     * @param name the name of {@link Group}.
     *
     * @return the {@link Group} with the specified name or <code>null</code> if there is no {@link Group} with the given name.
     *
     * @throws IllegalArgumentException if the passed name is <code>null</code> or empty.
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualGroupManager} has been disposed.
     */
    Group findGroup(String name) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns all {@link Group Groups} within the {@link Context} of this {@link ContextualGroupManager}.
     *
     * @return a {@link List} of {@link Group Groups}.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualGroupManager} has been disposed.
     * @throws GroupManagerException if something went wrong.
     */
    List<Group> findGroups() throws IllegalArgumentException, IllegalStateException, TimeoutException, GroupManagerException;

    /**
     * Returns all {@link Group Groups} to which the {@link Entity entity} denoted by the specified {@link EntityRef} belongs to.
     *
     * @param ref an {@link EntityRef}.
     *
     * @return a {@link List} of {@link Group Groups}.
     *
     * @throws IllegalArgumentException if the passed {@link EntityRef} is <code>null</code>.
     * @throws IllegalStateException if the passed {@link EntityRef} is invalid.
     * @throws IllegalContextException if the passed {@link EntityRef} does not belong to the same context as this {@link ContextualGroupManager}.
     * @throws GroupManagerException if something went wrong.
     */
    List<Group> findGroups(EntityRef ref) throws IllegalArgumentException, IllegalStateException, IllegalContextException, TimeoutException, GroupManagerException;

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
     * @throws IllegalContextException if the passed {@link EntityRef} or {@link Group} do not belong to the same context as this {@link ContextualGroupManager}.
     */
    void addEntityToGroup(EntityRef ref, Group group) throws IllegalArgumentException, IllegalStateException, IllegalContextException, InvalidGroupException;

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
     * @throws IllegalContextException if the passed {@link EntityRef} or {@link Group} do not belong to the same context as this {@link ContextualGroupManager}.
     */
    void removeEntityFromGroup(EntityRef ref, Group group) throws IllegalArgumentException, IllegalStateException, IllegalContextException, InvalidGroupException;

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
     * @throws InvalidGroupException if the specified {@link Group} is invalid.
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
     * @throws InvalidGroupException if the specified {@link Group} is invalid.
     * @throws IllegalContextException if the passed {@link EntityRef} or {@link Group} do not belong to the same context as this {@link ContextualGroupManager}.
     */
    boolean isEntityMemberOfGroup(EntityRef ref, Group group) throws IllegalArgumentException, IllegalStateException, IllegalContextException, InvalidGroupException;
}
