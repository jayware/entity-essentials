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
package org.jayware.e2.context.api;

import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.binding.api.BindingManager;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.template.api.TemplateManager;
import org.jayware.e2.util.Key;


/**
 * A <code>Context</code>
 *
 * @see Key
 * @see ValueProvider
 * @see Disposable
 * @since 1.0
 */
public interface Context
{
    /**
     * A ValueProvider is used in conjunction with {@link Context#putIfAbsent(Key, ValueProvider)} to postpone
     * the construction of value-objects.
     *
     * @param <T> the <code>value's</code> type.
     *
     * @see Context
     */
    interface ValueProvider<T>
    {
        /**
         * Provides the <code>value</code> to put into the specified {@link Context}.
         * <p>
         * <b>Note: </b> This operation is meant to be called by a {@link Context} implementation.
         *
         * @param context a {@link Context}.
         *
         * @return a <code>value</code>.
         */
        T provide(Context context);
    }

    /**
     * Disposes of this <code>Context</code> and all associated {@link Disposable Disposables}.
     */
    void dispose();

    /**
     * Returns whether this <code>Context</code> is disposed of.
     *
     * @return <code>true</code> if this <code>Context</code> is disposed of, otherwise <code>false</code>.
     */
    boolean isDisposed();

    /**
     * Associates the specified <code>value</code> with the specified {@link Key} in this <code>Context</code>.
     * <p>
     * This operation does not permit <code>null</code> keys. If <code>null</code> is passed as key, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param key a {@link Key} with which the specified value is to be associated.
     * @param value a <code>value</code> to be associated with the specified {@link Key}.
     * @param <T> the <code>value's</code> type.
     *
     * @throws IllegalArgumentException if the specified {@link Key} is <code>null</code>.
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    <T> void put(Key<T> key, T value) throws IllegalArgumentException, IllegalStateException;

    /**
     * Associates the specified <code>value</code> with the specified {@link Key} in this <code>Context</code>.
     * <p>
     * This operation does not permit <code>null</code> keys. If <code>null</code> is passed as key, an
     * {@link IllegalArgumentException} is thrown.
     * <p>
     * In contrast to {@link Context#put(Key, Object)} this operation does only insert an association if the
     * {@link Key} is currently not associated to a <code>value</code> in this <code>Context</code>.
     *
     * @param key a {@link Key} with which the specified value is to be associated.
     * @param value a <code>value</code> to be associated with the specified {@link Key}.
     * @param <T> the <code>value's</code> type.
     *
     * @return true if there was no previous association, otherwise false.
     *
     * @throws IllegalArgumentException if the specified {@link Key} is <code>null</code>.
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    <T> boolean putIfAbsent(Key<T> key, T value) throws IllegalArgumentException, IllegalStateException;

    /**
     * Associates the specified <code>value</code> with the specified {@link Key} in this <code>Context</code>.
     * <p>
     * This operation does not permit <code>null</code> keys. If <code>null</code> is passed as key, an
     * {@link IllegalArgumentException} is thrown.
     * <p>
     * In contrast to {@link Context#put(Key, Object)} this operation does only insert an association if the
     * {@link Key} is currently not associated to a <code>value</code> in this <code>Context</code>.
     * <p>
     * If it is expensive or undesired to create the <code>value</code> before its clear whether this <code>Context</code>
     * contains an association a {@link ValueProvider} affords the caller to postpone the creation and the value-object
     * is only created if the specified {@link Key} is currently not associated to a <code>value</code> in this
     * <code>Context</code>.
     *
     * @param key a {@link Key} with which the specified value is to be associated.
     * @param valueProvider a {@link ValueProvider} to obtain the <code>value</code> to be associated with the
     *                      specified {@link Key}.
     * @param <T> the <code>value's</code> type.
     *
     * @return true if there was no previous association, otherwise false.
     *
     * @throws IllegalArgumentException if the specified {@link Key} is <code>null</code>.
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    <T> boolean putIfAbsent(Key<T> key, ValueProvider<T> valueProvider) throws IllegalArgumentException, IllegalStateException;

    /**
     * Removes the mapping for the specified {@link Key} from this <code>Context</code> if it is present.
     * <p>
     * This operation does not permit <code>null</code> keys. If <code>null</code> is passed as key, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param key a {@link Key} with which the specified value is to be associated.
     * @param <T> the <code>value's</code> type.
     *
     * @throws IllegalArgumentException if the specified {@link Key} is <code>null</code>.
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    <T> void remove(Key<T> key) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns the <code>value</code> to which the specified {@link Key} is mapped, or <code>null</code> if
     * <code>null</code> is mapped to the {@link Key} or if this <code>Context</code> contains no mapping for
     * the specified {@link Key}.
     * <p>
     * This operation does not permit <code>null</code> keys. If <code>null</code> is passed as key, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param key the {@link Key} whose associated value is to be returned
     * @param <T> the <code>value's</code> type.
     *
     * @return the value to which the specified {@link Key} is mapped, or <code>null</code> if this map contains
     *         no mapping for the {@link Key}.
     *
     * @throws IllegalArgumentException if the specified {@link Key} is <code>null</code>.
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    <T> T get(Key<T> key) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns the <code>value</code> to which the specified {@link Key} is mapped, or <code>defaultValue</code>
     * if this <code>Context</code> contains no mapping for the {@link Key}. if the specified {@link Key}
     * is mapped to <code>null</code> than <code>null</code> is returned instead of the passed <code>defaultValue</code>.
     * <p>
     * This operation does not permit <code>null</code> keys. If <code>null</code> is passed as key, an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param key the {@link Key} whose associated value is to be returned
     * @param defaultValue a defaultValue which is returned if no mapping was found
     * @param <T> the <code>value's</code> type.
     *
     * @return the value to which the specified key is mapped, or defaultValue if this map contains no mapping for the key
     *
     * @throws IllegalArgumentException if the specified {@link Key} is <code>null</code>.
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    <T> T get(Key<T> key, T defaultValue) throws IllegalArgumentException, IllegalStateException;

    /**
     * Returns whether this <code>Context</code> contains a mapping for the specified {@link Key}.
     *
     * @param key a {@link Key}.
     *
     * @return <code>true</code> if this <code>Context</code> contains a mapping, otherwise <code>false</code>.
     *
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    boolean contains(Key key) throws IllegalStateException;

    /**
     * Returns the service which offers the interface denoted by the specified {@link Class}.
     * <p>
     * <b>Note:</b> In contrast to {@link Context#findService(Class)}, this operation throws
     * a {@link ServiceUnavailableException} if a suitable service could not be found.
     *
     * @param service a {@link Class} representing the service's interface.
     * @param <S> the type of the service.
     *
     * @return a service instance, never <code>null</code>.
     *
     * @throws ServiceUnavailableException if no suitable service could be found.
     */
    <S> S getService(Class<? extends S> service) throws ServiceUnavailableException;

