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
package org.jayware.e2.entity.impl;


import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityEvent.DeleteAllEntitiesEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntityEvent;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityNotFoundException;
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
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefListParam;
import static org.jayware.e2.entity.api.EntityPath.path;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Query.State.Success;


public class EntityManagerImplTest
{
    private static final String TEST_ID = "051946d4-dee0-476e-8cb0-6e9f39776a10";
    private static final UUID TEST_UUID = UUID.fromString(TEST_ID);

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
    public void testCreateEntityAbsolute()
    {
        EntityRef entityRef = testee.createEntity(context, path("/a/b"));

        assertThat(entityRef.isValid()).isTrue();
        assertThat(entityRef.getPath().asString()).isEqualTo("/a/b/");
    }

    @Test
    public void testCreateEntityRelative()
    {
        final EntityRef ref = testee.createEntity(context, path("/a"));

        EntityRef entityRef = testee.createEntity(ref, path("b"));

        assertThat(entityRef.isValid()).isTrue();
        assertThat(entityRef.getPath().asString()).isEqualTo("/a/b/");
    }

    @Test
    public void testFindEntity()
    {
        testee.createEntity(context, path("/a/b"));

        assertThat(testee.findEntity(context, path("/"))).isNotNull();
        assertThat(testee.findEntity(context, path("/a"))).isNotNull();
        assertThat(testee.findEntity(context, path("/a/b"))).isNotNull();
        assertThat(testee.findEntity(context, path("/a/b/c"))).isNull();
        assertThat(testee.findEntity(context, path(""))).isNull();
    }

    @Test
    public void testDeleteEntity()
    {
        EntityRef ref = testee.createEntity(context, path("/a/b"));

        assertThat(ref).isNotNull();
        testee.deleteEntity(ref);

        assertThat(testee.findEntity(context, path("/a/b"))).isNull();
        assertThat(ref.isInvalid()).isTrue();
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

            testEventManager.send(DeleteEntityEvent.class, parameters = withCapture());

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
            testEventManager.query(DeleteAllEntitiesEvent.class, withCapture(capturedQueryParameters)); result = testResultSet;
            testResultSet.await(Success, anyLong, (TimeUnit) any); result = true;
            testResultSet.get(EntityRefListParam); result = expectedListOfDeletedEntities;
        }};

        assertThat(testee.deleteEntities(testContext))
            .withFailMessage("The result of deleteEntities does not contain the expected list of deleted entities!")
            .containsOnlyElementsOf(expectedListOfDeletedEntities);

        assertThat(capturedQueryParameters.get(0)[0])
            .withFailMessage("The DeleteAllEntitiesEvent was not fired with the expected ContextParam!")
            .isEqualTo(param(ContextParam, testContext));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteRootEntity()
    {
        EntityRef ref = testee.findEntity(context, path("/"));
        assertThat(ref).isNotNull();

        try
        {
            testee.deleteEntity(ref);
        }
        finally
        {
            // Assert that when an exception is thrown by the
            // deleteEntity operation the entity does still exist.
            assertThat(testee.findEntity(context, path("/"))).isNotNull();
        }
    }

    @Test
    public void testGetEntity()
    {
        testee.createEntity(context, path("/a/b"));

        assertThat(testee.getEntity(context, path("/"))).isNotNull();
        assertThat(testee.getEntity(context, path("/a"))).isNotNull();
        assertThat(testee.getEntity(context, path("/a/b"))).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetEntity_WithEmptyPath()
    {
        testee.getEntity(context, path(""));
    }

    @Test(expectedExceptions = EntityNotFoundException.class)
    public void testGetEntity_WithAbsentEntity()
    {
        testee.getEntity(context, path("/a/b/c"));
    }

    @Test
    public void testExistsEntity()
    {
        testee.createEntity(context, path("/a/b"));

        assertThat(testee.existsEntity(context, path("/"))).isTrue();
        assertThat(testee.existsEntity(context, path("/a"))).isTrue();
        assertThat(testee.existsEntity(context, path("/a/b"))).isTrue();
        assertThat(testee.existsEntity(context, path("/a/b/c"))).isFalse();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_resolveEntity_With_String_Throws_IllegalArgrumentException_if_passed_Context_is_null()
    {
        testee.resolveEntity(null, TEST_ID);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_resolveEntity_With_String_Throws_IllegalStateException_if_passed_Context_is_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.resolveEntity(testContext, TEST_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_resolveEntity_With_String_Throws_IllegalArgrumentException_if_passed_String_is_null()
    {
        testee.resolveEntity(testContext, (String) null);
    }

    @Test
    public void test_resolveEntity_With_String_Returns_not_null()
    {
        assertThat(testee.resolveEntity(context, TEST_ID)).isNotNull();
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
        testee.resolveEntity(testContext, (UUID) null);
    }

    @Test
    public void test_resolveEntity_With_UUID_Returns_not_null()
    {
        assertThat(testee.resolveEntity(context, TEST_UUID)).isNotNull();
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
