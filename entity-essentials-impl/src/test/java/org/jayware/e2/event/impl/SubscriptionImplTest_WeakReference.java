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
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SubscriptionImplTest_WeakReference
{
    private SubscriptionImpl_WeakReference testee;

    private @Mocked Object testSubscriber;
    private @Mocked EventDispatcher testDispatcher;
    private @Mocked EventFilter testFilterA;
    private @Mocked EventFilter testFilterB;

    @BeforeMethod
    public void setUp()
    throws Exception
    {
        testee = new SubscriptionImpl_WeakReference(testSubscriber, testDispatcher, new EventFilter[] {testFilterA, testFilterB});
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
