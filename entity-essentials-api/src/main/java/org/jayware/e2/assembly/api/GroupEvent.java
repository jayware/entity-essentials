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
    extends GroupEvent, Command
    {
        /**
         * The name ({@link String}) of the {@link Group} which is subject of the event.
         */
        String GroupNameParam = "org.jayware.e2.event.param.Group.name";
    }

    interface GroupCreatedEvent extends GroupEvent, Notification {}

    interface DeleteGroupEvent extends GroupEvent, Command {}

    interface GroupDeletedEvent extends GroupEvent, Notification {}

    interface GroupMembershipEvent
    extends GroupEvent
    {
        /**
         * The {@link EntityRef} of the entity which is subject of the event.
         */
        String EntityRefParam = "org.jayware.e2.event.param.EntityRef";

        interface AddEntityToGroupEvent extends GroupMembershipEvent, Command {}

        interface EntityToGroupAddedEvent extends GroupMembershipEvent, Notification {}

        interface RemoveEntityFromGroupEvent extends GroupMembershipEvent, Command {}

        interface EntityFromGroupRemovedEvent extends GroupMembershipEvent, Notification {}
    }
}
