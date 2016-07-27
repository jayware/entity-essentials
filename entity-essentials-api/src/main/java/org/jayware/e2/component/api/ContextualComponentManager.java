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
package org.jayware.e2.component.api;


import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.Entity;
import org.jayware.e2.entity.api.EntityRef;

import java.util.Collection;


public interface ContextualComponentManager
extends Contextual
{
    /**
     * Prepares the passed {@link Component} type for use.
     * <p>
     * <b>Note:</b> Multiple calls to this operation for the same {@link Component} are silently ignored.
     * A {@link Component} is only prepared once.
     * </p>
     *
     * @param component a {@link Class} representing the {@link Component Component's} type.
     * @param <T> the type of the {@link Component}
     *
     * @throws IllegalArgumentException If the passed Component {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     */
    <T extends Component> void prepareComponent(Class<T> component) throws IllegalArgumentException, IllegalStateException, ComponentFactoryException, MalformedComponentException;

    /**
     * Creates an instance of the {@link Component} specified type ({@link Class}) within the {@link Context} of this
     * {@link ContextualComponentManager}.
     *
     * @param type a {@link Class} representing the {@link Component Component's} type.
     * @param <T> the type of the {@link Component}
     *
     * @return a {@link Component} of the type <code>T</code>.
     *
     * @throws IllegalArgumentException if the passed {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException if the {@link Context} to which this {@link ContextualComponentManager} belongs to
     *                               has been disposed.
     *
     * @throws ComponentManagerException if something went wrong during the creation of the {@link Component}.
     */
    <T extends Component> T createComponent(Class<T> type) throws IllegalArgumentException, IllegalStateException, ComponentManagerException;

    /**
     * Adds the {@link Component} of the specified type to the {@link Entity} referenced by the passed {@link EntityRef}.
     * <p>
     * <b>Note:</b> If the {@link Entity} already has a {@link Component} of the specified type the existing
     * {@link Component} is returned.
     * </p>
     *
     * @param ref       an {@link EntityRef}.
     * @param component the {@link Class} of the {@link Component}.
     * @param <T>       the type of the {@link Component}.
     *
     * @return the {@link Component}.
     *
     * @throws IllegalArgumentException If the passed {@link EntityRef} or the passed Component {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     *
     * @throws ComponentFactoryException If somethings went wrong during the creation or instantiation of the {@link Component}.
     *
     * @throws MalformedComponentException If the passed {@link Class} does not comply to the rules {@link Component} definition.
     *
     * @throws IllegalContextException If the specified {@link EntityRef} belongs to another {@link Context}.
     */
    <T extends Component> T addComponent(EntityRef ref, Class<T> component) throws IllegalArgumentException, IllegalStateException, ComponentFactoryException, MalformedComponentException, IllegalContextException;

    /**
     * Adds the specified {@link Component} to the {@link Entity} referenced by the passed {@link EntityRef}.
     * <p>
     * <b>Note:</b> If the {@link Entity} referenced by the passed {@link EntityRef} already has a {@link Component}
     * of the same type, this operation behaves in exaclly the same way as {@link ComponentManager#pushComponent(EntityRef, Component)}
     * and just updates the existing {@link Component}.
     * </p>
     *
     * @param ref       an {@link EntityRef}.
     * @param component the {@link Component} to add.
     * @param <T>       the type of the {@link Component}.
     *
     * @return the passed {@link Component}.
     *
     * @throws IllegalArgumentException if the passed {@link EntityRef} or {@link Component} is <code>null</code>.
     *
     * @throws IllegalStateException If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     *
     * @throws IllegalContextException if the specified {@link EntityRef} or the specified {@link Component} do not
     *                                 belong to the same {@link Context} as this {@link ContextualComponentManager}.
     *
     * @throws ComponentManagerException if something went wrong during the creation of the {@link Component}.
     */
    <T extends Component> T addComponent(EntityRef ref, T component) throws IllegalArgumentException, IllegalStateException, IllegalContextException, ComponentManagerException;

    /**
     * Removes the {@link Component} with the specified type from the {@link Entity} referenced by the passed
     * {@link EntityRef}.
     * <p>
     * <b>Note:</b> If the {@link Entity} doesn't have a {@link Component} of the specified type, nothing happens.
     * </p>
     *
     * @param ref an {@link EntityRef}
     *            the {@link Class} of the {@link Component}.
     * @param <T> the type of the {@link Component}.
     * @param component a {@link Component} which will be removed
     *
     * @throws IllegalArgumentException If the passed {@link EntityRef} or the passed Component {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     *
     * @throws IllegalContextException If the specified {@link EntityRef} belongs to another {@link Context}.
     */
    <T extends Component> void removeComponent(EntityRef ref, Class<T> component) throws IllegalArgumentException, IllegalStateException, IllegalContextException;

    /**
     * Returns the {@link Component} of the specified type associated to the {@link Entity} referenced by the passed
     * {@link EntityRef}.
     * <p>
     * <b>Note:</b> In contrast to {@link ContextualComponentManager#findComponent(EntityRef, Class)} this operation
     * never returns <code>null</code>. Instead this operations throws a {@link ComponentNotFoundException} if the
     * {@link Entity} doesn't have a {@link Component} of the appropriate type.
     * </p>
     *
     * @param ref       an {@link EntityRef}.
     * @param component the {@link Class} of the {@link Component}.
     * @param <T>       the type of the {@link Component}.
     *
     * @return the {@link Component}.
     *
     * @throws ComponentNotFoundException if the {@link Entity} referenced by the specified {@link EntityRef} doesn't
     *                                    have a {@link Component} of the appropriate type.
     *
     * @throws IllegalArgumentException If the passed {@link EntityRef} or the passed Component {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     *
     * @throws IllegalContextException If the specified {@link EntityRef} belongs to another {@link Context}.
     */
    <T extends Component> T getComponent(EntityRef ref, Class<T> component) throws ComponentNotFoundException, IllegalArgumentException, IllegalStateException, IllegalContextException;

    /**
     * Returns the component of the specified type associated to the {@link Entity} referenced by the passed
     * {@link EntityRef}.
     * <p>
     * <b>Note:</b> In contrast to {@link ContextualComponentManager#getComponent(EntityRef, Class)} this operation
     * returns <code>null</code> if the {@link Entity} doesn't have a {@link Component} of the appropriate type.
     * </p>
     *
     * @param ref       an {@link EntityRef}.
     * @param component the {@link Class} of the {@link Component}
     * @param <T>       the type of the {@link Component}.
     *
     * @return the {@link Component} or <code>null</code>.
     *
     * @throws IllegalArgumentException If the passed {@link EntityRef} or the passed Component {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     *
     * @throws IllegalContextException If the specified {@link EntityRef} belongs to another {@link Context}.
     */
    <T extends Component> T findComponent(EntityRef ref, Class<T> component) throws IllegalArgumentException, IllegalStateException, IllegalContextException;

    /**
     * Returns a {@link Collection} containing all {@link Component Components} associated to the specified
     * {@link EntityRef}.
     * <p>
     * This operation may return an empty {@link Collection} but never <code>null</code>.
     *
     * @param ref   an {@link EntityRef}.
     *
     * @return a {@link Collection}.
     *
     * @throws IllegalArgumentException If the passed {@link EntityRef} or the passed Component {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     *
     * @throws IllegalContextException If the specified {@link EntityRef} belongs to another {@link Context}.
     */
    Collection<Component> getComponents(EntityRef ref) throws IllegalArgumentException, IllegalStateException, IllegalContextException;

    /**
     * Returns whether all {@link Component Components} of the specified types are associated to the {@link Entity} referenced
     * by the passed {@link EntityRef}.
     *
     * @param ref        an {@link EntityRef}.
     * @param components a var-arg of {@link Class Classes}.
     *
     * @return <code>true</code> if the {@link Entity} has all {@link Component Components} of the specified types, otherwise <code>false</code>.
     *
     * @throws IllegalArgumentException If the passed {@link EntityRef} or the passed Component types is <code>null</code>.
     *
     * @throws IllegalStateException    If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     *
     * @throws IllegalContextException  If the specified {@link EntityRef} belongs to another {@link Context}.
     */
    boolean hasComponent(EntityRef ref, Class<? extends Component>... components);

    /**
     * Returns whether all {@link Component Components} of the specified types are associated to the {@link Entity} referenced
     * by the passed {@link EntityRef}.
     *
     * @param ref an {@link EntityRef}.
     * @param components a {@link Collection} of {@link Class Classes}.
     *
     * @return <code>true</code> if the {@link Entity} has all {@link Component Components} of the specified types, otherwise <code>false</code>.
     *
     * @throws IllegalArgumentException If the passed {@link EntityRef} or the passed Component {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException If the {@link Context} to which this {@link ContextualComponentManager} belongs to has been disposed.
     *
     * @throws IllegalContextException If the specified {@link EntityRef} belongs to another {@link Context}.
     */
    boolean hasComponent(EntityRef ref, Collection<Class<? extends Component>> components);
}
