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
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.assembly.api.InvalidGroupException;
import org.jayware.e2.assembly.api.components.GroupComponent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.entity.api.EntityPath;
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

    public GroupImpl(EntityRef ref)
    {
        myRef = ref;
        myContext = myRef.getContext();
        myComponentManager = myContext.getComponentManager();
        myGroupManager = myContext.getGroupManager();
        myGroupComponent = myComponentManager.getComponent(myRef, GroupComponent.class);
    }

    @Override
    public UUID getId()
    {
        return myRef.getId();
    }

    @Override
    public EntityPath getPath()
    {
        return myRef.getPath();
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
