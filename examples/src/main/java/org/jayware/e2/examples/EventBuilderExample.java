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
import org.jayware.e2.event.api.EventBuilder;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;

import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.examples.EventBuilderExample.ExampleEvent.ParamA;
import static org.jayware.e2.examples.EventBuilderExample.ExampleEvent.ParamB;
import static org.jayware.e2.util.ReferenceType.Strong;


public class EventBuilderExample
{
    public static void main(String[] args) {

        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EventManager eventManager = context.getService(EventManager.class);

        /* Subscribe an event handler to see an output */
        eventManager.subscribe(context, new ExampleHandler(), Strong);

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
