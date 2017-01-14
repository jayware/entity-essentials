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


import org.jayware.e2.util.Key;


/**
 * A <code>Query</code> is an {@link Event} which returns results.
 *
 * @see EventManager
 * @see ResultSet
 */
public interface Query
extends Event
{
    enum State
    {
        /**
         * The initial state of {@link Query} until it gets executed.
         */
        Ready,

        /**
         * The state of a {@link Query} during execution.
         */
        Running,

        /**
         * The state of a {@link Query} when execution finished successfully.
         */
        Success,

        /**
         * The state of a {@link Query} when execution finished unsuccessfully.
         */
        Failed
    }

    /**
     * Adds an association of the specified name and value to the {@link ResultSet} of this {@link Query}.
     *
     * @param name a {@link String}
     * @param value a value.
     *
     * @param <V> the value's type
     */
    <V> void result(String name, V value);

    /**
     * Adds an association of the specified name and value to the {@link ResultSet} of this {@link Query}.
     *
     * @param key a {@link Key}
     * @param value a value.
     *
     * @param <V> the value's type
     */
    <V> void result(Key<V> key, V value);
}
