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
package org.jayware.e2.component.impl;

import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.event.api.ResultSet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.ComponentEvent.ComponentParam;


public class ComponentManagerImplTest
{
    private @Mocked Context testContext;
    private @Mocked EventManager testEventManager;
    private @Mocked ResultSet testResultSet;

    private @Mocked TestComponentA testComponentA;

    private ComponentManagerImpl testee;

    @BeforeMethod
    public void setUp()
    {
        testee = new ComponentManagerImpl();

        new Expectations() {{
            testContext.getService(EventManager.class); result = testEventManager; minTimes = 0;
            testEventManager.query((Query) any); result = testResultSet; minTimes = 0;
            testEventManager.query((Class<? extends EventType.RootEvent>) any, (Parameter[]) any); result = testResultSet; minTimes = 0;
        }};
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createComponent_with_Context_and_Class_Throws_IllegalArgumentException_if_the_passed_Context_is_null()
    {
        testee.createComponent(null, TestComponentA.class);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_createComponent_with_Context_and_Class_Throws_IllegalStateException_if_the_passed_Context_has_been_disposed()
    {
        new Expectations() {{
            testContext.isDisposed(); result = true;
        }};

        testee.createComponent(testContext, TestComponentA.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createComponent_with_Context_and_Class_Throws_IllegalArgumentException_if_the_passed_Class_is_null()
    {
        new Expectations() {{
            testContext.isDisposed(); result = false; minTimes = 0;
        }};

        testee.createComponent(testContext, null);
    }

    @Test
    public void test_createComponent_Returns_the_instance_of_the_query_result()
    {
        new Expectations() {{
            testResultSet.get(ComponentParam); result = testComponentA;
            testResultSet.await((State) any, anyLong, (TimeUnit) any); result = true;
        }};

        assertThat(testee.createComponent(testContext, TestComponentA.class)).isEqualTo(testComponentA);
    }
}