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


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Indicates that {@link Event Events} of the annotated {@link EventType} have to be checked with the specified
 * {@link SanityChecker}.
 *
 * @see Event
 * @see EventType
 * @see SanityChecker
 *
 * @since 1.0
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface SanityCheck
{
    /**
     * Returns the {@link SanityChecker} to use.
     *
     * @return a {@link SanityChecker}.
     */
    Class<? extends SanityChecker> value();
}
