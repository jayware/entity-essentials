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


import org.jayware.e2.entity.api.EntityRef;


public class Preconditions
{
    /**
     * Ensures that a {@link Context} passed as a parameter to the calling method is not null and not disposed.
     *
     * @param context an {@link Context}
     *
     * @return the {@link Context} that was validated
     *
     * @throws IllegalArgumentException if {@link Context} is null.
     * @throws IllegalStateException if {@link Context} is disposed.
     */
    public static Context checkContext(Context context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException();
        }

        if (context.isDisposed())
        {
            throw new IllegalStateException();
        }

        return context;
    }
}
