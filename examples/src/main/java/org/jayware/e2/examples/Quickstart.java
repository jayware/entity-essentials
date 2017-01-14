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


import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;


public class Quickstart
{
    public static void main(String[] args) {

        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EntityManager entityManager = context.getService(EntityManager.class);
        ComponentManager componentManager = context.getService(ComponentManager.class);

        /* Create an entity */
        final EntityRef ref = entityManager.createEntity(context);

        /* Add a component to the entity */
        componentManager.addComponent(ref, ExampleComponent.class);

        /* Lookup a component */
        ExampleComponent cmp = componentManager.getComponent(ref, ExampleComponent.class);

        /* Change the component's properties */
        cmp.setText("Fubar!");
        cmp.setTextSize(14);

        /* Commit changes */
        cmp.pushTo(ref);

        /* Shutdown everything */
        context.dispose();
    }

    /* Define a custom component by a java interface */
    public interface ExampleComponent extends Component
    {
        /* Define properties by declaration of getters and setters  */
        String getText();

        void setText(String text);

        int getTextSize();

        void setTextSize(int size);
    }
}
