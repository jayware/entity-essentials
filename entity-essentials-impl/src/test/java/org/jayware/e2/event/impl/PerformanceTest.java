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
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.util.ReferenceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class PerformanceTest
{
    private static final int RUNS = 471173;

    private Context context;
    private EventManager eventManager;
    private IncrementHandler handler;

    @BeforeEach
    public void setUp()
    {
        context = ContextProvider.getInstance().createContext();
        eventManager = context.getService(EventManager.class);
        handler = new IncrementHandler(RUNS);

        eventManager.subscribe(context, handler, ReferenceType.STRONG);
    }

    @AfterEach
    public void tearDown()
    {
        context.dispose();
    }

    @Test
    public void test()
    throws Exception
    {
        final long startTime = System.nanoTime();

        for (int i = 0; i < RUNS; ++i)
        {
            eventManager.post(IncrementEvent.class, param(ContextParam, context));
        }

        handler.latch.await();

        System.out.println("==============");
        System.out.println(" Time: " + String.valueOf((System.nanoTime() - startTime) / 1000000) + "ms");
        System.out.println("==============");
    }

    public static class IncrementHandler
    {
        private final CountDownLatch latch;

        public IncrementHandler(int count)
        {
            this.latch = new CountDownLatch(count);
        }

        @Handle(IncrementEvent.class)
        public void increment()
        {
            latch.countDown();
        }
    }

    public interface IncrementEvent extends EventType.RootEvent {}
}
