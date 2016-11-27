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
package org.jayware.e2.examples;

import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.examples.Quickstart.ExampleComponent;


public class ComponentCreationExample
{
    public static void main(String[] args) {

        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EntityManager entityManager = context.getService(EntityManager.class);
        ComponentManager componentManager = context.getService(ComponentManager.class);

        EntityRef ref = entityManager.createEntity(context);
        ExampleComponent component;

        /* Create a detached component, which is initially not assigned to an entity. */
        component = componentManager.createComponent(context, ExampleComponent.class);

        /*
         * To attach a component to an entity the ComponentManager offers an addComponent operation
         * which accepts an instance of a previous created component.
         */
        componentManager.addComponent(ref, component);

        /* Create a component which is automatically assigned to an entity. */
        component = componentManager.addComponent(ref, ExampleComponent.class);

        component.setText("Hello World");

        /* Shutdown everything */
        context.dispose();
    }
}
