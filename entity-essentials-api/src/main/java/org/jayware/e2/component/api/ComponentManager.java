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
package org.jayware.e2.component.api;


import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.Entity;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.entity.api.InvalidEntityRefException;

import java.util.Collection;
import java.util.Set;


/**
 *
 *
 * @see Component
 * @see ComponentFactory
 *
 * @since 1.0
 */
public interface ComponentManager
{
    /**
     * Prepares the passed {@link Component} type for use in the specified {@link Context}.
     * <p>
     * <b>Note:</b> Multiple calls to this operation for the same {@link Component} and {@link Context} are silently
     * ignored. A {@link Component} is only prepared once.
     * </p>
     *
     * @param context   a {@link Context}
     * @param component a {@link Class} representing the {@link Component Component's} type.
     * @param <T> the type of the {@link Component}
     */
    <T extends Component> void prepareComponent(Context context, Class<T> component) throws ComponentFactoryException, MalformedComponentException;

    /**
     * Creates an instance of the {@link Component} specified type ({@link Class}) within the passed {@link Context}.
     *
     * @param context a {@link Context}.
     * @param component a {@link Class} representing the {@link Component Component's} type.
     * @param <T> the type of the {@link Component}
     *
     * @return a {@link Component} of the type <code>T</code>.
     *
     * @throws IllegalArgumentException if the passed {@link Context} or {@link Class} is <code>null</code>.
     *
     * @throws IllegalStateException if the specified {@link Context} has been disposed.
     *
     * @throws ComponentManagerException if something went wrong during the creation of the {@link Component}.
     */
    <T extends Component> T createComponent(Context context, Class<T> component) throws IllegalArgumentException, IllegalStateException, ComponentManagerException;

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
     * @return a {@link Component} of the type <code>T</code>.
     */
    <T extends Component> T addComponent(EntityRef ref, Class<T> component) throws ComponentFactoryException, MalformedComponentException, InvalidEntityRefException;

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
     * @throws IllegalStateException if the {@link Context} of the {@link EntityRef} and {@link Component} has been disposed.
     *
     * @throws IllegalContextException if the specified {@link EntityRef} and the specified {@link Component} do not
     *                                 belong to the same {@link Context}.
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
     * @return the removed {@link Component} or <code>null</code>.
     */
    <T extends Component> T removeComponent(EntityRef ref, Class<T> component);

    /**
     * Returns the {@link Component} of the specified type associated to the {@link Entity} referenced by the passed
     * {@link EntityRef}.
     * <p>
     * <b>Note:</b> In contrast to {@link ComponentManager#findComponent(EntityRef, Class)} this operation never returns
     * <code>null</code>. Instead this operations throws a {@link ComponentNotFoundException} if the {@link Entity}
     * doesn't have a {@link Component} of the appropriate type.
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
     */
    <T extends Component> T getComponent(EntityRef ref, Class<T> component) throws ComponentNotFoundException;

    <T extends Component, W extends AbstractComponentWrapper<W, T>> W getComponent(EntityRef ref, W wrapper) throws ComponentNotFoundException;

    /**
     * Returns a {@link Collection} containing all {@link Component Components} associated to the specified
     * {@link EntityRef}.
     * <p>
     * This operation may return an empty {@link Collection} but never <code>null</code>.
     *
     * @param ref   an {@link EntityRef}.
     *
     * @return a {@link Collection}.
     */
    Collection<Component> getComponents(EntityRef ref);

    Collection<Class<? extends Component>> getComponentTypes(EntityRef ref);

    /**
     * Returns the component of the specified type associated to the {@link Entity} referenced by the passed
     * {@link EntityRef}.
     * <p>
     * <b>Note:</b> In contrast to {@link ComponentManager#getComponent(EntityRef, Class)} this operation returns
     * <code>null</code> if the {@link Entity} doesn't have a {@link Component} of the appropriate type.
     * </p>
     *
     * @param ref       an {@link EntityRef}.
     * @param component the {@link Class} of the {@link Component}
     * @param <T>       the type of the {@link Component}.
     *
     * @return the {@link Component} or <code>null</code>.
     */
    <T extends Component> T findComponent(EntityRef ref, Class<T> component);

