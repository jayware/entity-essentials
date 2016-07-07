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
import mockit.Verifications;
import org.jayware.e2.component.api.ComponentEvent.ComponentCreatedEvent;
import org.jayware.e2.component.api.ComponentFactory;
import org.jayware.e2.component.api.ComponentInstancer;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Query;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

    @BeforeMethod
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
        testee.handleCreateComponentEvent(testQuery, TestComponentA.class);

        new Verifications()
        {{
            testEventManager.post(
                ComponentCreatedEvent.class,
                param(ContextParam, testContext),
                param(ComponentTypeParam, TestComponentA.class),
                param(ComponentParam, testComponentA)
            );
        }};
    }

}