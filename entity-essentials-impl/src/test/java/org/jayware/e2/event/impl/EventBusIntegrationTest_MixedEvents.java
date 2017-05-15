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

import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.util.ReferenceType.STRONG;


public class EventBusIntegrationTest_MixedEvents
{
    private Context context;
    private EventManager eventManager;

    @BeforeEach
    public void setUp()
    {
        context = ContextProvider.getInstance().createContext();
        eventManager = context.getService(EventManager.class);
    }

    @AfterEach
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

        eventManager.subscribe(context, new Handler(), STRONG);

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
