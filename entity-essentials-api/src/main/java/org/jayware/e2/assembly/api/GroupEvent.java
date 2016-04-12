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


public interface GroupEvent
extends AssemblyEvent
{
    /**
     * The {@link Group} which is subject of the event.
     */
    String GroupParam = "org.jayware.e2.event.param.Group";

    interface CreateGroupEvent
    extends GroupEvent
    {
        /**
         * The name ({@link String}) of the {@link Group} which is subject of the event.
         */
        String GroupNameParam = "org.jayware.e2.event.param.Group.name";
    }

    interface GroupCreatedEvent extends GroupEvent {}

    interface DeleteGroupEvent extends GroupEvent {}

    interface GroupDeletedEvent extends GroupEvent {}

    interface GroupMembershipEvent
    extends GroupEvent
    {
        /**
         * The {@link EntityRef} of the entity which is subject of the event.
         */
        String EntityRefParam = "org.jayware.e2.event.param.EntityRef";


        interface AddEntityToGroupEvent extends GroupMembershipEvent
        {

        }

        interface EntityToGroupAddedEvent extends GroupMembershipEvent
        {

        }

        interface RemoveEntityFromGroupEvent extends GroupMembershipEvent
        {

        }

        interface EntityFromGroupRemovedEvent extends GroupMembershipEvent
        {

        }

    }
}
