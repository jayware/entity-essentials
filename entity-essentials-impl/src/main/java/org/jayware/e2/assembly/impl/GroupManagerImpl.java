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


import org.jayware.e2.assembly.api.ContextualGroupManager;
import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.GroupEvent.CreateGroupEvent;
import org.jayware.e2.assembly.api.GroupEvent.DeleteGroupEvent;
import org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.RemoveEntityFromGroupEvent;
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.assembly.api.GroupManagerException;
import org.jayware.e2.assembly.api.GroupNotFoundException;
import org.jayware.e2.assembly.api.components.GroupComponent;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.util.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.jayware.e2.assembly.api.GroupEvent.CreateGroupEvent.GroupNameParam;
import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.AddEntityToGroupEvent;
import static org.jayware.e2.assembly.api.GroupEvent.GroupMembershipEvent.EntityRefParam;
import static org.jayware.e2.assembly.api.GroupEvent.GroupParam;
import static org.jayware.e2.assembly.api.Preconditions.checkGroupNotNullAndValid;
import static org.jayware.e2.component.api.Aspect.aspect;
import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.context.api.Preconditions.checkContextualsNotNullAndSameContext;
import static org.jayware.e2.entity.api.Preconditions.checkRefNotNullAndValid;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.util.Preconditions.checkStringNotEmpty;


public class GroupManagerImpl
implements GroupManager
{
    private static final long TIMEOUT_IN_MILLISECONDS = 5000;

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

        final EventManager eventManager = context.getService(EventManager.class);

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
        final EventManager eventManager = context.getService(EventManager.class);

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
    public List<Group> findGroups(Context context)
    {
        final EntityManager entityManager;
        final List<EntityRef> entities;
        final List<Group> result;

        checkContextNotNullAndNotDisposed(context);

        try
        {
            entityManager = context.getService(EntityManager.class);
            entities = entityManager.findEntities(context, aspect(GroupComponent.class));

            result = new ArrayList<Group>(entities.size());

            for (EntityRef entity : entities)
            {
                result.add(GroupImpl.createGroup(entity));
            }

            return result;
        }
        catch (Exception e)
        {
            throw new GroupManagerException("Failed to find groups in context '%s' !", e, context.getId());
        }
    }

    @Override
    public List<Group> findGroups(final EntityRef ref)
    {
        final Context context;
        final EntityManager entityManager;
        final List<EntityRef> entities;
        final List<Group> result;

        checkRefNotNullAndValid(ref);

        try
        {
            context = ref.getContext();
            entityManager = context.getService(EntityManager.class);
            entities = entityManager.findEntities(context, aspect(GroupComponent.class));

            result = new ArrayList<Group>();

            for (EntityRef entity : entities)
            {
                final Group group = GroupImpl.createGroup(entity);

                if (isEntityMemberOfGroup(ref, group))
                {
                    result.add(group);
                }
            }

            return result;
        }
        catch (Exception e)
        {
            throw new GroupManagerException("Failed to find groups of entity '%s' !", e, ref.getId());
        }
    }

    @Override
    public void addEntityToGroup(EntityRef ref, Group group)
    throws IllegalArgumentException
    {
        checkRefNotNullAndValid(ref);
        checkGroupNotNullAndValid(group);
        checkContextualsNotNullAndSameContext(ref, group);

        final Context context = ref.getContext();
        final EventManager eventManager = context.getService(EventManager.class);

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
        final EventManager eventManager = context.getService(EventManager.class);

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

    @Override
    public ContextualGroupManager asContextual(final Context context)
    {
        checkContextNotNullAndNotDisposed(context);
        return new ContextualGroupManagerImpl(context, this);
    }

    private GroupHub getOrCreateGroupHub(Context context)
    {
        context.putIfAbsent(GROUP_HUB, GROUP_HUB_VALUE_PROVIDER);
        return context.get(GROUP_HUB);
    }
}
