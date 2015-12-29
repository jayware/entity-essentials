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
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.util.Filter;
import org.jayware.e2.util.Traversal;

import java.util.List;


/**
 * The <code>EntityManager</code> provides operations to manage {@link Entity Entities}.
 * <p>
 * The <code>EntityManager</code> is context-unaware. By default every {@link Context} offers an
 * <code>EntityManager</code> instance but any instance can be used.
 *
 * @see Context
 * @see Entity
 * @see EntityPath
 * @see EntityRef
 * @see Aspect
 *
 * @since 1.0
 */
public interface EntityManager
{
    /**
     * Creates an {@link Entity} denoted by the passed {@link EntityPath}
     * in the specified {@link Context}.
     *
     * @param context a {@link Context} to use.
     * @param path an absolute {@link EntityPath}.
     *
     * @return an {@link EntityRef} to the newly created {@link Entity}
     *
     * @throws IllegalArgumentException if the specified {@link EntityPath} is not absolute.
     */
    EntityRef createEntity(Context context, EntityPath path) throws IllegalArgumentException;

    /**
     * Creates an {@link Entity} relative to a parent {@link Entity}
     * represented by the passed {@link EntityRef}. The specified relative {@link EntityPath}
     * is resolved against the parent's path to an absolute path.
     *
     * @param parentRef an {@link EntityRef} of the parent {@link Entity}.
     * @param path a relative {@link EntityPath} to use.
     *
     * @return an {@link EntityRef} to the newly created {@link Entity}
     *
     * @throws IllegalArgumentException if the specified {@link EntityPath} is not relative.
     */
    EntityRef createEntity(EntityRef parentRef, EntityPath path) throws IllegalArgumentException;

    /**
     * Deletes the {@link Entity} denoted by the specified {@link EntityRef}.
     *
     * @param ref an {@link EntityRef}.
     */
    void deleteEntity(EntityRef ref);

    /**
     * Moves an {@link Entity} from its current parent to an other one.
     * <p>
     * <b>Note:</b> It is not possible to move an {@link Entity} into an other
     * {@link Context}, therefor the destination {@link Entity}
     * has to be in the same {@link Context} as the {@link Entity}
     * to move.
     * </p>
     *
     * @param entity the {@link EntityRef} of the
     *               {@link Entity} to move.
     *
     * @param destination the {@link EntityRef} of the destination
     *                    {@link Entity}.
     *
     * @throws IllegalContextException if the specified {@link EntityRef EntityRefs}
     *                                 are from different {@link Context Contexts}.
     *
     */
    void moveEntity(EntityRef entity, EntityRef destination) throws IllegalContextException;

