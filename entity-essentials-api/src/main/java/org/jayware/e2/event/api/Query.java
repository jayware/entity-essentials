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
