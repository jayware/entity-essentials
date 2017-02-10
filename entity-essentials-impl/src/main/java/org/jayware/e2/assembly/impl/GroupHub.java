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
import static org.jayware.e2.util.ArrayUtil.concat;


public class GroupHub
implements Disposable
{
    private final Context myContext;
    private final EntityManager myEntityManager;
    private final ComponentManager myComponentManager;
    private final EventManager myEventManager;

    GroupHub(Context context)
    {
        myContext = context;

        myEntityManager = myContext.getService(EntityManager.class);
        myComponentManager = myContext.getService(ComponentManager.class);
        myEventManager = myContext.getService(EventManager.class);

        myEventManager.subscribe(context, this);
    }

    @Handle(CreateGroupEvent.class)
    public void handleCreateGroupEvent(@Param(GroupNameParam) String name)
    {
        final EntityRef ref = myEntityManager.createEntity(myContext);
        final GroupComponent component = myComponentManager.createComponent(myContext, GroupComponent.class);

        component.setName(name);
        component.addTo(ref);

        myEventManager.post(
            GroupCreatedEvent.class,
            param(ContextParam, myContext),
            param(GroupParam, GroupImpl.createGroup(ref)),
            param(GroupNameParam, name)
        );
    }

    @Handle(DeleteGroupEvent.class)
    public void handleDeleteGroupEvent(@Param(GroupNameParam) String name)
    {
        final List<EntityRef> groups = myEntityManager.findEntities(myContext, aspect(GroupComponent.class));

        for (EntityRef ref : groups)
        {
            final GroupComponent component = myComponentManager.getComponent(ref, GroupComponent.class);

            if (name.equals(component.getName()))
            {
                myEntityManager.deleteEntity(ref);

                myEventManager.post(
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
        final List<EntityRef> groups = myEntityManager.findEntities(myContext, aspect(GroupComponent.class));

        for (EntityRef ref : groups)
        {
            GroupComponent component = myComponentManager.getComponent(ref, GroupComponent.class);

            if (name.equals(component.getName()))
            {
                return GroupImpl.createGroup(ref);
            }
        }

        return null;
    }

    @Handle(AddEntityToGroupEvent.class)
    public void handleAddEntityToGroupEvent(@Param(GroupParam) Group group,
                                            @Param(EntityRefParam) EntityRef member)
    {
        final GroupComponent groupComponent = myComponentManager.getComponent(group, GroupComponent.class);
        EntityRef[] members = groupComponent.getMembers();

        if (members == null)
        {
            members = new EntityRef[0];
        }

        members = concat(members, member);

        groupComponent.setMembers(members);
        groupComponent.pushTo(group);

        myEventManager.post(
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
        final GroupComponent groupComponent = myComponentManager.getComponent(group, GroupComponent.class);
        final Set<EntityRef> members = new HashSet<EntityRef>(asList(groupComponent.getMembers()));

        members.remove(member);
        groupComponent.setMembers(members.toArray(new EntityRef[members.size()]));
        groupComponent.pushTo(group);

        myEventManager.post(
            EntityFromGroupRemovedEvent.class,
            param(ContextParam, myContext),
            param(GroupParam, group),
            param(EntityRefParam, member)
        );
    }

    public List<EntityRef> getEntitiesOfGroup(Group group)
    {
        final GroupComponent groupComponent = myComponentManager.getComponent(group, GroupComponent.class);
        final EntityRef[] members = groupComponent.getMembers();

        if (members == null || members.length == 0)
        {
            return Collections.emptyList();
        }

        return unmodifiableList(asList(members));
    }

    public boolean isEntityMemberOfGroup(EntityRef ref, Group group)
    {
        final GroupComponent groupComponent = myComponentManager.getComponent(group, GroupComponent.class);
        final EntityRef[] members = groupComponent.getMembers();

        return members != null && asList(members).contains(ref);
    }

    @Override
    public void dispose(Context context)
    {
        myEventManager.unsubscribe(context, this);
    }
}
