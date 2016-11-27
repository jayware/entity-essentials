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
