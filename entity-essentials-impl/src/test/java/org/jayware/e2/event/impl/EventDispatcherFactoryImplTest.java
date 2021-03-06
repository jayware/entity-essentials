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
package org.jayware.e2.event.impl;


import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.IllegalHandlerException;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Presence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class EventDispatcherFactoryImplTest
{
    private EventDispatcherFactoryImpl testee;

    private TestSubscriber testSubscriber;

    @BeforeEach
    public void setup()
    {
        testee = new EventDispatcherFactoryImpl();
        testSubscriber = new TestSubscriber();
    }

    @Test
    public void test_createEventDispatcher_HappyFlow()
    {
        final EventDispatcher dispatcher = testee.createEventDispatcher(testSubscriber.getClass());

        assertTrue(dispatcher.accepts(TestEventTypeA.class));
        assertTrue(dispatcher.accepts(TestEventTypeB.class));
        assertTrue(dispatcher.accepts(TestEventTypeC.class));
        assertFalse(dispatcher.accepts(TestEventTypeD.class));
    }

    @Test
    public void test_createEventDispatcher_FailsIfSubscriberIsNotAccessible()
    {
        assertThrows(IllegalHandlerException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createEventDispatcher(TestSubscriber_Unaccessible.class);
            }
        });
    }

    @Test
    public void test_createEventDispatcher_FailsIfAnUnknownParameterIsSpecified()
    {
        assertThrows(IllegalHandlerException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createEventDispatcher(TestSubscriber_UnknownParameter.class);
            }
        });
    }

    @Test
    public void test_createEventDispatcher_ForHandlerWithPrimitiveParameter()
    {
        testee.createEventDispatcher(TestHandler_WithPrimitiveParameter.class);
    }

    public static class TestSubscriber
    {
        @Handle({TestEventTypeA.class, TestEventTypeB.class})
        public void handle1st(Event event,
                              @Param(value = "foo", presence = Presence.Optional) String foo,
                              @Param("bar") String bar)
        {

        }

        @Handle({TestEventTypeA.class, TestEventTypeB.class, TestEventTypeC.class})
        public void handle2nd(@Param(value = "foo", presence = Presence.Optional) EntityRef ref)
        {

        }

        @Handle({TestEventTypeB.class, TestEventTypeB.class, TestEventTypeB.class})
        public void handleA()
        {

        }
    }

    public static class TestHandler_WithPrimitiveParameter
    {
        @Handle(TestEventTypeA.class)
        public void handle(@Param("boolean-param") boolean param) {}

        @Handle(TestEventTypeA.class)
        public void handle(@Param("byte-param") byte param) {}

        @Handle(TestEventTypeA.class)
        public void handle(@Param("short-param") short param) {}

        @Handle(TestEventTypeA.class)
        public void handle(@Param("int-param") int param) {}

        @Handle(TestEventTypeA.class)
        public void handle(@Param("long-param") long param) {}

        @Handle(TestEventTypeA.class)
        public void handle(@Param("float-param") float param) {}

        @Handle(TestEventTypeA.class)
        public void handle(@Param("double-param") double param) {}
    }

    private static class TestSubscriber_Unaccessible
    {

    }

    public static class TestSubscriber_UnknownParameter
    {
        @Handle(TestEventTypeA.class)
        public void handle(Object object)
        {
        }
    }
}
