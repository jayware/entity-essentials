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

import org.jayware.e2.assembly.api.AssemblyManager;
import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.InvalideGroupException;
import org.jayware.e2.assembly.api.components.GroupComponent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;


public class GroupImpl
implements Group
{
    private final EntityRef myRef;
    private final Context myContext;
    private final AssemblyManager myAssemblyManager;
    private final ComponentManager myComponentManager;

    private final GroupComponent myGroupComponent;

    public GroupImpl(EntityRef ref)
    {
        myRef = ref;
        myContext = myRef.getContext();
        myComponentManager = myContext.getComponentManager();
        myAssemblyManager = myContext.getAssemblyManager();
        myGroupComponent = myComponentManager.getComponent(myRef, GroupComponent.class);
    }

    @Override
    public Context getContext()
    {
        return myRef.getContext();
    }

    public EntityRef getRef()
    {
        return myRef;
    }

    @Override
    public String getName()
    {
        check();

        myGroupComponent.pullFrom(myRef);
        return myGroupComponent.getName();
    }

    @Override
    public Policy getPolicy()
    throws InvalideGroupException
    {
        check();

        myGroupComponent.pullFrom(myRef);
        return myGroupComponent.getPolicy();
    }

    @Override
    public void add(EntityRef ref)
    {
        myAssemblyManager.addEntityToGroup(ref, this);
    }

    @Override
    public void remove(EntityRef ref)
    {
        myAssemblyManager.removeEntityFromGroup(ref, this);
    }

    @Override
    public List<EntityRef> members()
    {
        return myAssemblyManager.getEntitiesOfGroup(this);
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

    private void check()
    {
        if (myContext.isDisposed())
        {
            throw new InvalideGroupException(this, "The context has been disposed!");
        }

        if (myRef.isInvalid())
        {
            throw new InvalideGroupException(this, "The corresponding entity has been deleted!");
        }

        if (!myComponentManager.hasComponent(myRef, GroupComponent.class))
        {
            throw new InvalideGroupException(this, "The corresponding GroupComponent has been removed!");
        }
    }
}
