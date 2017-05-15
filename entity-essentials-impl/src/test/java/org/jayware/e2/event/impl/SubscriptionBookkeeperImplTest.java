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
import org.jayware.e2.event.api.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SubscriptionBookkeeperImplTest
{
    private SubscriptionBookkeeperImpl testee;

    private @Mocked Subscription testSubscriptionA;
    private @Mocked Subscription testSubscriptionB;
    private @Mocked Object testSubscriberA;
    private @Mocked Object testSubscriberB;

    @BeforeEach
    public void setUp()
    {
        testee = new SubscriptionBookkeeperImpl();
    }

    @Test
    public void test_that_a_bookkeeper_keeps_only_one_subscription_per_subscriber()
    {
        new Expectations() {{
           testSubscriptionA.getSubscriber(); result = testSubscriberA;
           testSubscriptionB.getSubscriber(); result = testSubscriberA;
        }};

        testee.subscribe(testSubscriptionA);
        testee.subscribe(testSubscriptionB);

        assertThat(testee.subscriptions()).containsExactly(testSubscriptionA);
    }

    @Test
    public void test_that_a_bookkeeper_does_does_not_return_a_previously_unsubscribed_subscription()
    {
        new Expectations() {{
            testSubscriptionA.getSubscriber(); result = testSubscriberA;
            testSubscriptionB.getSubscriber(); result = testSubscriberB;
        }};

        testee.subscribe(testSubscriptionA);
        testee.subscribe(testSubscriptionB);

        testee.unsubscribe(testSubscriberA);

        assertThat(testee.subscriptions()).containsExactly(testSubscriptionB);
    }

    @Test
    public void test_that_a_bookkeeper_does_not_return_any_Subscriptions_if_now_one_is_subscribed()
    {
        assertThat(testee.subscriptions()).isEmpty();
    }

    @Test
    public void test_that_a_bookkeeper_does_not_return_any_subscriptions_after_clear_has_been_called()
    {
        new Expectations() {{
            testSubscriptionA.getSubscriber(); result = testSubscriberA;
            testSubscriptionB.getSubscriber(); result = testSubscriberB;
        }};

        testee.subscribe(testSubscriptionA);
        testee.subscribe(testSubscriptionB);

        testee.clear();

        assertThat(testee.subscriptions()).isEmpty();
    }
}