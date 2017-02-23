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
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;

import java.util.List;


public class ContextualGroupManagerImpl
implements ContextualGroupManager
{
    private final Context myContext;
    private final GroupManager myDelegate;

    ContextualGroupManagerImpl(final Context context, final GroupManager delegate)
    {
        myContext = context;
        myDelegate = delegate;
    }

    @Override
    public Group createGroup()
    {
        return myDelegate.createGroup(myContext);
    }

    @Override
    public Group createGroup(final String name)
    {
        return myDelegate.createGroup(myContext, name);
    }

    @Override
    public void deleteGroup(final Group group)
    {
        myDelegate.deleteGroup(group);
    }

    @Override
    public Group getGroup(final String name)
    {
        return myDelegate.getGroup(myContext, name);
    }

    @Override
    public Group findGroup(final String name)
    {
        return myDelegate.findGroup(myContext, name);
    }

    @Override
    public List<Group> findGroups()
    {
        return myDelegate.findGroups(myContext);
    }

    @Override
    public List<Group> findGroups(final EntityRef ref)
    {
        return myDelegate.findGroups(ref);
    }

    @Override
    public void addEntityToGroup(final EntityRef ref, final Group group)
    {
        myDelegate.addEntityToGroup(ref, group);
    }

    @Override
    public void removeEntityFromGroup(final EntityRef ref, final Group group)
    {
        myDelegate.removeEntityFromGroup(ref, group);
    }

    @Override
    public List<EntityRef> getEntitiesOfGroup(final Group group)
    {
        return myDelegate.getEntitiesOfGroup(group);
    }

    @Override
    public boolean isEntityMemberOfGroup(final EntityRef ref, final Group group)
    {
        return myDelegate.isEntityMemberOfGroup(ref, group);
    }
}
