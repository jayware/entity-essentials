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
import org.jayware.e2.assembly.api.GroupInstantiationException;
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.assembly.api.InvalidGroupException;
import org.jayware.e2.assembly.api.components.GroupComponent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.entity.api.EntityRef;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class GroupImpl
implements Group
{
    private final EntityRef myRef;
    private final Context myContext;
    private final GroupManager myGroupManager;
    private final ComponentManager myComponentManager;

    private final GroupComponent myGroupComponent;

    private GroupImpl(final EntityRef ref, final Context context, final ComponentManager componentManager, final GroupManager groupManager, final GroupComponent component)
    {
        myRef = ref;
        myContext = context;
        myComponentManager = componentManager;
        myGroupManager = groupManager;
        myGroupComponent = component;
    }

    static Group createGroup(final EntityRef ref)
    {
        final Context context;
        final ComponentManager componentManager;
        final GroupManager groupManager;
        final GroupComponent groupComponent;

        try
        {
            context = ref.getContext();
            componentManager = context.getService(ComponentManager.class);
            groupManager = context.getService(GroupManager.class);
            groupComponent = componentManager.getComponent(ref, GroupComponent.class);

            return new GroupImpl(ref, context, componentManager, groupManager, groupComponent);
        }
        catch (Exception e)
        {
            throw new GroupInstantiationException("Failed to instantiate group for: %s ", e, ref);
        }
    }

    @Override
    public UUID getId()
    {
        return myRef.getId();
    }

    @Override
    public Context getContext()
    {
        return myRef.getContext();
    }

    @Override
    public boolean belongsTo(Context context)
    {
        return context != null && myContext.equals(context);
    }

    @Override
    public boolean belongsTo(Contextual contextual)
    {
        return contextual != null && myContext.equals(contextual.getContext());
    }

    @Override
    public String getName()
    {
        check();

        myGroupComponent.pullFrom(myRef);
        return myGroupComponent.getName();
    }

    @Override
    public void setName(String name)
    {
        check();

        myGroupComponent.setName(name);
        myGroupComponent.pushTo(myRef);
    }

    @Override
    public void add(EntityRef ref)
    {
        check();

        myGroupManager.addEntityToGroup(ref, this);
    }

    @Override
    public void remove(EntityRef ref)
    {
        check();

        myGroupManager.removeEntityFromGroup(ref, this);
    }

    @Override
    public List<EntityRef> members()
    {
        check();

        return myGroupManager.getEntitiesOfGroup(this);
    }

    @Override
    public Iterator<EntityRef> iterator()
    {
        return members().iterator();
    }

    @Override
    public boolean isValid()
    {
        return !myContext.isDisposed() && myRef.isValid() && myComponentManager.hasComponent(myRef, GroupComponent.class);
    }

    @Override
    public boolean isInvalid()
    {
        return !isValid();
    }

    @Override
    public boolean equals(Object other)
    {
        return myRef.equals(other);
    }

    @Override
    public int hashCode()
    {
        return myRef.hashCode();
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("Group {");
        sb.append("name=").append(myGroupComponent.getName());
        sb.append('}');
        return sb.toString();
    }

    protected void check()
    {
        if (myContext.isDisposed())
        {
            throw new InvalidGroupException(this, "The context has been disposed!");
        }

        if (myRef.isInvalid())
        {
            throw new InvalidGroupException(this, "The corresponding entity has been deleted!");
        }

        if (!myComponentManager.hasComponent(myRef, GroupComponent.class))
        {
            throw new InvalidGroupException(this, "The corresponding GroupComponent has been removed!");
        }
    }
}
