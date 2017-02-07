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
package org.jayware.e2.assembly.impl;


import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.assembly.api.InvalidGroupException;
import org.jayware.e2.assembly.api.components.GroupComponent;
import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.entity.api.InvalidEntityRefException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GroupManagerTest
{
    private GroupManager testee;

    private Context context;

    private EntityRef testEntityA;
    private EntityRef testEntityB;

    private @Mocked Context testContext;
    private @Mocked EntityManager testEntityManager;
    private @Mocked ComponentManager testComponentManager;
    private @Mocked GroupComponent testGroupComponent;

    private @Mocked EntityRef testRefC;
    private @Mocked EntityRef testRefD;

    @BeforeMethod
    public void setUp()
    throws Exception
    {
        testee = new GroupManagerImpl();

        context = ContextProvider.getInstance().createContext();

        final EntityManager entityManager = context.getService(EntityManager.class);
        testEntityA = entityManager.createEntity(context);
        testEntityB = entityManager.createEntity(context);
    }

    @AfterMethod
    public void tearDown()
    {
        context.dispose();
    }

    @Test
    public void testCreateGroup()
    {
        assertThat(testee.createGroup(context)).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_CreateGroup_ThrowsIllegalArgumentExceptionWhenPassedContextIsNull()
    {
        testee.createGroup(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_CreateGroup_ThrowsIllegalStateExceptionWhenPassedContextIsDisposed()
    {
        final Context context = mock(Context.class);
        when(context.isDisposed()).thenReturn(true);

        testee.createGroup(context);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_CreateGroupWithName_ThrowsIllegalArgumentExceptionWhenPassedContextIsNull()
    {
        testee.createGroup(null, "fubar");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_CreateGroupWithName_ThrowsIllegalStateExceptionWhenPassedContextIsDisposed()
    {
        final Context context = mock(Context.class);
        when(context.isDisposed()).thenReturn(true);

        testee.createGroup(context, "fubar");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_CreateGroupWithName_ThrowsIllegalArgumentExceptionWhenPassedStringIsNull()
    {
        testee.createGroup(context, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_CreateGroupWithName_ThrowsIllegalArgumentExceptionWhenPassedNameIsEmpty()
    {
        testee.createGroup(context, "");
    }

    @Test
    public void test_deleteGroup()
    {
        final Group group = testee.createGroup(context);
        assertThat(group.isValid()).isTrue();
        testee.deleteGroup(group);
        assertThat(group.isValid()).isFalse();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_deleteGroup_ThrowsIllegalArgumentExceptionIfNUllIsPassed()
    {
        testee.deleteGroup(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_addEntityToGroup_ThrowsIllegalArgumentExceptionWhenPassedEntityRefIsNull()
    {
        final Group testGroup = mock(Group.class);
        testee.addEntityToGroup(null, testGroup);
    }

    @Test(expectedExceptions = InvalidEntityRefException.class)
    public void test_addEntityToGroup_ThrowsInvalidEntityRefExceptionWhenPassedEntityRefIsInvalid()
    {
        final Group testGroup = mock(Group.class);
        final EntityRef ref = mock(EntityRef.class);

        when(ref.isInvalid()).thenReturn(true);
        when(ref.isValid()).thenReturn(false);
        when(ref.getContext()).thenReturn(context);

        testee.addEntityToGroup(ref, testGroup);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_addEntityToGroup_ThrowsIllegalArgumentExceptionWhenPassedGroupIsNull()
    {
        testee.addEntityToGroup(testEntityA, null);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_addEntityToGroup_ThrowsInvalidGroupExceptionWhenPassedGroupIsInvalid()
    {
        final Group testGroup = mock(Group.class);

        when(testGroup.isInvalid()).thenReturn(true);
        when(testGroup.isValid()).thenReturn(false);

        testee.addEntityToGroup(testEntityA, testGroup);
    }

    @Test(expectedExceptions = IllegalContextException.class)
    public void test_addEntityToGroup_ThrowsIllegalContextExceptionIfEntityAndGroupDoesNotBelongToTheSameContext()
    {
        final Context testContext = mock(Context.class);
        final Group testGroup = mock(Group.class);

        when(testGroup.getContext()).thenReturn(testContext);
        when(testGroup.isInvalid()).thenReturn(false);
        when(testGroup.isValid()).thenReturn(true);

        testee.addEntityToGroup(testEntityA, testGroup);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_removeEntityFromGroup_ThrowsIllegalArgumentExceptionWhenPassedEntityRefIsNull()
    {
        final Group testGroup = mock(Group.class);
        testee.removeEntityFromGroup(null, testGroup);
    }

    @Test(expectedExceptions = InvalidEntityRefException.class)
    public void test_removeEntityFromGroup_ThrowsInvalidEntityRefExceptionWhenPassedEntityRefIsInvalid()
    {
        final Group testGroup = mock(Group.class);
        final EntityRef ref = mock(EntityRef.class);

        when(ref.isInvalid()).thenReturn(true);
        when(ref.isValid()).thenReturn(false);
        when(ref.getContext()).thenReturn(context);

        testee.removeEntityFromGroup(ref, testGroup);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_removeEntityFromGroup_ThrowsIllegalArgumentExceptionWhenPassedGroupIsNull()
    {
        testee.removeEntityFromGroup(testEntityA, null);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_removeEntityFromGroup_ThrowsInvalidGroupExceptionWhenPassedGroupIsInvalid()
    {
        final Group testGroup = mock(Group.class);

        when(testGroup.isInvalid()).thenReturn(true);
        when(testGroup.isValid()).thenReturn(false);

        testee.removeEntityFromGroup(testEntityA, testGroup);
    }

    @Test
    public void test_getEntitiesOfGroup()
    {
        final Group testGroup = testee.createGroup(context);
        testee.addEntityToGroup(testEntityA, testGroup);
        testee.addEntityToGroup(testEntityB, testGroup);

        assertThat(testee.getEntitiesOfGroup(testGroup)).contains(testEntityA, testEntityB);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_getEntitiesOfGroup_ThrowsIllegalArgumentExceptionWhenPassedGroupIsNull()
    {
        testee.getEntitiesOfGroup(null);
    }


    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_getEntitiesOfGroup_ThrowsInvalidGroupExceptionWhenPassedGroupIsInvalid()
    {
        final Group testGroup = mock(Group.class);

        when(testGroup.isInvalid()).thenReturn(true);
        when(testGroup.isValid()).thenReturn(false);

        testee.getEntitiesOfGroup(testGroup);
    }

    @Test
    public void test_findGroups_With_Context_Returns_the_expected_List_of_Groups()
    {
        final List<EntityRef> listOfEntities = asList(testRefC, testRefD);
        final List<Group> groups;

        new Expectations() {{
            context.isDisposed(); result = false;
            context.getService(EntityManager.class); result = testEntityManager;
            context.getService(ComponentManager.class); result = testComponentManager;
            testRefC.getContext(); result = testContext;
            testRefD.getContext(); result = testContext;
            testEntityManager.findEntities((Context) any, (Aspect) any); result = listOfEntities;
            testComponentManager.getComponent((EntityRef) any, GroupComponent.class); result = testGroupComponent;
        }};

        groups = testee.findGroups(testContext);

        assertThat(groups)
            .withFailMessage("Expected that the result list contains the same number of elements as the passed list of entities!")
            .hasSize(listOfEntities.size());
    }

    @Test
    public void test_GroupMembership()
    throws Exception
    {
        final Group testGroup = testee.createGroup(context);

        testee.addEntityToGroup(testEntityA, testGroup);

        assertThat(testee.isEntityMemberOfGroup(testEntityA, testGroup)).isTrue();
        assertThat(testee.isEntityMemberOfGroup(testEntityB, testGroup)).isFalse();

        testee.addEntityToGroup(testEntityB, testGroup);

        assertThat(testee.isEntityMemberOfGroup(testEntityA, testGroup)).isTrue();
        assertThat(testee.isEntityMemberOfGroup(testEntityB, testGroup)).isTrue();

        testee.removeEntityFromGroup(testEntityA, testGroup);

        assertThat(testee.isEntityMemberOfGroup(testEntityA, testGroup)).isFalse();
        assertThat(testee.isEntityMemberOfGroup(testEntityB, testGroup)).isTrue();

        testee.removeEntityFromGroup(testEntityB, testGroup);

        assertThat(testee.isEntityMemberOfGroup(testEntityA, testGroup)).isFalse();
        assertThat(testee.isEntityMemberOfGroup(testEntityB, testGroup)).isFalse();
    }
}
