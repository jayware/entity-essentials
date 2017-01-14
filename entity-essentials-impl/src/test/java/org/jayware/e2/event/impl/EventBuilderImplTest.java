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


import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventBuilder;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.impl.EventBuilderImpl.createEventBuilder;


public class EventBuilderImplTest
{
    private EventBuilder testee;

    @BeforeMethod
    public void setUp()
    {
        testee = createEventBuilder(TestEventTypeA.class);
    }

    @Test
    public void test()
    {
        Event event;

        testee.set("foo").to("bar");
        testee.set(param("muh", "kuh"));
        testee.setAll(new Parameters(new Parameter[]{param("number1", 42), param("number2", 73)}));

        event = testee.build();

        assertThat(event.getType()).isEqualTo(TestEventTypeA.class);
        assertThat((String) event.getParameter("foo")).isEqualTo("bar");
        assertThat((String) event.getParameter("muh")).isEqualTo("kuh");
        assertThat((Integer) event.getParameter("number1")).isEqualTo(42);
        assertThat((Integer) event.getParameter("number2")).isEqualTo(73);

        event = testee.reset().build();

        assertThat(event.getType()).isEqualTo(TestEventTypeA.class);
        assertThat(event.hasParameter("foo")).isFalse();
        assertThat(event.hasParameter("muh")).isFalse();
        assertThat(event.hasParameter("number1")).isFalse();
        assertThat(event.hasParameter("number2")).isFalse();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createEventBuilder_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        createEventBuilder(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_set_ParameterName_ThrowsIllegalArgumentExceptionIfNameIsNull()
    {
        testee.set((String) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_set_ParameterName_ThrowsIllegalArgumentExceptionIfNameIsEmpty()
    {
        testee.set("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_set_Parameter_ThrowsIllegalArgumentExceptionIfParameterIsNull()
    {
        testee.set((Parameter) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_setAll_ThrowsIllegalArgumentExceptionIfParametersIsNull()
    {
        testee.setAll(null);
    }
}
