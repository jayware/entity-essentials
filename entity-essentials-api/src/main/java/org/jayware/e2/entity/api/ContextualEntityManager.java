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
package org.jayware.e2.entity.api;

import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.util.Filter;

import java.util.List;


/**
 *
 */
public interface ContextualEntityManager
extends Contextual
{
    /**
     * Creates an {@link Entity} within the {@link Context} of this {@link ContextualEntityManager}.
     *
     * @return an {@link EntityRef} of the newly created {@link Entity}.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualEntityManager} has been disposed.
     */
    EntityRef createEntity() throws IllegalStateException;

    /**
     * Deletes the {@link Entity} denoted by the specified {@link EntityRef} within the {@link Context} of this {@link ContextualEntityManager}.
     *
     * @param ref an {@link EntityRef}.
     *
     * @throws IllegalArgumentException if the specified {@link EntityRef} is <code>null</code>.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualEntityManager} has been disposed.
     *
     * @throws IllegalContextException if the specified {@link EntityRef} belongs to another {@link Context}.
     */
    void deleteEntity(EntityRef ref) throws IllegalArgumentException, IllegalStateException, IllegalContextException;

    /**
     * Deletes all {@link Entity Entities} from the {@link Context} of this {@link ContextualEntityManager}.
     *
     * @return a {@link List} of {@link EntityRef}s.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualEntityManager} has been disposed.
     */
    List<EntityRef> deleteEntities() throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns a {@link List} of all {@link Entity Entities} within the {@link Context} of this {@link ContextualEntityManager}.
     *
     * @return a {@link List} of {@link EntityRef}s in any order, never <code>null</code>.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualEntityManager} has been disposed.
     */
    List<EntityRef> findEntities() throws IllegalStateException;

    /**
     * Returns a {@link List} of {@link EntityRef}s for all {@link Entity Entities} within the {@link Context} of this
     * {@link ContextualEntityManager} which match the specified {@link Aspect}.
     *
     * @param aspect an {@link Aspect}.
     *
     * @return a {@link List} of {@link EntityRef}s in any order, never <code>null</code>.
     *
     * @throws IllegalArgumentException if the specified {@link Aspect} is <code>null</code>.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualEntityManager} has been disposed.
     */
    List<EntityRef> findEntities(Aspect aspect) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns a {@link List} of {@link EntityRef}s for all {@link Entity Entities} within the {@link Context} of this
     * {@link ContextualEntityManager} which pass specified {@link Filter}s.
     *
     * @param filters one or more {@link Filter}s.
     *
     * @return a {@link List} of {@link EntityRef}s in any order, never <code>null</code>.
     *
     * @throws IllegalArgumentException if the specified {@link Aspect} is <code>null</code>.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualEntityManager} has been disposed.
     */
    List<EntityRef> findEntities(Filter<EntityRef>... filters) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns a {@link List} of {@link EntityRef}s for all {@link Entity Entities} within the {@link Context} of this {@link ContextualEntityManager}
     * which match the specified {@link Aspect} and pass specified {@link Filter}s.
     *
     * @param aspect an {@link Aspect}.
     * @param filters one or more {@link Filter}s.
     *
     * @return a {@link List} of {@link EntityRef}s in any order, never <code>null</code>.
     *
     * @throws IllegalArgumentException if the specified {@link Aspect} is <code>null</code> or <code>null</code> is passed as vararg.
     *
     * @throws IllegalStateException if the {@link Context} of this {@link ContextualEntityManager} has been disposed.
     */
    List<EntityRef> findEntities(Aspect aspect, Filter<EntityRef>... filters) throws IllegalArgumentException, IllegalStateException;
}