    /**
     * Returns the service which offers the interface denoted by the specified {@link Class}.
     * <p>
     * <b>Note:</b> In contrast to {@link Context#getService(Class)}, this operation returns
     * <code>null</code> if a suitable service could not be found.
     *
     * @param service a {@link Class} representing the service's interface.
     * @param <S> the type of the service.
     *
     * @return a service instance or <code>null</code> if a suitable could not be found.
     */
    <S> S findService(Class<? extends S> service);

    /**
     * @deprecated in favour of {@link Context#getService(Class)}
     * <p>
     * Returns the {@link EntityManager} instance of this <code>Context</code>.
     *
     * @return this context's {@link EntityManager}.
     *
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    @Deprecated
    EntityManager getEntityManager() throws IllegalStateException;

    /**
     * @deprecated in favour of {@link Context#getService(Class)}
     * <p>
     * Returns the {@link ComponentManager} instance of this <code>Context</code>.
     *
     * @return this context's {@link ComponentManager}.
     *
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    @Deprecated
    ComponentManager getComponentManager() throws IllegalStateException;

    /**
     * @deprecated in favour of {@link Context#getService(Class)}
     * <p>
     * Returns the {@link BindingManager} instance of this <code>Context</code>.
     *
     * @return this context's {@link BindingManager}.
     *
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    @Deprecated
    BindingManager getBindingManager() throws IllegalStateException;

    /**
     * @deprecated in favour of {@link Context#getService(Class)}
     * <p>
     * Returns the {@link TemplateManager} instance of this <code>Context</code>.
     *
     * @return this context's {@link TemplateManager}.
     *
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    @Deprecated
    TemplateManager getTemplateManager() throws IllegalStateException;

    /**
     * @deprecated in favour of {@link Context#getService(Class)}
     * <p>
     * Returns the {@link EventManager} instance of this <code>Context</code>.
     *
     * @return this context's {@link EventManager}.
     *
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    @Deprecated
    EventManager getEventManager() throws IllegalStateException;

    /**
     * @deprecated in favour of {@link Context#getService(Class)}
     * <p>
     * Returns the {@link GroupManager} instance of this <code>Context</code>.
     *
     * @return this context's {@link GroupManager}.
     *
     * @throws IllegalStateException if this <code>Context</code> was disposed of.
     */
    @Deprecated
    GroupManager getGroupManager() throws IllegalStateException;
}
