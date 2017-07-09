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
package org.jayware.e2.entity.api;

import org.jayware.e2.context.api.Contextual;

import java.util.UUID;


/**
 *
 *
 * @see Entity
 *
 * @since 1.0
 */
public interface EntityRef
extends Contextual
{
    /**
     * Returns the ID of the {@link Entity} this {@link EntityRef} points to.
     * @return the ID of an {@link Entity}
     */
    UUID getId();

    /**
     * Returns whether this {@link EntityRef} is valid.
     * <p>
     * This operation returns the opposite of {@link EntityRef#isInvalid()}.
     * </p>
     *
     * @return <code>true</code> if this {@link EntityRef} is valid, otherwise <code>false</code>.
     */
    boolean isValid();

    /**
     * Returns whether this {@link EntityRef} is invalid.
     * <p>
     * This operation returns the opposite of {@link EntityRef#isValid()}.
     * </p>
     *
     * @return <code>true</code> if this {@link EntityRef} is invalid, otherwise <code>false</code>.
     */
    boolean isInvalid();
}