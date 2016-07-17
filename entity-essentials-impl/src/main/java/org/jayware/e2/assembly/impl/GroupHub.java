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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ObjectArrays.concat;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.jayware.e2.assembly.api.GroupEvent.CreateGroupEvent;
import static org.jayware.e2.assembly.api.GroupEvent.CreateGroupEvent.GroupNameParam;
import static org.jayware.e2.assembly.api.GroupEvent.DeleteGroupEvent;
import static org.jayware.e2.assembly.api.GroupEvent.GroupCreatedEvent;
import static org.jayware.e2.assembly.api.GroupEvent.GroupDeletedEvent;
import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.AddEntityToGroupEvent;
import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.EntityFromGroupRemovedEvent;
import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.EntityRefParam;
import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.EntityToGroupAddedEvent;
import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.RemoveEntityFromGroupEvent;
import static org.jayware.e2.assembly.api.GroupEvent.GroupParam;
import static org.jayware.e2.component.api.Aspect.aspect;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class GroupHub
implements Disposable
{
    private final Context myContext;

    GroupHub(Context context)
    {
        myContext = context;
        myContext.getService(EventManager.class).subscribe(context, this);
    }

    @Handle(CreateGroupEvent.class)
    public void handleCreateGroupEvent(@Param(GroupNameParam) String name)
    {
        final EntityManager entityManager = myContext.getService(EntityManager.class);
        final ComponentManager componentManager = myContext.getService(ComponentManager.class);
        final EventManager eventManager = myContext.getService(EventManager.class);

        final EntityRef ref = entityManager.createEntity(myContext);
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
        final EntityManager entityManager = myContext.getService(EntityManager.class);
        final ComponentManager componentManager = myContext.getService(ComponentManager.class);
        final EventManager eventManager = myContext.getService(EventManager.class);

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
        final EntityManager entityManager = myContext.getService(EntityManager.class);
        final ComponentManager componentManager = myContext.getService(ComponentManager.class);

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
        final ComponentManager componentManager = myContext.getService(ComponentManager.class);
        final EventManager eventManager = myContext.getService(EventManager.class);

        final GroupComponent groupComponent = componentManager.getComponent(group, GroupComponent.class);
        EntityRef[] members = groupComponent.getMembers();

        if (members == null)
        {
            members = new EntityRef[0];
        }

        members = concat(members, member);

        groupComponent.setMembers(members);
        groupComponent.pushTo(group);

        eventManager.post(
            EntityToGroupAddedEvent.class,
            param(ContextParam, myContext),
            param(GroupParam, group),
            param(EntityRefParam, member)
        );
    }

    @Handle(RemoveEntityFromGroupEvent.class)
    public void handleRemoveEntityFromGroupEvent(@Param(GroupParam) Group group,
                                                 @Param(EntityRefParam) EntityRef member)
    {
        final ComponentManager componentManager = myContext.getService(ComponentManager.class);
        final EventManager eventManager = myContext.getService(EventManager.class);

        final GroupComponent groupComponent = componentManager.getComponent(group, GroupComponent.class);
        final Set<EntityRef> members = new HashSet<EntityRef>(asList(groupComponent.getMembers()));

        members.remove(member);
        groupComponent.setMembers(members.toArray(new EntityRef[members.size()]));
        groupComponent.pushTo(group);

        eventManager.post(
            EntityFromGroupRemovedEvent.class,
            param(ContextParam, myContext),
            param(GroupParam, group),
            param(EntityRefParam, member)
        );
    }

    public List<EntityRef> getEntitiesOfGroup(Group group)
    {
        final ComponentManager componentManager = myContext.getService(ComponentManager.class);
        final GroupComponent groupComponent = componentManager.getComponent(group, GroupComponent.class);
        final EntityRef[] members = groupComponent.getMembers();

        if (members == null || members.length == 0)
        {
            return Collections.emptyList();
        }

        return unmodifiableList(asList(members));
    }

    public boolean isEntityMemberOfGroup(EntityRef ref, Group group)
    {
        final ComponentManager componentManager = myContext.getService(ComponentManager.class);
        final GroupComponent groupComponent = componentManager.getComponent(group, GroupComponent.class);
        final EntityRef[] members = groupComponent.getMembers();

        return members != null && asList(members).contains(ref);
    }

    @Override
    public void dispose(Context context)
    {
        context.getService(EventManager.class).unsubscribe(context, this);
    }
}
