/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
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
package org.jayware.e2.context.impl;


import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.binding.api.BindingManager;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.template.api.TemplateManager;

import java.util.ServiceLoader;


public class ContextProviderImpl
extends ContextProvider
{
    @Override
    public Context createContext()
    {
        final EntityManager entityManager = ServiceLoader.load(EntityManager.class).iterator().next();
        final ComponentManager componentManager = ServiceLoader.load(ComponentManager.class).iterator().next();
        final TemplateManager templateManager = ServiceLoader.load(TemplateManager.class).iterator().next();
        final EventManager eventManager = ServiceLoader.load(EventManager.class).iterator().next();
        final GroupManager groupManager = ServiceLoader.load(GroupManager.class).iterator().next();
        final BindingManager bindingManager = ServiceLoader.load(BindingManager.class).iterator().next();

        return new ContextImpl(entityManager, componentManager, templateManager, eventManager, groupManager, bindingManager);
    }
}
