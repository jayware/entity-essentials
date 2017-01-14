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
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SubscriptionImplTest_StrongReference
{
    private SubscriptionImpl_StrongReference testee;

    private @Mocked Object testSubscriber;
    private @Mocked EventDispatcher testDispatcher;
    private @Mocked EventFilter testFilterA;
    private @Mocked EventFilter testFilterB;

    @BeforeMethod
    public void setUp()
    throws Exception
    {
        testee = new SubscriptionImpl_StrongReference(testSubscriber, testDispatcher, new EventFilter[] {testFilterA, testFilterB});
    }

    @Test
    public void test()
    throws Exception
    {
        assertThat(testee.getSubscriber()).isEqualTo(testSubscriber);
        assertThat(testee.getEventDispatcher()).isEqualTo(testDispatcher);
        assertThat(testee.getFilters()).containsExactlyInAnyOrder(testFilterA, testFilterB);
    }

    @Test
    public void test_isValid_Returns_true_for_a_newly_constructed_subscription()
    throws Exception
    {
        assertThat(testee.isValid()).isTrue();
    }

    @Test
    public void test_isValid_Returns_false_when_subscription_gets_invalidated()
    throws Exception
    {
        testee.invalidate();
        assertThat(testee.isValid()).isFalse();
    }
}
