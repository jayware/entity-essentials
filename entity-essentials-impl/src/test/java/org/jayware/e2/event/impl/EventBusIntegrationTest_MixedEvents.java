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
package org.jayware.e2.event.impl;

import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.util.ReferenceType.Strong;


public class EventBusIntegrationTest_MixedEvents
{
    private Context context;
    private EventManager eventManager;

    @BeforeMethod
    public void setUp()
    {
        context = ContextProvider.getInstance().createContext();
        eventManager = context.getService(EventManager.class);
    }

    @AfterMethod
    public void tearDown()
    {
        context.dispose();
    }

    @Test
    public void test_that_all_events_get_processed_in_any_order()
    throws Exception
    {
        final int count = 100;
        final CountDownLatch finishLatch = new CountDownLatch(4 * count);
        final CountDownLatch startLatch = new CountDownLatch(1);

        eventManager.subscribe(context, new Handler(), Strong);

        for (int i = 0; i < count; ++i)
        {
            final Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if (startLatch.await(10, SECONDS))
                        {
                            sleep(new Random().nextInt(10));
                            eventManager.post(TestEventTypeA.class, param(ContextParam, context), param("latch", finishLatch));
                        }
                    }
                    catch (InterruptedException ignored)
                    {

                    }
                }
            });

            thread.start();
            sleep(10);
        }

        startLatch.countDown();

        assertThat(finishLatch.await(10, SECONDS)).isTrue();
    }

    public static class Handler
    {
        @Handle(TestEventTypeA.class)
        public void handleTestEventTypeA(Event event, @Param(ContextParam) Context context, @Param("latch") CountDownLatch latch)
        {
            latch.countDown();

            final EventManager eventManager = context.getService(EventManager.class);
            eventManager.send(TestEventTypeB.class, param(ContextParam, context), param("latch", latch));
        }

        @Handle(TestEventTypeB.class)
        public void handleTestEventTypeB(Event event, @Param(ContextParam) Context context, @Param("latch") CountDownLatch latch)
        {
            latch.countDown();

            final EventManager eventManager = context.getService(EventManager.class);
            eventManager.post(TestEventTypeC.class, param(ContextParam, context), param("latch", latch));
        }

        @Handle(TestEventTypeC.class)
        public void handleTestEventTypeC(Event event, @Param(ContextParam) Context context, @Param("latch") CountDownLatch latch)
        {
            latch.countDown();

            final EventManager eventManager = context.getService(EventManager.class);
            eventManager.send(TestEventTypeD.class, param(ContextParam, context), param("latch", latch));
        }

        @Handle(TestEventTypeD.class)
        public void handleTestEventTypeD(Event event, @Param(ContextParam) Context context, @Param("latch") CountDownLatch latch)
        {
            latch.countDown();
        }
    }
}
