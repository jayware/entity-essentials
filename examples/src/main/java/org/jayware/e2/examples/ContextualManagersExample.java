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
import org.jayware.e2.component.api.ContextualComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.ContextualEntityManager;
import org.jayware.e2.entity.api.EntityManager;


public class ContextualManagersExample
{
    public static void main(String[] args) {

        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EntityManager entityManager = context.getService(EntityManager.class);
        ComponentManager componentManager = context.getService(ComponentManager.class);

        /* In many cases there is only one context. Therefore it is impractical to pass the context in, again and again. */
        entityManager.createEntity(context);
        componentManager.prepareComponent(context, Quickstart.ExampleComponent.class);

        /* To avoid such situations, it is possible to create XXXManager which are bound to a specific context.*/
        ContextualEntityManager contextualEntityManager = entityManager.asContextual(context);
        ContextualComponentManager contextualComponentManager = componentManager.asContextual(context);

        /* These managers offer an api which does not requires a context paramter. */
        contextualEntityManager.createEntity();
        contextualComponentManager.prepareComponent(Quickstart.ExampleComponent.class);

        /* Shutdown everything */
        context.dispose();
    }
}
