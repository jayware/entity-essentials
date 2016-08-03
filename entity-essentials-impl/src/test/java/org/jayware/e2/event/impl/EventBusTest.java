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
