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


import org.jayware.e2.assembly.api.GroupEvent.CreateGroupEvent;
import org.jayware.e2.assembly.api.GroupEvent.DeleteGroupEvent;
import org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.RemoveEntityFromGroupEvent;
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.GroupNotFoundException;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.util.Key;

import java.util.List;
import java.util.UUID;

import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.AddEntityToGroupEvent;
import static org.jayware.e2.assembly.api.GroupEvent.CreateGroupEvent.GroupNameParam;
import static org.jayware.e2.assembly.api.GroupEvent.GroupParam;
import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.EntityRefParam;
import static org.jayware.e2.assembly.api.Preconditions.checkGroupNotNullAndValid;
import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.context.api.Preconditions.checkContextualsNotNullAndSameContext;
import static org.jayware.e2.entity.api.Preconditions.checkRefNotNullAndValid;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.util.Preconditions.checkStringNotEmpty;


public class GroupManagerImpl
implements GroupManager
{
    private static final Key<GroupHub> GROUP_HUB = Key.createKey("org.jayware.e2.GroupHub");

    private static final Context.ValueProvider<GroupHub> GROUP_HUB_VALUE_PROVIDER = new Context.ValueProvider<GroupHub>()
    {
        @Override
        public GroupHub provide(Context context)
        {
            return new GroupHub(context);
        }
    };

    @Override
    public Group createGroup(Context context)
    throws IllegalArgumentException
    {
        return createGroup(context, UUID.randomUUID().toString());
    }

    @Override
    public Group createGroup(Context context, String name)
    {
        checkContextNotNullAndNotDisposed(context);
        checkStringNotEmpty(name);

        getOrCreateGroupHub(context);

        final EventManager eventManager = context.getEventManager();

        eventManager.send(
            CreateGroupEvent.class,
            param(ContextParam, context),
            param(GroupParam, null),
            param(GroupNameParam, name)
        );

        return getGroup(context, name);
    }

    @Override
    public void deleteGroup(Group group)
    {
        checkGroupNotNullAndValid(group);

        final Context context = group.getContext();
        final EventManager eventManager = context.getEventManager();

        eventManager.send(
            DeleteGroupEvent.class,
            param(ContextParam, context),
            param(GroupParam, null),
            param(GroupNameParam, group.getName())
        );
    }

    @Override
    public Group getGroup(Context context, String name)
    {
        Group group = findGroup(context, name);

        if (group == null)
        {
            throw new GroupNotFoundException("A group with the name '" + name + "' does not exist!");
        }

        return group;
    }

    @Override
    public Group findGroup(Context context, String name)
    {
        checkContextNotNullAndNotDisposed(context);
        checkStringNotEmpty(name);

        return getOrCreateGroupHub(context).findGroup(name);
    }

    @Override
    public void addEntityToGroup(EntityRef ref, Group group)
    throws IllegalArgumentException
    {
        checkRefNotNullAndValid(ref);
        checkGroupNotNullAndValid(group);
        checkContextualsNotNullAndSameContext(ref, group);

        final Context context = ref.getContext();
        final EventManager eventManager = context.getEventManager();

        eventManager.send(
            AddEntityToGroupEvent.class,
            param(ContextParam, context),
            param(GroupParam, group),
            param(EntityRefParam, ref)
        );
    }

    @Override
    public void removeEntityFromGroup(EntityRef ref, Group group)
    throws IllegalArgumentException
    {
        checkRefNotNullAndValid(ref);
        checkGroupNotNullAndValid(group);

        final Context context = ref.getContext();
        final EventManager eventManager = context.getEventManager();

        eventManager.send(
            RemoveEntityFromGroupEvent.class,
            param(ContextParam, context),
            param(GroupParam, group),
            param(EntityRefParam, ref)
        );
    }

    @Override
    public List<EntityRef> getEntitiesOfGroup(Group group)
    {
        checkGroupNotNullAndValid(group);

        return getOrCreateGroupHub(group.getContext()).getEntitiesOfGroup(group);
    }

    @Override
    public boolean isEntityMemberOfGroup(EntityRef ref, Group group)
    {
        checkRefNotNullAndValid(ref);
        checkGroupNotNullAndValid(group);

        return getOrCreateGroupHub(ref.getContext()).isEntityMemberOfGroup(ref, group);
    }

    private GroupHub getOrCreateGroupHub(Context context)
    {
        context.putIfAbsent(GROUP_HUB, GROUP_HUB_VALUE_PROVIDER);
        return context.get(GROUP_HUB);
    }
}
