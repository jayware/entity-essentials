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
