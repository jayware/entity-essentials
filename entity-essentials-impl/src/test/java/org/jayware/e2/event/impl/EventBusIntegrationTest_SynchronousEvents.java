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
import static org.jayware.e2.util.ReferenceType.STRONG;


public class EventBusIntegrationTest_SynchronousEvents
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
    public void test_that_all_synchronous_events_get_processed_in_their_threads_and_the_order_is_preserved()
    throws Exception
    {
        final int count = 100;
        final EventCounter eventCounter = new EventCounter();
        final List<Probe> probes = new ArrayList<Probe>();
        final CountDownLatch finishLatch = new CountDownLatch(count);
        final CountDownLatch startLatch = new CountDownLatch(1);

        eventManager.subscribe(context, new Handler(), STRONG);
        eventManager.subscribe(context, eventCounter);

        for (int i = 0; i < count; ++i)
        {
            final Probe probe = new Probe();
            probes.add(probe);

            final Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if (startLatch.await(10, SECONDS))
                        {
                            sleep(1 + new Random().nextInt(10));

                            eventManager.send(TestEventTypeA.class, param(ContextParam, context), param("probe", probe));
                            finishLatch.countDown();
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

        for (Probe probe : probes)
        {
            assertThat(probe.events).containsExactly(TestEventTypeA.class, TestEventTypeB.class, TestEventTypeC.class, TestEventTypeD.class);
            assertThat(probe.threads).containsExactly(probe.threads.peek(), probe.threads.peek(), probe.threads.peek(), probe.threads.peek());
        }

        assertThat(eventCounter.counter.get()).isEqualTo(4 * count);
    }

    public static class Handler
    {
        @Handle(TestEventTypeA.class)
        public void handleTestEventTypeA(Event event, @Param(ContextParam) Context context, @Param("probe") Probe probe)
        {
            probe.analyse(event);

            final EventManager eventManager = context.getService(EventManager.class);
            eventManager.send(TestEventTypeB.class, param(ContextParam, context), param("probe", probe));
        }

        @Handle(TestEventTypeB.class)
        public void handleTestEventTypeB(Event event, @Param(ContextParam) Context context, @Param("probe") Probe probe)
        {
            probe.analyse(event);

            final EventManager eventManager = context.getService(EventManager.class);
            eventManager.send(TestEventTypeC.class, param(ContextParam, context), param("probe", probe));
        }

        @Handle(TestEventTypeC.class)
        public void handleTestEventTypeC(Event event, @Param(ContextParam) Context context, @Param("probe") Probe probe)
        {
            probe.analyse(event);

            final EventManager eventManager = context.getService(EventManager.class);
            eventManager.send(TestEventTypeD.class, param(ContextParam, context), param("probe", probe));
        }

        @Handle(TestEventTypeD.class)
        public void handleTestEventTypeD(Event event, @Param(ContextParam) Context context, @Param("probe") Probe probe)
        {
            probe.analyse(event);
        }
    }

    public static class EventCounter
    {
        private final AtomicInteger counter = new AtomicInteger();

        @Handle(RootEvent.class)
        public void count()
        {
            counter.incrementAndGet();
        }
    }

    public static class Probe
    {
        private final Queue<Class<? extends EventType>> events = new ConcurrentLinkedQueue<Class<? extends EventType>>();
        private final Queue<Integer> threads = new ConcurrentLinkedQueue<Integer>();

        public void analyse(Event event)
        {
            events.add(event.getType());
            threads.add(Thread.currentThread().hashCode());
        }
    }
}
