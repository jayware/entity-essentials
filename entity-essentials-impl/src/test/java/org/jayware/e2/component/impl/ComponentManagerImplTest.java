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
import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.ComponentEvent.AddComponentEvent;
import org.jayware.e2.component.api.ComponentEvent.RemoveComponentEvent;
import org.jayware.e2.component.api.ComponentManagerException;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.entity.api.InvalidEntityRefException;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.event.api.ResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.ComponentEvent.ComponentParam;
import static org.jayware.e2.component.api.ComponentEvent.ComponentTypeParam;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityIdParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Query.State.Success;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ComponentManagerImplTest
{
    private final UUID testRefId = fromString("e2b94185-38e2-4a0f-abfc-f8ef4fb4e92b");

    private @Mocked Context testContext, anotherContext;
    private @Mocked EventManager testEventManager;
    private @Mocked ResultSet testResultSet;

    private @Mocked EntityRef testRef;
    private @Mocked TestComponentA testComponentA, testComponentB;
    private @Mocked AbstractComponent testAbstractComponent;

    private ComponentManagerImpl testee;

    @BeforeEach
    public void setUp()
    {
        testee = new ComponentManagerImpl();

        new Expectations() {{
            testContext.getService(EventManager.class); result = testEventManager; minTimes = 0;
            testRef.getId(); result = testRefId; minTimes = 0;
        }};
    }

    @Test
    public void test_createComponent_with_Context_and_Class_Throws_IllegalArgumentException_if_the_passed_Context_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createComponent(null, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_createComponent_with_Context_and_Class_Throws_IllegalStateException_if_the_passed_Context_has_been_disposed()
    {
        new Expectations() {{
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createComponent(testContext, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_createComponent_with_Context_and_Class_Throws_IllegalArgumentException_if_the_passed_Class_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createComponent(testContext, null);
            }
        });
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

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(null, testAbstractComponent);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalArgumentException_if_the_passed_Component_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(testRef, (TestComponentA) null);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_InvalidEntityRefException_if_the_EntityRef_is_invalid()
    {
        new Expectations()
        {{
            testRef.isInvalid(); result = true;
            testRef.getContext(); result = testContext;
        }};

        assertThrows(InvalidEntityRefException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(testRef, testAbstractComponent);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalStateException_if_the_Context_to_which_the_EntityRef_and_the_Component_belong_to_has_been_disposed()
    {
        new Expectations()
        {{
            testRef.getContext(); result = testContext;
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(testRef, testAbstractComponent);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Throws_IllegalContextException_if_the__EntityRef_and_the_Component_do_not_belong_to_the_same_context()
    {
        new Expectations()
        {{
            testRef.getContext(); result = testContext;
            testRef.belongsTo(testAbstractComponent); result = false;
            testContext.isDisposed(); result = false;
        }};

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(testRef, testAbstractComponent);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Component_Fires_AddComponentEvent_with_expected_parameters()
    {
        new Expectations()
        {{
            testRef.getContext(); result = testContext;
            testRef.belongsTo(testAbstractComponent); result = true;
            testAbstractComponent.type(); result = TestComponentA.class;
            testContext.isDisposed(); result = false;
            testResultSet.await(Success, anyLong, (TimeUnit) any); result = true;
            testResultSet.get(ComponentParam); result = testComponentB;
        }};

        assertThat(testee.addComponent(testRef, testAbstractComponent)).isEqualTo(testComponentB);

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
                .withFailMessage("ComponentManager fired a query to add a component without the expected EntityIdParam!")
                .contains(param(EntityIdParam, testRefId));

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to add a component without the expected ComponentTypeParam!")
                .contains(param(ComponentTypeParam, TestComponentA.class));

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to add a component without the expected ComponentTypeParam!")
                .contains(param(ComponentParam, testAbstractComponent));
        }};
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Class_Throws_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(null, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Class_Throws_IllegalArgumentException_if_the_passed_Component_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(testRef, (Class) null);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Class_Throws_IllegalStateException_if_the_Context_to_which_the_EntityRef_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testRef.isInvalid(); result = true;
            testRef.getContext(); result = testContext;
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(testRef, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Class_Throws_InvalidEntityRefException_if_the_EntityRef_is_invalid()
    {
        new Expectations()
        {{
            testRef.isInvalid(); result = true;
        }};

        assertThrows(InvalidEntityRefException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(testRef, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Class_Throws_ComponentManagerException_when_the_Query_times_out()
    {
        new Expectations()
        {{
            testRef.isInvalid(); result = false;
            testRef.getContext(); result = testContext;
            testContext.isDisposed(); result = false;
            testResultSet.await(Success, anyLong, (TimeUnit) any); result = false;
        }};

        assertThrows(ComponentManagerException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.addComponent(testRef, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_addComponent_With_EntityRef_and_Class_Fires_AddComponentEvent_with_expected_parameters()
    {
        new Expectations()
        {{
            testRef.getContext(); result = testContext;
            testContext.isDisposed(); result = false;
            testResultSet.await(Success, anyLong, (TimeUnit) any); result = true;
            testResultSet.get(ComponentParam); result = testComponentA;
        }};

        assertThat(testee.addComponent(testRef, TestComponentA.class)).isEqualTo(testComponentA);

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
                .withFailMessage("ComponentManager fired a query to remove a component without the expected EntityIdParam!")
                .contains(param(EntityIdParam, testRefId));

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to add a component without the expected ComponentTypeParam!")
                .contains(param(ComponentTypeParam, TestComponentA.class));

        }};
    }

    @Test
    public void test_removeComponent_With_EntityRef_and_Class_Throws_IllegalArgumentException_if_passed_EntityRef_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.removeComponent(null, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_removeComponent_With_EntityRef_and_Class_Throws_IllegalArgumentException_if_passed_Class_is_null()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.removeComponent(testRef, (Class) null);
            }
        });
    }

    @Test
    public void test_removeComponent_With_EntityRef_and_Class_Throws_IllegalStateException_if_the_Context_to_which_the_passed_EntityRef_belongs_to_has_been_disposed()
    {
        new Expectations()
        {{
            testRef.isInvalid(); result = true;
            testRef.getContext(); result = testContext;
            testContext.isDisposed(); result = true;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.removeComponent(testRef, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_removeComponent_With_EntityRef_and_Class_Throws_InvalidEntityRefException_if_the_passed_EntityRef_is_invalid()
    {
        new Expectations()
        {{
            testRef.isInvalid(); result = true;
            testRef.getContext(); result = testContext;
            testContext.isDisposed(); result = false;
        }};

        assertThrows(InvalidEntityRefException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.removeComponent(testRef, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_removeComponent_With_EntityRef_and_Class_Throws_ComponentManagerException_when_the_Query_times_out()
    {
        new Expectations()
        {{
            testRef.isInvalid(); result = false;
            testRef.getContext(); result = testContext;
            testResultSet.await(Success, anyLong, (TimeUnit) any); result = false;
        }};

        assertThrows(ComponentManagerException.class, new Executable()
        {
            @Override
            public void execute()
            throws Throwable
            {
                testee.removeComponent(testRef, TestComponentA.class);
            }
        });
    }

    @Test
    public void test_removeComponent_With_EntityRef_and_Class_Fires_A_RemoveComponentEvent_with_expected_parameters_and_returns_the_deleted_Component()
    {
        new Expectations()
        {{
            testRef.isInvalid(); result = false;
            testRef.getContext(); result = testContext;
            testRef.getId(); result = testRefId;
            testResultSet.await(Success, anyLong, (TimeUnit) any); result = true;
            testResultSet.find(ComponentParam); result = testComponentA;
        }};

        assertThat(testee.removeComponent(testRef, TestComponentA.class)).isEqualTo(testComponentA);

        new Verifications()
        {{
            final Parameter[] parameters;

            testEventManager.query(RemoveComponentEvent.class, parameters = withCapture());

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to remove a component without the expected ContextParam!")
                .contains(param(ContextParam, testContext));

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to remove a component without the expected EntityRefParam!")
                .contains(param(EntityRefParam, testRef));

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to remove a component without the expected EntityIdParam!")
                .contains(param(EntityIdParam, testRefId));

            assertThat(parameters)
                .withFailMessage("ComponentManager fired a query to remove a component without the expected ComponentTypeParam!")
                .contains(param(ComponentTypeParam, TestComponentA.class));
        }};
    }
}