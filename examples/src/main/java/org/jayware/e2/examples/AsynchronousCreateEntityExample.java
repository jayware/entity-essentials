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


import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.ResultSet;

import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityRefParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class AsynchronousCreateEntityExample
{
    public static void main(String[] args) {

        // Create a context and obtain required managers
        Context context = ContextProvider.getInstance().createContext();
        EventManager eventManager = context.getService(EventManager.class);
        EntityManager entityManager = context.getService(EntityManager.class);

        EntityRef ref;

        /*
         * The usual way to crate an entity. The createEntity operation
         * acts synchronously, so it blocks until the entity is created.
         */
        ref = entityManager.createEntity(context);

        /* But it is also possible to create an entity asynchronously by firing a query. */
        ResultSet resultSet = eventManager.query(
            CreateEntityEvent.class,
            param(ContextParam, context)
        );

        /* The resultSet will contain the newly created entity. */
        ref = resultSet.get(EntityRefParam);

        System.out.println("\nEntity: " + ref + "\n");

        /* Shutdown everything */
        context.dispose();
    }
}
