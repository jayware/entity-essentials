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
 * A {@link Parameter Parameter's} presence information specified by the {@link Param} annotations.
 *
 * @see Event
 * @see Param
 * @see Parameter
 *
 * @since 1.0
 */
public enum Presence
{
    /**
     * The handler method is called only if the parameter is present and not <code>null</code>.
     */
    Required,

    /**
     * The handler method is called only if the parameter is present, but the parameter can be <code>null</code>.
     */
    Conditional,

    /**
     * The handler method is called even if the parameter is absent.
     */
    Optional;
}
