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
package org.jayware.e2.assembly.impl;

import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.components.GroupComponent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Disposable;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ObjectArrays.concat;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.jayware.e2.assembly.api.AssemblyEvent.AddEntityToGroupEvent;
import static org.jayware.e2.assembly.api.AssemblyEvent.CreateGroupEvent;
import static org.jayware.e2.assembly.api.AssemblyEvent.CreateGroupEvent.GroupNameParam;
import static org.jayware.e2.assembly.api.AssemblyEvent.DeleteGroupEvent;
import static org.jayware.e2.assembly.api.AssemblyEvent.EntityFromGroupRemovedEvent;
import static org.jayware.e2.assembly.api.AssemblyEvent.EntityToGroupAddedEvent;
import static org.jayware.e2.assembly.api.AssemblyEvent.GroupCreatedEvent;
import static org.jayware.e2.assembly.api.AssemblyEvent.GroupDeletedEvent;
import static org.jayware.e2.assembly.api.AssemblyEvent.GroupEvent.GroupParam;
import static org.jayware.e2.assembly.api.AssemblyEvent.GroupMembershipEvent.EntityRefParam;
import static org.jayware.e2.assembly.api.AssemblyEvent.RemoveEntityFromGroupEvent;
import static org.jayware.e2.component.api.Aspect.aspect;
import static org.jayware.e2.entity.api.EntityPath.path;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class GroupHub
implements Disposable
{
    private final Context myContext;

    GroupHub(Context context)
    {
        myContext = context;
        myContext.getEventManager().subscribe(context, this);
    }

    @Handle(CreateGroupEvent.class)
    public void handleCreateGroupEvent(@Param(GroupNameParam) String name)
    {
        final EntityManager entityManager = myContext.getEntityManager();
        final ComponentManager componentManager = myContext.getComponentManager();
        final EventManager eventManager = myContext.getEventManager();

        final EntityRef ref = entityManager.createEntity(myContext, path("/" + name));
        final GroupComponent component = componentManager.addComponent(ref, GroupComponent.class);

        component.setName(name);
        component.pushTo(ref);

        eventManager.post(
            GroupCreatedEvent.class,
            param(ContextParam, myContext),
            param(GroupParam, new GroupImpl(ref)),
            param(GroupNameParam, name)
        );
    }

    @Handle(DeleteGroupEvent.class)
    public void handleDeleteGroupEvent(@Param(GroupNameParam) String name)
    {
        final EntityManager entityManager = myContext.getEntityManager();
        final ComponentManager componentManager = myContext.getComponentManager();
        final EventManager eventManager = myContext.getEventManager();

        final List<EntityRef> groups = entityManager.findEntities(myContext, aspect(GroupComponent.class));

        for (EntityRef ref : groups)
        {
            final GroupComponent component = componentManager.getComponent(ref, GroupComponent.class);

            if (name.equals(component.getName()))
            {
                entityManager.deleteEntity(ref);

                eventManager.post(
                    GroupDeletedEvent.class,
                    param(ContextParam, myContext),
                    param(GroupParam, null),
                    param(GroupNameParam, name)
                );

                return;
            }
        }
    }

    public Group findGroup(String name)
    {
        final EntityManager entityManager = myContext.getEntityManager();
        final ComponentManager componentManager = myContext.getComponentManager();

        final List<EntityRef> groups = entityManager.findEntities(myContext, aspect(GroupComponent.class));

        for (EntityRef ref : groups)
        {
            GroupComponent component = componentManager.getComponent(ref, GroupComponent.class);

            if (name.equals(component.getName()))
            {
                return new GroupImpl(ref);
            }
        }

        return null;
    }

    @Handle(AddEntityToGroupEvent.class)
    public void handleAddEntityToGroupEvent(@Param(GroupParam) Group group,
                                            @Param(EntityRefParam) EntityRef member)
    {
        final ComponentManager componentManager = myContext.getComponentManager();
        final EventManager eventManager = myContext.getEventManager();
        final EntityRef groupRef = ((GroupImpl) group).getRef();

        final GroupComponent groupComponent = componentManager.getComponent(groupRef, GroupComponent.class);
        EntityRef[] members = groupComponent.getMembers();

        if (members == null)
        {
            members = new EntityRef[1];
        }

        groupComponent.setMembers(concat(member, members));
        groupComponent.pushTo(groupRef);

        eventManager.post(
            EntityToGroupAddedEvent.class,
            param(ContextParam, myContext),
            param(GroupParam, group),
            param(EntityRefParam, member)
        );
    }

    @Handle(RemoveEntityFromGroupEvent.class)
    public void handleRemoveEntityToGroupEvent(@Param(GroupParam) Group group,
                                               @Param(EntityRefParam) EntityRef member)
    {
        final ComponentManager componentManager = myContext.getComponentManager();
        final EventManager eventManager = myContext.getEventManager();
        final EntityRef groupRef = ((GroupImpl) group).getRef();

        final GroupComponent groupComponent = componentManager.getComponent(groupRef, GroupComponent.class);
        final Set<EntityRef> members = new HashSet(asList(groupComponent.getMembers()));

        members.remove(member);
        groupComponent.setMembers(members.toArray(new EntityRef[members.size()]));
        groupComponent.pushTo(groupRef);

        eventManager.post(
            EntityFromGroupRemovedEvent.class,
            param(ContextParam, myContext),
            param(GroupParam, group),
            param(EntityRefParam, member)
        );
    }

    public List<EntityRef> getEntitiesOfGroup(Group group)
    {
        final ComponentManager componentManager = myContext.getComponentManager();
        final GroupComponent groupComponent = componentManager.getComponent(((GroupImpl) group).getRef(), GroupComponent.class);
        final EntityRef[] members = groupComponent.getMembers();

        if (members == null || members.length == 0)
        {
            return Collections.emptyList();
        }

        return unmodifiableList(asList(members));
    }

    public boolean isEntityMemberOfGroup(EntityRef ref, Group group)
    {
        final ComponentManager componentManager = myContext.getComponentManager();
        final EntityRef groupRef = ((GroupImpl) group).getRef();
        final GroupComponent groupComponent = componentManager.getComponent(groupRef, GroupComponent.class);
        final EntityRef[] members = groupComponent.getMembers();

        return members != null && asList(members).contains(ref);
    }

    @Override
    public void dispose(Context context)
    {
        context.getEventManager().unsubscribe(context, this);
    }
}
