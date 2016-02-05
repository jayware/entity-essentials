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
package org.jayware.e2.context.api;


/**
 * A <code>Contextual</code> is an object which belongs to an <code>Context</code>.
 *
 * @see Context
 */
public interface Contextual
{
    /**
     * Returns the {@link Context} this object belongs to.
     *
     * @return this object's {@link Context}.
     */
    Context getContext();

    /**
     * Returns whether this object belongs to the specified {@link Context}.
     *
     * @param context a {@link Context}.
     *
     * @return <code>true</code> if this object belongs to the specified {@link Context}, otherwise <code>false</code>.
     */
    boolean belongsTo(Context context);

    /**
     * Returns whether this object belongs to the same {@link Context} as the specified one.
     *
     * @param contextual a {@link Contextual}.
     *
     * @return <code>true</code> if this object belongs to the smae {@link Context} as the specified one, otherwise <code>false</code>.
     */
    boolean belongsTo(Contextual contextual);
}
