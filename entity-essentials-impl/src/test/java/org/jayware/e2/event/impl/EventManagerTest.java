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
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Parameters;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.impl.EventManagerImpl.EVENT_BUS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EventManagerTest
{
    private EventManager testee;

    private Context context;
    private EventBus eventbus;

    @BeforeMethod
    public void setUp()
    {
        context = mock(Context.class);
        eventbus = mock(EventBus.class);

        when(context.isDisposed()).thenReturn(false);
        when(context.get(EVENT_BUS)).thenReturn(eventbus);

        testee = new EventManagerImpl();
    }

    @Test
    public void test_createEvent_ReturnsNotNull()
    {
        assertThat(testee.createEvent(TestEventTypeA.class)).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createEvent_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        testee.createEvent(null);
    }

    @Test
    public void test_createEvent_Parameters_ReturnsNotNull()
    {
        assertThat(testee.createEvent(TestEventTypeA.class, new Parameters())).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createEvent_Parameters_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        testee.createEvent(null, new Parameters());
    }

    @Test
    public void test_createEvent_ParameterArry_ReturnsNotNull()
    {
        assertThat(testee.createEvent(TestEventTypeA.class, param("foo", "bar"))).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createEvent_ParameterArry_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        testee.createEvent(null, param("foo", "bar"));
    }

    @Test
    public void test_createQuery_Parameters_ReturnsNotNull()
    {
        assertThat(testee.createQuery(TestEventTypeA.class, new Parameters())).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createQuery_Parameters_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        testee.createEvent(null, new Parameters());
    }

    @Test
    public void test_createQuery_ParameterArray_ReturnsNotNull()
    {
        assertThat(testee.createQuery(TestEventTypeA.class, param("foo", "bar"))).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createQuery_ParameterArray_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        testee.createEvent(null, param("foo", "bar"));
    }
}
