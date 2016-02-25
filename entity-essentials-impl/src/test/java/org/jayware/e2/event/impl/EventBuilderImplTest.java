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
        assertThat((int) event.getParameter("number1")).isEqualTo(42);
        assertThat((int) event.getParameter("number2")).isEqualTo(73);

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
