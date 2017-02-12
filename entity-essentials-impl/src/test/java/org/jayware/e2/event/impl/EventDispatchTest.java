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

import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventDispatchException;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Subscription;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class EventDispatchTest
{
    private EventDispatch testee;

    private @Mocked Context testContext;
    private @Mocked Event testEvent;

    private @Mocked Subscription testSubscriptionA,
                                 testSubscriptionB;

    private @Mocked EventFilter testEventFilterA,
                                testEventFilterB,
                                testEventFilterC;

    private @Mocked EventDispatcher testDispatcher;
    private @Mocked Object testSubscriber;

    @BeforeMethod
    public void setUp()
    {
        testee = new EventDispatch(testContext, testEvent, Arrays.<Subscription>asList(testSubscriptionA, testSubscriptionB));
    }


    @Test
    public void test_that_runEventDispatch_Should_not_call_the_dispatcher_if_it_does_not_accept_that_type_of_event()
    {
        new Expectations() {{
            testDispatcher.accepts((Class) any); result = false;
            testDispatcher.dispatch((Event) any, any); times = 0;
        }};

        testee.runEventDispatch(testDispatcher, testSubscriber, new EventFilter[0]);
    }

    @Test
    public void test_that_runEventDispatch_Should_not_call_the_dispatcher_if_one_of_the_filters_reject_that_event()
    {
        new Expectations() {{
            testDispatcher.accepts((Class) any); result = true;
            testDispatcher.dispatch((Event) any, any); times = 0;
            testEventFilterA.accepts(testContext, testEvent); result = false;
        }};

        testee.runEventDispatch(testDispatcher, testSubscriber, new EventFilter[]{testEventFilterA});
    }

    @Test
    public void test_that_runEventDispatch_Throws_an_EventDispatchException_if_the_dispatch_failed_with_an_exception()
    {
        new Expectations() {{
            testDispatcher.accepts((Class) any); result = true;
            testDispatcher.dispatch((Event) any, any); result = new RuntimeException("Dispatch fucked-up");
            testEvent.getType(); result = TestEventType.class;
        }};

        try
        {
            testee.runEventDispatch(testDispatcher, testSubscriber, new EventFilter[0]);
            fail("Expected not to reach this line because of an EventDispatchException!");
        }
        catch (EventDispatchException ignored) { }
        catch (Exception e)
        {
            fail(format("Expected an exception of type: '%s' but caught: '%s'", EventDispatchException.class, e.getClass()), e);
        }
    }

    @Test
    public void test_that_passedFilters_Calls_every_EventFilter_and_returns_true_if_none_of_the_Filters_returns_false()
    {
        new Expectations() {{
            testEventFilterA.accepts(testContext, testEvent); result = true;
            testEventFilterB.accepts(testContext, testEvent); result = true;
            testEventFilterC.accepts(testContext, testEvent); result = true;
        }};

        assertThat(testee.passedFilters(new EventFilter[]{testEventFilterA, testEventFilterB, testEventFilterC})).isTrue();
    }

    @Test
    public void test_that_passedFilters_Aborts_and_returns_false_if_the_first_EventFilter_returns_false()
    {
        new Expectations() {{
            testEventFilterA.accepts((Context) any, (Event) any); result = false;
            testEventFilterB.accepts(testContext, testEvent); times = 0;
            testEventFilterC.accepts(testContext, testEvent); times = 0;
        }};

        assertThat(testee.passedFilters(new EventFilter[]{testEventFilterA, testEventFilterB, testEventFilterC})).isFalse();
    }

    @Test
    public void test_that_passedFilters_Aborts_and_returns_false_if_a_EventFilter_returns_false()
    {
        new Expectations() {{
            testEventFilterA.accepts((Context) any, (Event) any); result = true;
            testEventFilterB.accepts((Context) any, (Event) any); result = false;
            testEventFilterC.accepts((Context) any, (Event) any); times = 0;
        }};

        assertThat(testee.passedFilters(new EventFilter[]{testEventFilterA, testEventFilterB, testEventFilterC})).isFalse();
    }

    @Test
    public void test_that_passedFilters_Aborts_and_returns_false_if_the_last_EventFilter_returns_false()
    {
        new Expectations() {{
            testEventFilterA.accepts((Context) any, (Event) any); result = true;
            testEventFilterB.accepts((Context) any, (Event) any); result = true;
            testEventFilterC.accepts((Context) any, (Event) any); result = false;
        }};

        assertThat(testee.passedFilters(new EventFilter[]{testEventFilterA, testEventFilterB, testEventFilterC})).isFalse();
    }

    @Test
    public void test_that_passedFilters_Throws_an_EventDispatchException_if_one_of_the_EventFilters_fails_with_an_Exception()
    {
        new Expectations() {{
            testEventFilterA.accepts(testContext, testEvent); result = true;
            testEventFilterB.accepts(testContext, testEvent); result = new RuntimeException("Filter fucked-up");
        }};

        try
        {
            testee.passedFilters(new EventFilter[]{testEventFilterA, testEventFilterB, testEventFilterC});
            fail("Expected not to reach this line because of an EventDispatchException!");
        }
        catch (EventDispatchException ignored) { }
        catch (Exception e)
        {
            fail(format("Expected an exception of type: '%s' but caught: '%s'", EventDispatchException.class, e.getClass()), e);
        }
    }

    @Test
    public void test_that_getEvent_Returns_the_Event_passed_during_construction()
    {
        assertThat(testee.getEvent()).isEqualTo(testEvent);
    }

    @Test
    public void test_that_getSubscriptions_Returns_the_Subscriptions_passed_during_construction()
    {
        assertThat(testee.getSubscriptions()).containsExactly(testSubscriptionA, testSubscriptionB);
    }

    private interface TestEventType extends EventType.RootEvent {}
}