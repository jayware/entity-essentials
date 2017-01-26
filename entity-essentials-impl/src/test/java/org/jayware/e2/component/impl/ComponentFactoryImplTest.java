/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
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


import mockit.Mocked;
import org.jayware.e2.component.api.ComponentInstancer;
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.component.impl.TestComponents.TestComponentC;
import org.jayware.e2.context.api.Context;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class ComponentFactoryImplTest
{
    private @Mocked Context testContext;

    ComponentFactoryImpl testee;

    @BeforeMethod
    public void setup()
    {
        testee = new ComponentFactoryImpl();
    }

    @Test
    public void testPrepare()
    {
        testee.prepareComponent(TestComponentA.class);
    }

    @Test
    public void test_create()
    {
        testee.prepareComponent(TestComponentC.class);
        final TestComponentC component = testee.createComponent(TestComponentC.class).newInstance(testContext);

        assertThat(component).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_createComponent_From_Class_Fails_if_null_is_passed()
    {
        testee.createComponent((Class) null);
    }

    @Test(expectedExceptions = MalformedComponentException.class)
    public void test_prepareComponent_Should_fail_when_parameter_types_do_not_match()
    {
        testee.prepareComponent(TestComponents.TestComponentWithParameterTypeMismatch.class);
    }

    @Test
    public void test_concurrent_component_preparation()
    throws Exception
    {
        final int executorCount = 10;
        final int jobCount = 100;
        final ComponentInstancer<TestComponentA> instancer;
        final TestComponentA testComponentA;
        final ExecutorService threadPool = Executors.newFixedThreadPool(executorCount);
        final Runnable prepareComponentRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                testee.prepareComponent(TestComponentA.class);
            }
        };

        for (int i = 0; i < jobCount; ++i)
        {
            threadPool.execute(prepareComponentRunnable);
        }

        threadPool.shutdown();
        if (!threadPool.awaitTermination(16, TimeUnit.SECONDS))
        {
            fail("ThreadPool did not terminate within the maximum time to wait!");
        }

        assertThat(testee.isComponentPrepared(TestComponentA.class)).isTrue();

        instancer = testee.createComponent(TestComponentA.class);
        testComponentA = instancer.newInstance(testContext);

        assertThat(testComponentA).isNotNull();
    }
}
