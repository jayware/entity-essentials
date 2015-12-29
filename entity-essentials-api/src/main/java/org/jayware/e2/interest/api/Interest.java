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
package org.jayware.e2.interest.api;


import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;


public interface Interest
{
    Context getContext();

    void add(EntityRef ref);

    void remove(EntityRef ref);

    void subscribe(Object subscriber);

    void unsubscribe(Object subscriber);

    void enable();

    void disable();

    void setEnabled(boolean enabled);

    void setDisabled(boolean disabled);

    boolean isEnabled();

    boolean isDisabled();
}