    /**
     * Returns an {@link EntityRef} to the {@link Entity}
     * denoted by the specified {@link EntityPath} in the given {@link Context}.
     * <p>
     * <b>Note:</b> In contrast to {@link EntityManager#findEntity(Context, EntityPath)} this operation
     * never returns <code>null</code>. Instead this operations throws an {@link EntityNotFoundException}
     * if an {@link Entity} with the specified {@link EntityPath}
     * doesn't exist in the given {@link Context}.
     * </p>
     *
     * @param context a {@link Context} to use.
     * @param path an absolute {@link EntityPath}.
     *
     * @return an {@link EntityRef}, but never <code>null</code>.
     *
     * @throws EntityNotFoundException if an {@link Entity} with the specified
     *                                 {@link EntityPath} does not exist.
     *
     * @throws IllegalArgumentException if the specified {@link EntityPath} is empty.
     *                                  if the specified {@link EntityPath} is not absolute.
     */
    EntityRef getEntity(Context context, EntityPath path) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * Returns an {@link EntityRef} to the {@link Entity}
     * denoted by the specified {@link EntityPath} in the given {@link Context}.
     * <p>
     * <b>Note:</b> In contrast to {@link EntityManager#getEntity(Context, EntityPath)}
     * this operation returns <code>null</code> if an {@link Entity} with the specified
     * {@link EntityPath} doesn't exist in the given {@link Context}.
     * </p>
     *
     * @param context a {@link Context} to use.
     * @param path an absolute {@link EntityPath}.
     *
     * @return an {@link EntityRef} or <code>null</code>.
     *
     * @throws IllegalArgumentException if the specified {@link EntityPath} is not absolute.
     */
    EntityRef findEntity(Context context, EntityPath path) throws IllegalArgumentException;

    /**
     * Finds {@link Entity Entities} in the specified {@link Context} based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntities(Context, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context}.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context) throws IllegalArgumentException;

    /**
     * Finds {@link Entity Entities} in the specified {@link Context} based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntities(Context, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>given</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param aspect an {@link Aspect} to qualify the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Aspect aspect) throws IllegalArgumentException;

    /**
     * Finds {@link Entity Entities} in the specified {@link Context} based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntities(Context, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>given</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Filter<EntityRef>... filters);

    /**
     * Finds {@link Entity Entities} in the specified {@link Context} based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntities(Context, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>given</td><td>given</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Aspect aspect, Filter<EntityRef>... filters);

    /**
     * Finds {@link Entity Entities} in the specified {@link Context} based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntities(Context, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Traversal traversal);

    /**
     * Finds {@link Entity Entities} in the specified {@link Context} based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntities(Context, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>given</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param aspect an {@link Aspect} to qualify the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Traversal traversal, Aspect aspect);

    /**
     * Finds {@link Entity Entities} in the specified {@link Context} based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntities(Context, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Aspect#ANY}</td><td>given</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Traversal traversal, Filter<EntityRef>... filters);

    /**
     * Finds {@link Entity Entities} in the specified {@link Context} based on the passed parameters.
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntities(Context context, Traversal traversal, Aspect aspect, Filter<EntityRef>... filters);

    /**
     * Finds all ancestors of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityAncestors(Context, EntityPath, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityAncestors(Context context, EntityPath path);

    /**
     * Finds all ancestors of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityAncestors(Context, EntityPath, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>given</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param aspect an {@link Aspect} to qualify the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityAncestors(Context context, EntityPath path, Aspect aspect);

    /**
     * Finds all ancestors of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityAncestors(Context, EntityPath, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Aspect#ANY}</td><td>given</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityAncestors(Context context, EntityPath path, Filter<EntityRef>... filters);

    /**
     * Finds all ancestors of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityAncestors(Context context, EntityPath path, Aspect aspect, Filter<EntityRef>... filters);

    /**
     * Finds all ancestors of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityAncestors(EntityRef, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityAncestors(EntityRef ref);

    /**
     * Finds all ancestors of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityAncestors(EntityRef, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param aspect an {@link Aspect} to qualify the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityAncestors(EntityRef ref, Aspect aspect);

    /**
     * Finds all ancestors of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityAncestors(EntityRef, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Aspect#ANY}</td><td>given</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs}, never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityAncestors(EntityRef ref, Filter<EntityRef>... filters);

    /**
     * Finds all ancestors of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityAncestors(EntityRef ref, Aspect aspect, Filter<EntityRef>... filters);

    /**
     * Finds all descendants of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(Context, EntityPath, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(Context context, EntityPath path);

    /**
     * Finds all descendants of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(Context, EntityPath, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param aspect an {@link Aspect} to qualify the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(Context context, EntityPath path, Aspect aspect);

    /**
     * Finds all descendants of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(Context, EntityPath, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(Context context, EntityPath path, Filter<EntityRef>... filters);

    /**
     * Finds all descendants of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(Context, EntityPath, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(Context context, EntityPath path, Traversal traversal);

    /**
     * Finds all descendants of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(Context, EntityPath, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(Context context, EntityPath path, Aspect aspect, Filter<EntityRef>... filters);

    /**
     * Finds all descendants of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(Context, EntityPath, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param aspect an {@link Aspect} to qualify the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(Context context, EntityPath path, Traversal traversal, Aspect aspect);

    /**
     * Finds all descendants of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(Context, EntityPath, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(Context context, EntityPath path, Traversal traversal, Filter<EntityRef>... filters);

    /**
     * Finds all descendants of the {@link Entity} denoted by the specified {@link EntityPath} in the given {@link Context},
     * based on the passed parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(Context, EntityPath, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>Context</th><th>EntityPath</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param context a {@link Context} to use.
     * @param path the {@link EntityPath} of the {@link Entity} from where to start the search.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(Context context, EntityPath path, Traversal traversal, Aspect aspect, Filter<EntityRef>... filters);

    /**
     * Finds all descendants of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(EntityRef, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(EntityRef ref);

    /**
     * Finds all descendants of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(EntityRef, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param aspect an {@link Aspect} to qualify the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(EntityRef ref, Aspect aspect);

    /**
     * Finds all descendants of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(EntityRef, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>given</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     *
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(EntityRef ref, Filter<EntityRef>... filters);

    /**
     * Finds all descendants of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(EntityRef, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * 
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(EntityRef ref, Traversal traversal);

    /**
     * Finds all descendants of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(EntityRef, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>{@link Traversal#Unordered}</td><td>{@link Aspect#ANY}</td><td>given</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     * 
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(EntityRef ref, Aspect aspect, Filter<EntityRef>... filters);

    /**
     * Finds all descendants of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(EntityRef, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>given</td><td>{@link EntityPathFilter#ALL}</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param aspect an {@link Aspect} to qualify the result.
     * 
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(EntityRef ref, Traversal traversal, Aspect aspect);

    /**
     * Finds all descendants of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * Based on: {@link EntityManager#findEntityDescendants(EntityRef, Traversal, Aspect, Filter[])}<br>
     * <table>
     *      <tr><th>EntityRef</th><th>Traversal</th><th>Aspect</th><th>Filters</th></tr>
     *      <tr><td>given</td><td>given</td><td>{@link Aspect#ANY}</td><td>given</td></tr>
     *      <caption>Parameters</caption>
     * </table>
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     * 
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(EntityRef ref, Traversal traversal, Filter<EntityRef>... filters);

    /**
     * Finds all descendants of the {@link Entity} referenced by the specified {@link EntityRef}, based on the passed
     * parameters.
     * <p>
     * An {@link EntityManager} may return any implementation of the {@link List} interface and an {@link EntityManager}
     * may also return different implementation of the {@link List} interface on subsequent calls when different
     * parameters are passed.
     *
     * @param ref the {@link EntityRef} of the {@link Entity} from where to start the search.
     * @param traversal a {@link Traversal} to define the order in which the {@link Entity Entities} are processed.
     * @param aspect an {@link Aspect} to qualify the result.
     * @param filters a set of {@link Filter Filters} to reduce the result.
     * 
     * @return a {@link List} of {@link EntityRef EntityRefs} maybe empty, but never <code>null</code>.
     *
     * @throws IllegalArgumentException if one of the parameters is <code>null</code>.
     */
    List<EntityRef> findEntityDescendants(EntityRef ref, Traversal traversal, Aspect aspect, Filter<EntityRef>... filters);

    /**
     * Returns whether an {@link Entity} with the specified {@link EntityPath} exists in the specified {@link Context}.
     *
     * @param context a {@link Context}.
     * @param path an {@link EntityPath}.
     *
     * @return <code>true</code> if an {@link Entity} with the specified {@link EntityPath} in the passed {@link Context}
     *         exists, otherwise <code>false</code>.
     */
    boolean existsEntity(Context context, EntityPath path);
}