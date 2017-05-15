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
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.impl.EventManagerImpl.EVENT_BUS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class EventManagerTest
{
    private EventManager testee;

    private Context context;
    private EventBus eventbus;

    @BeforeEach
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

    @Test
    public void test_createEvent_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createEvent(null);
            }
        });
    }

    @Test
    public void test_createEvent_Parameters_ReturnsNotNull()
    {
        assertThat(testee.createEvent(TestEventTypeA.class, new Parameters())).isNotNull();
    }

    @Test
    public void test_createEvent_Parameters_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createEvent(null, new Parameters());
            }
        });
    }

    @Test
    public void test_createEvent_ParameterArry_ReturnsNotNull()
    {
        assertThat(testee.createEvent(TestEventTypeA.class, param("foo", "bar"))).isNotNull();
    }

    @Test
    public void test_createEvent_ParameterArry_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createEvent(null, param("foo", "bar"));
            }
        });
    }

    @Test
    public void test_createQuery_Parameters_ReturnsNotNull()
    {
        assertThat(testee.createQuery(TestEventTypeA.class, new Parameters())).isNotNull();
    }

    @Test
    public void test_createQuery_Parameters_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createEvent(null, new Parameters());
            }
        });
    }

    @Test
    public void test_createQuery_ParameterArray_ReturnsNotNull()
    {
        assertThat(testee.createQuery(TestEventTypeA.class, param("foo", "bar"))).isNotNull();
    }

    @Test
    public void test_createQuery_ParameterArray_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createEvent(null, param("foo", "bar"));
            }
        });
    }
}
