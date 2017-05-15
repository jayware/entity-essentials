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
import org.jayware.e2.event.impl.SubscriptionFactoryImpl.SubscriptionImpl_StrongReference;
import org.jayware.e2.event.impl.SubscriptionFactoryImpl.SubscriptionImpl_WeakReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.util.ReferenceType.STRONG;
import static org.jayware.e2.util.ReferenceType.WEAK;


public class SubscriptionFactoryImplTest
{
    private SubscriptionFactoryImpl testee;

    private @Mocked Object testSubscriber;
    private @Mocked EventDispatcher testDispatcher;

    @BeforeEach
    public void setUp()
    {
        testee = new SubscriptionFactoryImpl();
    }

    @Test
    public void test_createSubscription_Returns_the_expected_Subscription_for_the_passed_ReferencesType()
    {
        assertThat(testee.createSubscription(testSubscriber, STRONG, null, testDispatcher))
            .isInstanceOf(SubscriptionImpl_StrongReference.class);

        assertThat(testee.createSubscription(testSubscriber, WEAK, null, testDispatcher))
            .isInstanceOf(SubscriptionImpl_WeakReference.class);
    }
}