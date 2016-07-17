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


import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.util.Filter;
import org.jayware.e2.util.TimeoutException;

import java.util.List;
import java.util.UUID;


/**
 * The <code>EntityManager</code> provides operations to manage {@link Entity Entities}.
 * <p>
 * The <code>EntityManager</code> is context-unaware. By default every {@link Context} offers an
 * <code>EntityManager</code> instance but any instance can be used.
 *
 * @see Context
 * @see Entity
 * @see EntityRef
 * @see Aspect
 */
public interface EntityManager
{
    /**
     * Creates an {@link Entity} in the specifed {@link Context}.
     *
     * @param context a {@link Context} to use.
     *
     * @return an {@link EntityRef} to the newly created {@link Entity}.
     *
     * @throws IllegalArgumentException if the passed {@link Context} is <code>null</code>.
     *
     * @throws IllegalStateException if the passed {@link Context} has been disposed.
     *
     * @throws TimeoutException if the {@link Entity} could not be created within a certain time.
     */
    EntityRef createEntity(Context context) throws IllegalArgumentException, IllegalStateException, TimeoutException;

    /**
     * Creates an {@link Entity} with the specified {@link UUID} in the given {@link Context}.
     * <p>
     * <b>Note:</b> If an {@link Entity} with the specified {@link UUID} already exists, this operation <u>does not</u>
     * create a new {@link Entity}, instead an {@link EntityRef} to the existing {@link Entity} will be returned.
     *
     * @param context a {@link Context} to use.
     *
     * @param id an {@link UUID}.
     *
     * @return an {@link EntityRef} to the newly created {@link Entity}.
     *
     * @throws IllegalArgumentException if the passed {@link Context} or the passed {@link UUID} is <code>null</code>.
     *
     * @throws IllegalStateException if the passed {@link Context} has been disposed.
     *
     * @throws TimeoutException if the {@link Entity} could not be created within a certain time.
     */
    EntityRef createEntity(Context context, UUID id) throws IllegalArgumentException, IllegalStateException, TimeoutException;

    /**
     * Deletes the {@link Entity} denoted by the specified {@link EntityRef}.
     *
     * @param ref an {@link EntityRef}.
     */
    void deleteEntity(EntityRef ref);

    /**
     * Deletes all {@link Entity Entities} within the specified {@link Context}.
     *
     * @param context a {@link Context} to use.
     *
     * @return a {@link List} containing {@link EntityRef}s of the deleted entities.
     *
     * @throws IllegalArgumentException if the passed {@link Context} is <code>null</code>.
     *
     * @throws IllegalStateException if the passed {@link Context} has been disposed.
     */
    List<EntityRef> deleteEntities(Context context) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns a {@link List} of {@link EntityRef}s for all {@link Entity Entities}  within the specified {@link Context}.
     *
     * @param context a {@link Context}.
     *
     * @return a {@link List} of {@link EntityRef}s in any order, never <code>null</code>.
     *
     * @throws IllegalArgumentException if the passed {@link Context} is <code>null</code>.
     *
     * @throws IllegalStateException if the passed {@link Context} has been disposed.
     */
    List<EntityRef> findEntities(Context context) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns a {@link List} of {@link EntityRef}s for all {@link Entity Entities} within the specified {@link Context}
     * which match the specified {@link Aspect}.
     *
     * @param context a {@link Context} to use.
     * @param aspect an {@link Aspect} to qualify the result.
     *
     * @return a {@link List} of {@link EntityRef}s in any order, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Aspect aspect) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns a {@link List} of {@link EntityRef}s for all {@link Entity Entities} within the specified {@link Context}
     * which pass the specified {@link Filter Filters}.
     *
     * @param context a {@link Context} to use.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef}s in any order, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Filter<EntityRef>... filters) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns a {@link List} of {@link EntityRef}s for all {@link Entity Entities} within the specified {@link Context}
     * which match the specified {@link Aspect} and pass specified {@link Filter}s.
     *
     * @param context a {@link Context} to use.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef}s in any order, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Aspect aspect, Filter<EntityRef>... filters) throws IllegalArgumentException, IllegalStateException;

    /**
     * Resolves the {@link Entity} with the specified {@link UUID}.
     * <p>
     * <b>Note:</b> This operation does never return <code>null</code> even if there is no {@link Entity} associated
     * to the passed {@link UUID}. This operation returns an {@link EntityRef} which is invalid if an {@link Entity}
     * with the spcified {@link UUID} does not exist.
     *
     * @param context a {@link Context} to use.
     *
     * @param id an {@link UUID}.
     *
     * @return an {@link EntityRef} to the resolved {@link Entity}.
     *
     * @throws IllegalArgumentException if the passed {@link Context} or the passed {@link UUID} is <code>null</code>.
     *
     * @throws IllegalStateException if the passed {@link Context} has been disposed.
     *
     * @throws TimeoutException if the {@link Entity} could not be created within a certain time.
     */
    EntityRef resolveEntity(Context context, UUID id) throws IllegalArgumentException, IllegalStateException, TimeoutException;

    /**
     * Returns a new instance of a {@link ContextualEntityManager} which belongs to the specified {@link Context}.
     *
     * @param context a {@link Context}
     *
     * @return a {@link ContextualEntityManager}.
     *
     * @throws IllegalArgumentException if the passed {@link Context} is <code>null</code>.
     *
     * @throws IllegalStateException if the passed {@link Context} has been disposed.
     */
    ContextualEntityManager asContextual(Context context) throws IllegalArgumentException, IllegalStateException;
}