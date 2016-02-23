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
        testee = new StateLatch<>(TestStates.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_newStateLatch_ThrowsIllegalArgumentExceptionIfEnumTypeIsNull()
    {
        new StateLatch<>(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_newStateLatch_ThrowsIllegalArgumentExceptionIfInitialValueIsNull()
    {
        new StateLatch<>(TestStates.class, null);
    }

    @Test
    public void test_newStateLatch()
    {
        new StateLatch<>(TestStates.class);
        new StateLatch<>(TestStates.class, StateA);
    }

    @Test
    public void test_newStateLatch_DefaultInitialValue()
    {
        assertThat(new StateLatch<>(TestStates.class).getState()).isEqualTo(StateA);
    }

    @Test
    public void test_newStateLatch_WithInitialValue()
    {
        assertThat(new StateLatch<>(TestStates.class, StateC).getState()).isEqualTo(StateC);
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
                try
                {
                    testee.await(StateA);
                    blockedFlag.set(false);
                }
                catch (InterruptedException ignored) {}
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
            try
            {
                testee.await(myState);
                myLatch.countDown();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
