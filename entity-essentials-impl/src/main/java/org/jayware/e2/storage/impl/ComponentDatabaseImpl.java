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
package org.jayware.e2.storage.impl;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.storage.api.ComponentDatabase;

import java.util.Collection;
import java.util.Map;


public class ComponentDatabaseImpl
implements ComponentDatabase
{
    private final Map<Class<? extends Component>, Map<EntityRef, Component>> myComponentDatabase;

    public ComponentDatabaseImpl(Map<Class<? extends Component>, Map<EntityRef, Component>> components)
    {
        myComponentDatabase = components;
    }

    @Override
    public void put(EntityRef ref, Component component)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(EntityRef ref, Component component)
    {

    }

    @Override
    public Component get(EntityRef ref, Class<? extends Component> type)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Component> get(EntityRef ref)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(EntityRef ref)
    {

    }
}
