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


import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;


public interface Group
extends Iterable<EntityRef>
{
    enum Policy {
        Manual,
        Aspect
    }

    Context getContext();

    String getName() throws InvalideGroupException;

    Policy getPolicy() throws InvalideGroupException;

    void add(EntityRef ref) throws InvalideGroupException;

    void remove(EntityRef ref) throws InvalideGroupException;

    boolean isValid();

    boolean isInvalid();
}
