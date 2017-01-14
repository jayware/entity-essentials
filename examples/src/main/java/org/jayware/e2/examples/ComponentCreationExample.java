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
