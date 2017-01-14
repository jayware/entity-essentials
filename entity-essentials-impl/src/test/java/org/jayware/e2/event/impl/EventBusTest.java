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

import mockit.Mocked;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Handle;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.random;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.util.ReferenceType.Weak;


public class EventBusTest
{
    private static final long TIMEOUT_IN_SECONDS = 10;

    private @Mocked Context context;

    @Test
    public void test_that_an_EventBus_can_be_disposed()
    throws Exception
    {
        final int eventCount = 100 + (int) (random() * 100);
        final CountDownLatch latch = new CountDownLatch(1);
        final EventBus eventBus = new EventBus(context);
        final Handler handler = new Handler();

        eventBus.subscribe(handler, Weak, new EventFilter[0]);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Warm-up
                for (int i = 0; i < eventCount; ++i)
                {
                    final Event event = EventBuilderImpl.createEventBuilder(TestEventTypeA.class).set(ContextParam).to(context).build();
                    eventBus.send(event);
                }

                eventBus.dispose(context);
                latch.countDown();
            }
        }).start();

        assertThat(latch.await(TIMEOUT_IN_SECONDS, SECONDS))
            .withFailMessage("Disposing the EventBus takes longer than %ss!", TIMEOUT_IN_SECONDS)
            .isTrue();

        assertThat(handler.hitCount.get())
            .withFailMessage("Not all events get processed! %s events got processed, expected %s!", handler.hitCount.get(), eventCount)
            .isEqualTo(eventCount);
    }

    public static class Handler
    {
        private final AtomicInteger hitCount = new AtomicInteger();

        @Handle(TestEventTypeA.class)
        public void handle()
        {
            hitCount.incrementAndGet();
        }
    }
}
