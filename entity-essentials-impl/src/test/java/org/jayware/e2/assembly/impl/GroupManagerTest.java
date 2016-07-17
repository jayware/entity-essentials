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
package org.jayware.e2.assembly.impl;


import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.assembly.api.InvalidGroupException;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.entity.api.InvalidEntityRefException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GroupManagerTest
{
    private GroupManager testee;

    private Context testContext;

    private EntityRef testEntityA;
    private EntityRef testEntityB;

    @BeforeMethod
    public void setUp()
    throws Exception
    {
        testee = new GroupManagerImpl();

        testContext = ContextProvider.getInstance().createContext();

        final EntityManager entityManager = testContext.getService(EntityManager.class);
        testEntityA = entityManager.createEntity(testContext);
        testEntityB = entityManager.createEntity(testContext);
    }

    @AfterMethod
    public void tearDown()
    {
        testContext.dispose();
    }

    @Test
    public void testCreateGroup()
    {
        assertThat(testee.createGroup(testContext)).isNotNull();
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
        testee.createGroup(testContext, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_CreateGroupWithName_ThrowsIllegalArgumentExceptionWhenPassedNameIsEmpty()
    {
        testee.createGroup(testContext, "");
    }

    @Test
    public void test_deleteGroup()
    {
        final Group group = testee.createGroup(testContext);
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
        when(ref.getContext()).thenReturn(testContext);

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
        when(ref.getContext()).thenReturn(testContext);

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
        final Group testGroup = testee.createGroup(testContext);
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
    public void test_GroupMembership()
    throws Exception
    {
        final Group testGroup = testee.createGroup(testContext);

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
