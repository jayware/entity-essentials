/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
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
package org.jayware.e2.event.impl;


import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.IllegalHandlerException;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Presence;
import org.jayware.e2.event.api.Subscriber;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


public class EventDispatcherFactoryImplTest
{
    private EventDispatcherFactoryImpl testee;

    private TestSubscriber testSubscriber;

    @BeforeMethod
    public void setup()
    {
        testee = new EventDispatcherFactoryImpl();
        testSubscriber = new TestSubscriber();
    }

    @Test
    public void testCreateEventDispatcher_HappyFlow()
    {
        final EventDispatcher dispatcher = testee.createEventDispatcher(testSubscriber.getClass());

        assertTrue(dispatcher.accepts(TestEventTypeA.class));
        assertTrue(dispatcher.accepts(TestEventTypeB.class));
        assertTrue(dispatcher.accepts(TestEventTypeC.class));
        assertFalse(dispatcher.accepts(TestEventTypeD.class));
    }

    @Test
    public void testCreateEventDispatcher_FailsIfSubscriberIsNotAccessible()
    {
        try
        {
            testee.createEventDispatcher(TestSubscriber_Unaccessible.class);

            fail("Exception expected!");
        }
        catch (IllegalHandlerException e)
        {

        }
    }

    @Test
    public void testCreateEventDispatcher_FailsIfNoEventParameterIsSpecified()
    {
        try
        {
            testee.createEventDispatcher(new Subscriber()
            {
                @Handle(TestEventTypeA.class)
                public void handle()
                {
                }
            }.getClass());

            fail("Exception expected!");
        }
        catch (IllegalHandlerException ignored)
        {

        }
    }

    @Test(expectedExceptions = IllegalHandlerException.class)
    public void testCreateSubscription_FailsIfAnUnknownParameterIsSpecified()
    {
        testee.createEventDispatcher(new Subscriber()
        {
            @Handle(TestEventTypeA.class)
            public void handle(Object obj)
            {
            }
        }.getClass());
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
        public void handle2nd(@Param(value = "foo", presence = Presence.Optional)EntityRef ref)
        {

        }

        @Handle({TestEventTypeB.class, TestEventTypeB.class, TestEventTypeB.class})
        public void handleA()
        {

        }
    }

    private static class TestSubscriber_Unaccessible
    {

    }
}