    /**
     * Pulls the specified {@link Component} from the {@link Entity} referenced by the passed {@link EntityRef}.
     * <p>
     * <b>Note:</b> If the {@link Entity} does not have such a {@link Component}, this operation throws
     * a {@link ComponentNotFoundException}.
     * </p>
     *
     * @param ref an {@link EntityRef} to the owner of the {@link Component}.
     * @param component the {@link Component} to pull.
     * @param <T> the {@link Component Component's} type.
     *
     * @throws ComponentNotFoundException if the {@link Entity} does not have such a {@link Component}.
     */
    <T extends Component> void pullComponent(EntityRef ref, T component) throws ComponentNotFoundException;

    /**
     * Pushes the specified {@link Component} to the {@link Entity} referenced by the passed {@link EntityRef}.
     * <p>
     * <b>Note:</b> In contrast to {@link ComponentManager#addComponent(EntityRef, Component)}, this operation throws
     * a {@link ComponentNotFoundException} If the {@link Entity} referenced by the passed {@link EntityRef} does not
     * have such a {@link Component}.
     * </p>
     *
     * @param ref an {@link EntityRef} to the owner of the {@link Component}.
     * @param component the {@link Component} to push.
     * @param <T> the {@link Component Component's} type.
     *
     * @throws ComponentNotFoundException if the {@link Entity} does not have such a {@link Component}.
     */
    <T extends Component> void pushComponent(EntityRef ref, T component) throws ComponentNotFoundException;

    /**
     * Returns whether a {@link Component} of the specified type is associated to the {@link Entity} referenced
     * by the passed {@link EntityRef}.
     *
     * @param ref       an {@link EntityRef}.
     * @param component the {@link Class} of the {@link Component}
     *
     * @return true if the {@link Entity} has a {@link Component} of the specified type, otherwise false.
     */
    boolean hasComponent(EntityRef ref, Class<? extends Component> component);

    /**
     * Returns whether all {@link Component Components} of the specified types are associated to the {@link Entity} referenced
     * by the passed {@link EntityRef}.
     *
     * @param ref        an {@link EntityRef}.
     * @param components a {@link Collection} of {@link Class Classes}.
     *
     * @return true if the {@link Entity} has all {@link Component Components} of the specified types, otherwise false.
     */
    boolean hasComponents(EntityRef ref, Collection<Class<? extends Component>> components);

    /**
     * Returns the number of {@link Component Components} associated to {@link Entity} referenced by the specified
     * {@link EntityRef}.
     *
     * @param ref an {@link EntityRef}
     *
     * @return the number of {@link Component Components} or <code>0</code> if no {@link Component} is associated.
     */
    int getNumberOfComponents(EntityRef ref);

    /**
     * Resolves the {@link Class} of a {@link Component} by the name of the {@link Component}. To resolve
     * a {@link Component} successfully, the {@link Component} has to be prepared in the given {@link Context}.
     *
     * @param context a {@link Context}
     * @param name    the name of the {@link Component} to resolve.
     * @param <T>     the expected type of the {@link Component}
     *
     * @return the resolved {@link Class type} of the {@link Component} or <code>null</code> if the type
     * could not be resolved.
     */
    <T extends Component> Class<T> resolveComponent(Context context, String name);

    /**
     * Returns a {@link Set} of {@link Class Classes} of all {@link Component Components} known in the specified
     * {@link Context}.
     *
     * @param context the {@link Context} of the {@link Component Components}
     *
     * @return a {@link Set} containing the {@link Class Classes} of all all {@link Component Components} known
     *         in the specified {@link Context}.
     */
    Set<Class<? extends Component>> getComponentClasses(Context context);

    /**
     * Returns a new instance of a {@link ContextualComponentManager} which belongs to the specified {@link Context}.
     *
     * @param context a {@link Context}
     *
     * @return a {@link ContextualComponentManager}.
     *
     * @throws IllegalArgumentException if the passed {@link Context} is <code>null</code>.
     *
     * @throws IllegalStateException if the passed {@link Context} has been disposed.
     */
    ContextualComponentManager asContextual(Context context) throws IllegalArgumentException, IllegalStateException;
}