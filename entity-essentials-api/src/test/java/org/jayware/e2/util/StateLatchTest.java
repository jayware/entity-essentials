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
package org.jayware.e2.util;


import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.util.StateLatchTest.TestStates.StateA;
import static org.jayware.e2.util.StateLatchTest.TestStates.StateB;
import static org.jayware.e2.util.StateLatchTest.TestStates.StateC;


public class StateLatchTest
{
    private StateLatch<TestStates> testee;

    @BeforeMethod
    public void setUp()
    {
        testee = new StateLatch<TestStates>(TestStates.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_newStateLatch_ThrowsIllegalArgumentExceptionIfEnumTypeIsNull()
    {
        new StateLatch<TestStates>(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_newStateLatch_ThrowsIllegalArgumentExceptionIfInitialValueIsNull()
    {
        new StateLatch<TestStates>(TestStates.class, null);
    }

    @Test
    public void test_newStateLatch()
    {
        new StateLatch<TestStates>(TestStates.class);
        new StateLatch<TestStates>(TestStates.class, StateA);
    }

    @Test
    public void test_newStateLatch_DefaultInitialValue()
    {
        assertThat(new StateLatch<TestStates>(TestStates.class).getState()).isEqualTo(StateA);
    }

    @Test
    public void test_newStateLatch_WithInitialValue()
    {
        assertThat(new StateLatch<TestStates>(TestStates.class, StateC).getState()).isEqualTo(StateC);
    }

    @Test
    public void test_hasState_ThrowsNoExceptionIfNullIsPassed()
    {
        testee.hasState(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_await_ThrowsIllegalArgumentExceptionIfNullIsPassed()
    throws InterruptedException
    {
        testee.await(null);
    }

    @Test
    public void test_await_DoesNotBlockIfLatchIsAlreadyInPassedState()
    throws InterruptedException
    {
        final AtomicBoolean blockedFlag = new AtomicBoolean(true);
        final Thread blockedThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                testee.await(StateA);
                blockedFlag.set(false);
            }
        });

        blockedThread.start();
        sleep(250);

        try
        {
            assertThat(blockedFlag.get()).isFalse();
        }
        finally
        {
            blockedThread.interrupt();
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_awaitWithTimeout_ThrowsIllegalArgumentExceptionIfNullIsPassed()
    throws InterruptedException
    {
        testee.await(null, 1, SECONDS);
    }

    @Test
    public void test_awaitWithTimeout_ReturnsFalseWhenTimeoutElapses()
    throws InterruptedException
    {
        assertThat(testee.await(StateC, 1, SECONDS)).isFalse();
    }

    @Test
    public void test_signal()
    {
        testee.signal(StateB);
        assertThat(testee.getState()).isEqualTo(StateB);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_signal_ThrowsIllegalArgumentExceptionIfNullIsPassed()
    {
        testee.signal(null);
    }

    @Test
    public void test_signal_ReleasesWaitingThreads()
    throws InterruptedException
    {
        final int numberOfThreads = 3;
        final CountDownLatch latch = new CountDownLatch(numberOfThreads);

        final Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; ++i)
        {
            threads[i] = new Thread(new AwaitStateAndCountDownRunnable(latch, TestStates.values()[i]));
            threads[i].start();
            sleep(100);
        }

        testee.signal(StateC);

        final boolean result = latch.await(1, SECONDS);

        try
        {
            assertThat(result).isTrue();
        }
        finally
        {
            if (!result)
            {
                for (Thread thread : threads)
                {
                    thread.interrupt();
                }
            }
        }
    }

    @Test
    public void test_signal_ChangesState()
    {
        assertThat(testee.getState()).isEqualTo(StateA);
        testee.signal(StateB);
        assertThat(testee.getState()).isEqualTo(StateB);
    }

    public enum TestStates
    {
        StateA,
        StateB,
        StateC
    }

    private class AwaitStateAndCountDownRunnable
    implements Runnable
    {
        private final CountDownLatch myLatch;
        private final TestStates myState;

        public AwaitStateAndCountDownRunnable(CountDownLatch latch, TestStates state)
        {
            myLatch = latch;
            myState = state;
        }

        @Override
        public void run()
        {
            testee.await(myState);
            myLatch.countDown();
        }
    }
}
