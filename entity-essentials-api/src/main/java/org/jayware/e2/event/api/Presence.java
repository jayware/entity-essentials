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
