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
package org.jayware.e2.entity.impl;


import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntitiesEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntityEvent;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.ResultSet;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefListParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class EntityManagerImplTest
{
    private static final UUID TEST_UUID = fromString("051946d4-dee0-476e-8cb0-6e9f39776a10");

    private Context context;
    private EntityManager testee;

    private @Mocked Context testContext;
    private @Mocked EventManager testEventManager;
    private @Mocked EntityRef testRefA, testRefB, testRefC;
    private @Mocked ResultSet testResultSet;

    @BeforeMethod
    public void setup()
    {
        context = ContextProvider.getInstance().createContext();
        testee = new EntityManagerImpl();

        new Expectations()
        {{
            testContext.getService(EventManager.class); result = testEventManager; minTimes = 0;
        }};
    }

    @AfterMethod
    public void tearDown()
    {
        context.dispose();
    }

    @Test
    public void testCreateEntity()
    {
        EntityRef ref = testee.createEntity(context);
        assertThat(ref.isValid()).isTrue();
    }

    @Test
    public void test_deleteEntity_Fires_DeleteEntityEvent_with_expected_parameters()
    throws Exception
    {
        final UUID id = UUID.randomUUID();

        new Expectations()
        {{
            testRefA.getId(); result = id;
        }};

        testee.deleteEntity(testRefA);

        new Verifications()
        {{
            final Parameter[] parameters;

            testEventManager.query(DeleteEntityEvent.class, parameters = withCapture());

            assertThat(parameters).contains(param(ContextParam, testContext));
            assertThat(parameters).contains(param(EntityRefParam, testRefA));
            assertThat(parameters).contains(param(EntityIdParam, id));
        }};
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_deleteEntities_Throws_IllegalArgumentException_if_passed_context_is_null()
    throws Exception
    {
        testee.deleteEntities(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_deleteEntities_Throws_IllegalStateException_if_passed_context_has_been_disposed()
    throws Exception
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.deleteEntities(testContext);
    }

    @Test
    public void test_deleteEntities_Fires_DeleteAllEntitiesEvent_as_Query_with_expected_parameters()
    throws Exception
    {
        final List<EntityRef> expectedListOfDeletedEntities = new ArrayList<EntityRef>();
        final List<Parameter[]> capturedQueryParameters = new ArrayList<Parameter[]>();

        expectedListOfDeletedEntities.addAll(Arrays.<EntityRef>asList(testRefA, testRefB, testRefC));

        new Expectations()
        {{
            testEventManager.query(DeleteEntitiesEvent.class, withCapture(capturedQueryParameters)); result = testResultSet;
            testResultSet.get(EntityRefListParam); result = expectedListOfDeletedEntities;
        }};

        assertThat(testee.deleteEntities(testContext))
            .withFailMessage("The result of deleteEntities does not contain the expected list of deleted entities!")
            .containsOnlyElementsOf(expectedListOfDeletedEntities);

        assertThat(capturedQueryParameters.get(0)[0])
            .withFailMessage("The DeleteAllEntitiesEvent was not fired with the expected ContextParam!")
            .isEqualTo(param(ContextParam, testContext));
    }

    @Test
    public void test_findEntities_With_Context_()
    {
        final EntityRef refA = testee.createEntity(context);
        final EntityRef refB = testee.createEntity(context);
        final EntityRef refC = testee.createEntity(context);

        assertThat(testee.findEntities(context)).containsExactlyInAnyOrder(refA, refB, refC);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_resolveEntity_With_UUID_Throws_IllegalArgrumentException_if_passed_Context_is_null()
    {
        testee.resolveEntity(null, TEST_UUID);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_resolveEntity_With_UUID_Throws_IllegalStateException_if_passed_Context_is_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.resolveEntity(testContext, TEST_UUID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_resolveEntity_With_UUID_Throws_IllegalArgrumentException_if_passed_UUID_is_null()
    {
        testee.resolveEntity(testContext, null);
    }

    @Test
    public void test_resolveEntity_With_UUID_Returns_the_expected_EntityRef()
    {
        final EntityRef expectedRef = testee.createEntity(context, TEST_UUID);
        assertThat(testee.resolveEntity(context, TEST_UUID)).isEqualTo(expectedRef);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_asContextual_Throws_IllegalArgumentException_if_null_is_passed()
    throws Exception
    {
        testee.asContextual(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_asContextual_Throws_IllegalStateException_if_the_passed_Context_is_disposed()
    throws Exception
    {
        new Expectations() {{
            testContext.isDisposed(); result = true;
        }};

        testee.asContextual(testContext);
    }
}
