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

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.jayware.e2.component.api.ComponentEvent.ComponentCreatedEvent;
import org.jayware.e2.component.api.ComponentFactory;
import org.jayware.e2.component.api.ComponentInstancer;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.ComponentEvent.ComponentParam;
import static org.jayware.e2.component.api.ComponentEvent.ComponentTypeParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class ComponentStoreTest
{
    private @Mocked Context testContext;
    private @Mocked EventManager testEventManager;
    private @Mocked ComponentFactory testComponentFactory;
    private @Mocked ComponentInstancer testComponentInstancer;
    private @Mocked TestComponentA testComponentA;

    private @Mocked Event testEvent;
    private @Mocked Query testQuery;

    private ComponentStore testee;

    @BeforeEach
    public void setUp()
    {
        new Expectations()
        {{
            testContext.getService(EventManager.class); result = testEventManager; minTimes = 0;
            testContext.getService(ComponentFactory.class); result = testComponentFactory; minTimes = 0;
            testQuery.isQuery(); result = true; minTimes = 0;
        }};

        testee = new ComponentStore(testContext);
    }

    @Test
    public void test_handleCreateComponentEvent_Processes_only_Queries_and_not_Events()
    {
        testee.handleCreateComponentEvent(testEvent, TestComponentA.class);
    }

    @Test
    public void test_handleCreateComponentEvent_Inserts_an_instance_of_the_specified_Component_into_the_Query_ResultSet()
    {
        new Expectations()
        {{
            testComponentFactory.createComponent(TestComponentA.class); result = testComponentInstancer;
            testComponentInstancer.newInstance(testContext); result = testComponentA;
        }};

        testee.handleCreateComponentEvent(testQuery, TestComponentA.class);

        new Verifications()
        {{
            testQuery.result(ComponentParam, testComponentA); times = 1;
        }};
    }

    @Test
    public void test_handleCreateComponentEvent_Fires_a_ComponentCreatedEvent_with_expected_parameters()
    {
        new Expectations() {{
            testComponentA.type(); result = TestComponentA.class;
        }};

        testee.handleCreateComponentEvent(testQuery, TestComponentA.class);

        new Verifications()
        {{
            final Parameters.Parameter[] parameters;
            testEventManager.post(
                ComponentCreatedEvent.class,
                parameters = withCapture()
            );

            assertThat(parameters).contains(
                param(ContextParam, testContext),
                param(ComponentTypeParam, TestComponentA.class),
                param(ComponentParam, testComponentA)
            );
        }};
    }

}