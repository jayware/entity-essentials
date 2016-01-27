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
package org.jayware.e2.assembly.api;


import org.jayware.e2.entity.api.EntityRef;


public class Preconditions
{
    /**
     * Ensures that a {@link Group} passed as a parameter to the calling method is not null and valid.
     *
     * @param group an {@link EntityRef}
     *
     * @return the {@link Group} that was validated.
     *
     * @throws IllegalArgumentException if {@link Group} is null.
     * @throws IllegalStateException if {@link Group} is invalid.
     */
    public static Group checkGroupNotNullAndValid(Group group)
    {
        if (group == null)
        {
            throw new IllegalArgumentException();
        }

        if (group.isInvalid())
        {
            throw new IllegalStateException();
        }

        return group;
    }
}
