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
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ResultSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class QueryIntegrationTest
{
    public static final String TEST_PARAM = "fubar";
    public static final Integer TEST_VALUE = 42;

    private EventManager testee;

    private Context testContext;
    private TestHandler testHandler;

    @BeforeEach
    public void setUp()
    {
        testContext = ContextProvider.getInstance().createContext();
        testHandler = new TestHandler();

        testee = testContext.getService(EventManager.class);
        testee.subscribe(testContext, testHandler);
    }

    @AfterEach
    public void tearDown()
    {
        testContext.dispose();
    }

    @Test
    public void test()
    throws InterruptedException
    {
        final int count = 100;
        final List<Computation> computations = new CopyOnWriteArrayList<Computation>();
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch finishLatch = new CountDownLatch(count);

        for (int i = 0; i < count; ++i)
        {
            final Computation computation = new Computation((int) (Math.random() * 100), (int) (Math.random() * 100));
            computations.add(computation);

            final Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        if (startLatch.await(10, SECONDS))
                        {
                            sleep(10 + new Random().nextInt(100));

                            final Query testQuery = testee.createQuery(TestQueryEvent.class,
                                                        param(ContextParam, testContext),
                                                        param("computation", computation)
                                                    );

                            final ResultSet resultSet = testee.query(testQuery);
                            computation.result.set(((Integer) resultSet.get("result")).intValue());
                        }
                    }
                    catch (InterruptedException ignored)
                    {

                    }

                    finishLatch.countDown();
                }
            });

            thread.start();
            sleep(5);

        }

        startLatch.countDown();

        assertThat(finishLatch.await(10, SECONDS)).isTrue();

        for (Computation computation : computations)
        {
            assertThat(computation.result.get()).isEqualTo(computation.expectation);
        }
    }

    public interface TestQueryEvent
    extends RootEvent
    {

    }

    public class TestHandler
    {
        @Handle(TestQueryEvent.class)
        public void handle(Query query, @Param("computation") Computation computation)
        {
            query.result("result", computation.inputA + computation.inputB);
        }
    }

    public static class Computation
    {
        private final int inputA;
        private final int inputB;
        private final int expectation;
        private final AtomicInteger result;

        public Computation(int a, int b)
        {
            inputA = a;
            inputB = b;
            expectation = a + b;
            result = new AtomicInteger();
        }
    }
}
