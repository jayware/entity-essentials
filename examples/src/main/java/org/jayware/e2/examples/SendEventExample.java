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
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;

import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class SendEventExample
{
    public static void main(String[] args) {

        // Create a context and obtain required managers
        Context context = ContextProvider.getInstance().createContext();
        EventManager eventManager = context.getService(EventManager.class);

        // Instantiate a handler object
        ExampleHandler handler = new ExampleHandler();

        // Subscribe the handler to receive events
        eventManager.subscribe(context, handler);

        /* Send an event */
        eventManager.send(
            ExampleEvent.class,
            param(ContextParam, context),
            param("message", "Hello World!")
        );

        /* Shutdown everything */
        context.dispose();
    }

    /* A custom event type can be defined by declaring an interface and inherit the RootEvent */
    public interface ExampleEvent
    extends RootEvent
    {

    }

    /*
     * An event handler is a class which contains at least
     * one method annotated with the  @Handle annotation.
     */
    public static class ExampleHandler
    {
        /*
         * The @Handle annotation accepts the type of the Event which this handler method consumes.
         */
        @Handle(ExampleEvent.class)
        public void printMessage(@Param(value = "message") String message)
        {
            System.out.println("\n" + message + "\n");
        }
    }
}
