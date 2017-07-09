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

import org.jayware.e2.event.api.Parameters.Parameter;


/**
 * This interface defines a read-only view on a set of parameters.
 *
 * @see Parameters
 * @see Parameter
 *
 * @since 1.0
 */
public interface ReadOnlyParameters
extends Iterable<Parameter>
{
    /**
     * Returns the value of the {@link Parameter Parameter} with the specified name or <code>null</code> if there
     * is no {@link Parameter Parameter} with the passed name.
     *
     * @param parameter the {@link Parameter Parameter's} name.
     *
     * @return the {@link Parameter Parameter's} value or <code>null</code>.
     */
    Object get(String parameter);

    /**
     * Returns whether or not this {@link Parameters} contains a {@link Parameter Parameter} with the specified name.
     *
     * @param parameter the {@link Parameter Parameter's} name.
     *
     * @return <code>true</code> if this {@link Parameters} contains a {@link Parameter Parameter} with the specified
     *         name, otherwise <code>false</code>.
     */
    boolean contains(String parameter);
}
