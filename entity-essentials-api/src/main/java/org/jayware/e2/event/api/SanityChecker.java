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


/**
 * A <code>SanityChecker</code> is used to check {@link Event Events}.
 * <p>
 * A <code>SanityChecker</code> implementation is instantiated by the event framework, therefore an implementation has
 * to have a parameterless constructor.
 *
 * @see Event
 * @see SanityCheck
 * @see SanityCheckFailedException
 *
 * @since 1.0
 */
public interface SanityChecker
{
    /**
     * Checks the specified {@link Event}. If the {@link Event} fails this {@link SanityChecker SanityChecker's} check
     * a {@link SanityCheckFailedException} is thrown.
     *
     * @param event an {@link Event}.
     *
     * @throws SanityCheckFailedException if the {@link Event} fails the check.
     */
    void check(Event event) throws SanityCheckFailedException;
}
