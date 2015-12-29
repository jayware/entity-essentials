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