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
