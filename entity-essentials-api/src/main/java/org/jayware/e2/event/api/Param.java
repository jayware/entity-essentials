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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.jayware.e2.event.api.Presence.Required;


/**
 * Provides additional information for method-parameters of event handler methods.
 * <p>
 * The parameter name (this annotation's value) is mandatory for the {@link Presence} information the default value is
 * set to {@link Presence#Required}.
 *
 * @see EventManager
 * @see Event
 * @see Parameters
 * @see Presence
 *
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param
{
    /**
     * Returns the name of the {@link Parameters.Parameter Parameter}.
     *
     * @return the name of the {@link Parameters.Parameter Parameter}.
     */
    String value();

    /**
     * Returns the {@link Presence} information of the {@link Parameters.Parameter Parameter}.
     * <p>
     * <b>Default:</b> {@link Presence#Required}
     *
     * @return the {@link Parameters.Parameter Parameter's} {@link Presence} information.
     */
    Presence presence() default Required;
}