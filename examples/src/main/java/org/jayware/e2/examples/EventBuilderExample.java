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
import org.jayware.e2.event.api.EventBuilder;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;

import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.examples.EventBuilderExample.ExampleEvent.ParamA;
import static org.jayware.e2.examples.EventBuilderExample.ExampleEvent.ParamB;
import static org.jayware.e2.util.ReferenceType.STRONG;


public class EventBuilderExample
{
    public static void main(String[] args) {

        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EventManager eventManager = context.getService(EventManager.class);

        /* Subscribe an event handler to see an output */
        eventManager.subscribe(context, new ExampleHandler(), STRONG);

        /* The EventBuilder is useful to build similar events of the same type. */
        final EventBuilder builder = eventManager.createEvent(ExampleEvent.class);

        /* The EventBuilder offers operations to set parameters. */
        builder.set(ContextParam).to(context);
        builder.set(ParamA).to("foo");
        builder.set(ParamB).to(42);

        /* The EventManager accepts an EventBuilder instance and uses its build() operation to get the event to send. */
        eventManager.send(builder);

        /* It is possible to modify all or some of the parameters while keeping the others. */
        builder.set(ParamA).to("bar");
        builder.set(ParamB).to(73);

        /* And finally sending a slightly modified event again. */
        eventManager.send(builder);

        /* Shutdown everything */
        context.dispose();
    }

    public interface ExampleEvent
    extends RootEvent
    {
        String ParamA = "paramA";
        String ParamB = "paramB";
    }

    public static class ExampleHandler
    {
        @Handle(ExampleEvent.class)
        public void handle(@Param(ParamA) String paramA, @Param(ParamB) int paramB)
        {
            System.out.println("\n" + paramA + " " + paramB);
        }
    }
}
