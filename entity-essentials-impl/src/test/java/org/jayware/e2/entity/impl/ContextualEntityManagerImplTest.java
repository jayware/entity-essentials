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
import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.util.Filter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;


public class ContextualEntityManagerImplTest
{
    private @Mocked Context testContext, otherContext;
    private @Mocked EntityManager testEntityManager;
    private @Mocked EntityRef testRefA, testRefB, testRefC;
    private @Mocked Aspect testAspect;
    private @Mocked Filter<EntityRef> testFilterA, testFilterB;
    private @Mocked Contextual testContextual;

    private ContextualEntityManagerImpl testee;

    @BeforeMethod
    public void setUp()
    {
        testee = new ContextualEntityManagerImpl(testContext, testEntityManager);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_createEntity_Throws_IllegalStateException_if_the_context_has_been_disposed()
    {
        new Expectations() {{
            testContext.isDisposed(); result = true;
        }};

        testee.createEntity();
    }

    @Test
    public void test_createEntity_Calls_its_delegate_and_returns_the_constructed_EntityRef()
    {
        new Expectations()
        {{
            testEntityManager.createEntity(testContext); result = testRefA;
        }};

        assertThat(testee.createEntity()).isEqualTo(testRefA);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_DeleteEntity_Throws_IllegalArgumentException_if_null_is_passed()
    {
        testee.deleteEntity(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_DeleteEntity_Throws_IllegalStateException_if_its_Context_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.deleteEntity(testRefA);
    }

    @Test(expectedExceptions = IllegalContextException.class)
    public void test_DeleteEntity_Throws_IllegalContextException_if_the_passed_EntityRef_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = false;
        }};

        testee.deleteEntity(testRefA);
    }

    @Test
    public void test_DeleteEntity_Calls_its_delegate_to_delete_the_Entity_denoted_by_the_passed_EntityRef()
    {
        new Expectations()
        {{
            testRefA.belongsTo(testContext); result = true;
        }};

        testee.deleteEntity(testRefA);

        new Verifications()
        {{
            testEntityManager.deleteEntity(testRefA);
        }};
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_DeleteEntities_Throws_IllegalStateException_if_its_Context_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.deleteEntities();
    }

    @Test
    public void test_DeleteEntity_Calls_its_delegate_to_delete_Entities_and_returns_a_list_containing_all_deleted_Entities()
    {
        final List<EntityRef> resultList = new ArrayList<EntityRef>();
        resultList.add(testRefA);
        resultList.add(testRefB);

        new Expectations()
        {{
            testEntityManager.deleteEntities(testContext); result = resultList;
        }};

        assertThat(testee.deleteEntities()).containsOnlyElementsOf(resultList);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_findEntities_Throws_IllegalStateException_if_its_Context_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.findEntities();
    }

    @Test
    public void test_findEntities_Returns_the_expected_List_of_EntityRefs()
    {
        new Expectations()
        {{
            testEntityManager.findEntities(testContext); result = asList(testRefB, testRefC);
        }};

        assertThat(testee.findEntities()).containsExactly(testRefB, testRefC);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_findEntities_With_Aspect_Throws_IllegalArgumentException_if_null_is_passed()
    {
        testee.findEntities((Aspect) null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_findEntities_With_Aspect_Throws_IllegalStateException_if_its_Context_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.findEntities(testAspect);
    }

    @Test
    public void test_findEntities_With_Aspect_Returns_the_expected_List_of_EntityRefs()
    {
        new Expectations()
        {{
            testEntityManager.findEntities(testContext, testAspect); result = asList(testRefB, testRefC);
        }};

        assertThat(testee.findEntities(testAspect)).containsExactly(testRefB, testRefC);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_findEntities_With_Filters_Throws_IllegalArgumentException_if_null_is_passed()
    {
        testee.findEntities((Filter<EntityRef>[]) null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_findEntities_With_Filters_Throws_IllegalStateException_if_its_Context_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.findEntities(testFilterA, testFilterB);
    }

    @Test
    public void test_findEntities_With_Filters_Returns_the_expected_List_of_EntityRefs()
    {
        new Expectations()
        {{
            testEntityManager.findEntities(testContext, testFilterA, testFilterB); result = asList(testRefB, testRefC);
        }};

        assertThat(testee.findEntities(testFilterA, testFilterB)).containsExactly(testRefB, testRefC);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_findEntities_With_Aspect_And_Filters_Throws_IllegalArgumentException_if_null_is_passed_as_Filters()
    {
        testee.findEntities(testAspect, (Filter<EntityRef>[]) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_findEntities_With_Aspect_And_Filters_Throws_IllegalArgumentException_if_null_is_passed_as_Aspect()
    {
        testee.findEntities((Aspect) null, testFilterA, testFilterB);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_findEntities_With_Aspect_And_Filters_Throws_IllegalStateException_if_its_Context_has_been_disposed()
    {
        new Expectations()
        {{
            testContext.isDisposed(); result = true;
        }};

        testee.findEntities(testAspect, testFilterA, testFilterB);
    }

    @Test
    public void test_findEntities_With_Aspect_And_Filters_Returns_the_expected_List_of_EntityRefs()
    {
        new Expectations()
        {{
            testEntityManager.findEntities(testContext, testAspect, testFilterA, testFilterB); result = asList(testRefB, testRefC);
        }};

        assertThat(testee.findEntities(testAspect, testFilterA, testFilterB)).containsExactly(testRefB, testRefC);
    }

    @Test
    public void test_getContext_Returns_the_correct_Context()
    {
        assertThat(testee.getContext()).isEqualTo(testContext);
    }

    @Test
    public void test_belongsTo_With_Context_Returns_true_if_the_passed_Context_is_the_same()
    {
        assertThat(testee.belongsTo(testContext)).isTrue();
    }

    @Test
    public void test_belongsTo_With_Context_Returns_false_if_another_Context_is_passed()
    {
        assertThat(testee.belongsTo(otherContext)).isFalse();
    }

    @Test
    public void test_belongsTo_With_Contextual_Returns_true_if_the_passed_Contextual_belongs_to_the_same_Context()
    {
        new Expectations()
        {{
            testContextual.getContext(); result = testContext;
        }};

        assertThat(testee.belongsTo(testContextual)).isTrue();
    }

    @Test
    public void test_belongsTo_With_Contextual_Returns_false_if_the_passed_Contextual_belongs_to_another_Context()
    {
        new Expectations()
        {{
            testContextual.getContext(); result = otherContext;
        }};

        assertThat(testee.belongsTo(testContextual)).isFalse();
    }
}