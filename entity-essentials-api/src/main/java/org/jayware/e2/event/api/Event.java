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
package org.jayware.e2.event.api;


import java.util.UUID;


/**
 * Base <code>Event</code>.
 *
 * @see EventManager
 * @see EventFilter
 * @see Handle
 * @see Parameters
 */
public interface Event
{
    /**
     * Returns the Id of this {@link Event}.
     *
     * @return a {@link UUID} representing this {@link Event}'s Id.
     */
    UUID getId();

    /**
     * Returns the {@link EventType} of this {@link Event}.
     *
     * @return this {@link Event}'s {@link EventType}.
     */
    Class<? extends EventType> getType();

    /**
     * Returns whether this {@link Event} matches the specified {@link EventType}.
     * <p>
     * An {@link Event} matches a specific {@link EventType} if the {@link Event}'s {@link EventType} is assignable
     * to the specific one. The {@link Class#isAssignableFrom(Class)} operation is used to determine if the specified
     * {@link Class} is either the same as, or is a superclass/superinterface of the class of this {@link Event}'s
     * {@link EventType}.
     *
     * @param type a {@link EventType}.
     *
     * @return <code>true</code> if this {@link Event} matches the specified {@link EventType},
     *         otherwise <code>false</code>.
     */
    boolean matches(Class<? extends EventType> type);

    /**
     * Returns the value of the parameter with the specified name or <code>null</code>.
     *
     * @param name the name of the parameter.
     * @param <V> the type of the parameter.
     *
     * @return the value of the parameter or <code>null</code> if the value is <code>null</code>
     *         or this {@link Event} does not have a parameter with the specified name.
     */
    <V> V getParameter(String name);

    /**
     * Returns whether this {@link Event} carries a parameter with the specified name.
     *
     * @param name the name of the paramter.
     *
     * @return <code>true</code> if this {@link Event} carries a parameter with the specified name,
     *         otherwise <code>false</code>.
     */
    boolean hasParameter(String name);

    /**
     * Returns {@link ReadOnlyParameters}.
     *
     * @return {@link ReadOnlyParameters}
     */
    ReadOnlyParameters getParameters();

    /**
     * Returns whether this {@link Event} is a {@link Query}.
     *
     * <b>Note:</b> This operation returns the opposite of {@link Event#isNotQuery()}
     *
     * @return <code>true</code> if this {@link Event} is a {@link Query}, otherwise <code>false</code>.
     */
    boolean isQuery();

    /**
     * Returns whether this {@link Event} is <u>not</u> a {@link Query}.
     *
     * <b>Note:</b> This operation returns the opposite of {@link Event#isQuery()} ()}
     *
     * @return <code>true</code> if this {@link Event} isn't a {@link Query}, otherwise <code>false</code>.
     */
    boolean isNotQuery();
}