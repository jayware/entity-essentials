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
package org.jayware.e2.examples;


import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.assembly.api.events.AssemblyEvent;
import org.jayware.e2.assembly.api.events.GroupEvent;
import org.jayware.e2.assembly.api.events.GroupEvent.GroupMembershipEvent.EntityFromGroupRemovedEvent;
import org.jayware.e2.assembly.api.events.GroupEvent.GroupMembershipEvent.EntityToGroupAddedEvent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityPath;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.util.ReferenceType;

import java.io.IOException;

import static org.jayware.e2.assembly.api.events.GroupEvent.GroupMembershipEvent.EntityRefParam;
import static org.jayware.e2.assembly.api.events.GroupEvent.GroupParam;
import static org.jayware.e2.util.ReferenceType.Strong;


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
        eventManager.subscribe(context, new ExampleHandler(), Strong);

        /* A couple of entities */
        EntityRef clark = entityManager.createEntity(context);
        EntityRef steve = entityManager.createEntity(context);
        EntityRef tony = entityManager.createEntity(context);

        /* Creating a group with a name */
        Group avengers = groupManager.createGroup(context, "the avengers");

        /* Adding an entity to a group can be done in two ways. */
        groupManager.addEntityToGroup(steve, avengers);
        avengers.add(tony);

        /* The opposite (remove) can also be done in two ways. */
        groupManager.removeEntityFromGroup(steve, avengers);
        avengers.remove(tony);

        /* Because a group is an Iterable it is possible to iterate directly over the members of a group. */
        for (EntityRef avenger : avengers) {}

        /* The GroupManager offers an operation to check whether an entity is member of a group */
        groupManager.isEntityMemberOfGroup(clark, avengers);

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
