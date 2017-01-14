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
