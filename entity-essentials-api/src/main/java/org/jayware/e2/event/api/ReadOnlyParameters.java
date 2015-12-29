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
