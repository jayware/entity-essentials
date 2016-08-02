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
import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.ComponentEvent.AddComponentEvent;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.entity.api.InvalidEntityRefException;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.event.api.ResultSet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.ComponentEvent.ComponentParam;
import static org.jayware.e2.component.api.ComponentEvent.ComponentTypeParam;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityRefParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Query.State.Success;


public class ComponentManagerImplTest
{
    private @Mocked Context testContext, anotherContext;
    private @Mocked EventManager testEventManager;
    private @Mocked ResultSet testResultSet;

    private @Mocked EntityRef testRef;
    private @Mocked TestComponentA testComponentA;
    private @Mocked AbstractComponent testAbstractComponent;

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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        testee.addComponent(null, testAbstractComponent);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalArgumentException_if_the_passed_Component_is_null()
    {
        testee.addComponent(testRef, (TestComponentA) null);
    }

    @Test(expectedExceptions = InvalidEntityRefException.class)
    public void test_addComponent_With_EntityRef_and_Component_Throws_InvalidEntityRefException_if_the_EntityRef_is_invalid()
    {
        new Expectations()
        {{
            testRef.isValid(); result = false; minTimes = 0;
            testRef.isInvalid(); result = true; minTimes = 0;
            testRef.getContext(); result = testContext; minTimes = 0;
            testRef.belongsTo(testContext); result = true; minTimes = 0;
            testRef.belongsTo(testAbstractComponent); result = true; minTimes = 0;
            testAbstractComponent.getContext(); result = testContext; minTimes = 0;
            testAbstractComponent.belongsTo(testContext); result = true; minTimes = 0;
            testAbstractComponent.belongsTo(testRef); result = true; minTimes = 0;
        }};

        testee.addComponent(testRef, testAbstractComponent);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalStateException_if_the_Context_to_which_the_EntityRef_and_the_Component_belong_to_has_been_disposed()
    {
        new Expectations()
        {{
            testRef.getContext(); result = testContext; minTimes = 0;
            testRef.belongsTo(testContext); result = true; minTimes = 0;
            testRef.belongsTo(testAbstractComponent); result = true; minTimes = 0;
            testAbstractComponent.getContext(); result = testContext; minTimes = 0;
            testAbstractComponent.belongsTo(testContext); result = true; minTimes = 0;
            testAbstractComponent.belongsTo(testRef); result = true; minTimes = 0;
            testContext.isDisposed(); result = true;
        }};

        testee.addComponent(testRef, testAbstractComponent);
    }

    @Test(expectedExceptions = IllegalContextException.class)
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalContextException_if_the__EntityRef_and_the_Component_do_not_belong_to_the_same_context()
    {
        new Expectations()
        {{
            testRef.getContext(); result = testContext; minTimes = 0;
            testRef.belongsTo(testContext); result = true; minTimes = 0;
            testRef.belongsTo(testAbstractComponent); result = false; minTimes = 0;
            testAbstractComponent.getContext(); result = anotherContext; minTimes = 0;
            testAbstractComponent.belongsTo(testContext); result = false; minTimes = 0;
            testAbstractComponent.belongsTo(anotherContext); result = true; minTimes = 0;
            testAbstractComponent.belongsTo(testRef); result = false; minTimes = 0;
            testContext.isDisposed(); result = false; minTimes = 0;
            anotherContext.isDisposed(); result = false; minTimes = 0;
        }};

        testee.addComponent(testRef, testAbstractComponent);
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Fires_AddComponentEvent_with_expected_parameters()
    {
        new Expectations()
        {{
            testRef.getContext(); result = testContext; minTimes = 0;
            testRef.belongsTo(testContext); result = true; minTimes = 0;
            testRef.belongsTo(testAbstractComponent); result = true; minTimes = 0;
            testAbstractComponent.getContext(); result = testContext; minTimes = 0;
            testAbstractComponent.belongsTo(testContext); result = true; minTimes = 0;
            testAbstractComponent.belongsTo(testRef); result = true; minTimes = 0;
            testAbstractComponent.type(); result = TestComponentA.class;
            testContext.isDisposed(); result = false; minTimes = 0;
        }};

        testee.addComponent(testRef, testAbstractComponent);

        new Verifications()
        {{
            final Parameter[] parameters;
            testEventManager.send(
                AddComponentEvent.class,
                parameters = withCapture()
            );

            assertThat(parameters).contains(
                param(ContextParam, testContext),
                param(EntityRefParam, testRef),
                param(ComponentTypeParam, TestComponentA.class),
                param(ComponentParam, testAbstractComponent)
            );
        }};
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_addComponent_With_EntityRef_and_Class_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        testee.addComponent(null, TestComponentA.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_addComponent_With_EntityRef_and_Class_Throws_IllegalArgumentException_if_the_passed_Component_is_null()
    {
        testee.addComponent(testRef, (Class) null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_addComponent_With_EntityRef_and_Class_Throws_IllegalStateException_if_the_Context_to_which_the_EntityRef_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testRef.isValid(); result = false; minTimes = 0;
            testRef.isInvalid(); result = true; minTimes = 0;
            testRef.getContext(); result = testContext; minTimes = 0;
            testContext.isDisposed(); result = true; minTimes = 0;
        }};

        testee.addComponent(testRef, TestComponentA.class);
    }


    @Test(expectedExceptions = InvalidEntityRefException.class)
    public void test_addComponent_With_EntityRef_and_Class_Throws_InvalidEntityRefException_if_the_EntityRef_is_invalid()
    {
        new Expectations()
        {{
            testRef.isValid(); result = false; minTimes = 0;
            testRef.isInvalid(); result = true; minTimes = 0;
        }};

        testee.addComponent(testRef, TestComponentA.class);
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Class_Fires_AddComponentEvent_with_expected_parameters()
    {
        new Expectations()
        {{
            testRef.getContext(); result = testContext; minTimes = 0;
            testContext.isDisposed(); result = false; minTimes = 0;
            testResultSet.await(Success, anyLong, (TimeUnit) any); result = true;
            testResultSet.get(ComponentParam); result = testComponentA;
        }};

        assertThat(testee.addComponent(testRef, TestComponentA.class)).isNotNull();

        new Verifications()
        {{
            final Parameter[] parameters;

            testEventManager.query(AddComponentEvent.class, parameters = withCapture());

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to add a component without the expected ContextParam!")
                .contains(param(ContextParam, testContext));

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to add a component without the expected EntityRefParam!")
                .contains(param(EntityRefParam, testRef));

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to add a component without the expected ComponentTypeParam!")
                .contains(param(ComponentTypeParam, TestComponentA.class));

        }};
    }
}