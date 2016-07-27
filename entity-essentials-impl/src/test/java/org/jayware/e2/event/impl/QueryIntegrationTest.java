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
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.ResultSet;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
import static org.jayware.e2.event.api.Query.State.Success;


public class QueryIntegrationTest
{
    public static final String TEST_PARAM = "fubar";
    public static final Integer TEST_VALUE = 42;

    private EventManager testee;

    private Context testContext;
    private TestHandler testHandler;

    @BeforeMethod
    public void setUp()
    {
        testContext = ContextProvider.getInstance().createContext();
        testHandler = new TestHandler();

        testee = testContext.getService(EventManager.class);
        testee.subscribe(testContext, testHandler);
    }

    @AfterMethod
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
                            computation.result.set(resultSet.get("result"));
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
