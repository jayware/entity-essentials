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
