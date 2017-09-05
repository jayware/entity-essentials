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
package org.jayware.e2.examples;


import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.EntityFromGroupRemovedEvent;
import org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.EntityToGroupAddedEvent;
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;

import java.io.IOException;

import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.EntityRefParam;
import static org.jayware.e2.assembly.api.GroupEvent.GroupParam;
import static org.jayware.e2.util.ReferenceType.STRONG;


public class EntityGroupsExample
{
    public static void main(String[] args)
    throws IOException
    {

        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EventManager eventManager = context.getService(EventManager.class);
        EntityManager entityManager = context.getService(EntityManager.class);

        /* The GroupManager offers an interface to manage groups of entities. */
        GroupManager groupManager = context.getService(GroupManager.class);

        /* Subscribe an event handler to see an output */
        eventManager.subscribe(context, new ExampleHandler(), STRONG);

        /* A couple of entities */
        EntityRef clark = entityManager.createEntity(context);
        EntityRef steve = entityManager.createEntity(context);
        EntityRef tony = entityManager.createEntity(context);

        /* Creating a group with a name */
        Group avengers = groupManager.createGroup(context, "the avengers");
        Group justiceleague = groupManager.createGroup(context, "justice league");

        /* Adding an entity to a group can be done in two ways. */
        groupManager.addEntityToGroup(steve, avengers);
        avengers.add(tony);
        justiceleague.add(clark);

        /* The opposite (remove) can also be done in two ways. */
        groupManager.removeEntityFromGroup(steve, avengers);
        avengers.remove(tony);

        /* Because a group is an Iterable it is possible to iterate directly over the members of a group. */
        for (EntityRef avenger : avengers) {}

        /* The GroupManager offers an operation to check whether an entity is member of a group. */
        groupManager.isEntityMemberOfGroup(clark, avengers);

        /* It is also possible to get a list of all groups within a context. */
        groupManager.findGroups(context);

        /* Shutdown everything */
        context.dispose();
    }

    /* There are a couple of Events related to groups to integrate with. */
    public static class ExampleHandler
    {
        @Handle(EntityToGroupAddedEvent.class)
        public void handleJoin(@Param(EntityRefParam) EntityRef ref, @Param(GroupParam) Group group)
        {
            System.out.println("\n" + ref.getId() + " joined " + group.getName() + "\n");
        }

        @Handle(EntityFromGroupRemovedEvent.class)
        public void handleLeave(@Param(EntityRefParam) EntityRef ref, @Param(GroupParam) Group group)
        {
            System.out.println("\n" + ref.getId() + " leaved " + group.getName() + "\n");
        }
    }
}
