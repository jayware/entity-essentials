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
    List<EntityRef> deleteEntities() throws IllegalStateException;

    /**
     * Returns a {@link List} of {@link EntityRef}s for all {@link Entity Entities}  within the {@link Context} of this
     * {@link ContextualEntityManager}.
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
     * {@link ContextualEntityManager} which pass specified {@link Filter Filters}.
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
