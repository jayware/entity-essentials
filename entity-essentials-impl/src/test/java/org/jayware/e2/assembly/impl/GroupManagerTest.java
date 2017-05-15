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


import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.assembly.api.InvalidGroupException;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.entity.api.InvalidEntityRefException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GroupManagerTest
{
    private GroupManager testee;

    private Context context;

    private EntityRef testEntityA;
    private EntityRef testEntityB;

    @BeforeEach
    public void setUp()
    throws Exception
    {
        testee = new GroupManagerImpl();

        context = ContextProvider.getInstance().createContext();

        final EntityManager entityManager = context.getService(EntityManager.class);
        testEntityA = entityManager.createEntity(context);
        testEntityB = entityManager.createEntity(context);
    }

    @AfterEach
    public void tearDown()
    {
        context.dispose();
    }

    @Test
    public void testCreateGroup()
    {
        assertThat(testee.createGroup(context)).isNotNull();
    }

    @Test
    public void test_CreateGroup_ThrowsIllegalArgumentExceptionWhenPassedContextIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createGroup(null);
            }
        });
    }

    @Test
    public void test_CreateGroup_ThrowsIllegalStateExceptionWhenPassedContextIsDisposed()
    {
        final Context context = mock(Context.class);
        when(context.isDisposed()).thenReturn(true);

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createGroup(context);
            }
        });
    }

    @Test
    public void test_CreateGroupWithName_ThrowsIllegalArgumentExceptionWhenPassedContextIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createGroup(null, "fubar");
            }
        });
    }

    @Test
    public void test_CreateGroupWithName_ThrowsIllegalStateExceptionWhenPassedContextIsDisposed()
    {
        final Context context = mock(Context.class);
        when(context.isDisposed()).thenReturn(true);

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createGroup(context, "fubar");
            }
        });
    }

    @Test
    public void test_CreateGroupWithName_ThrowsIllegalArgumentExceptionWhenPassedStringIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createGroup(context, null);
            }
        });
    }

    @Test
    public void test_CreateGroupWithName_ThrowsIllegalArgumentExceptionWhenPassedNameIsEmpty()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.createGroup(context, "");
            }
        });
    }

    @Test
    public void test_deleteGroup()
    {
        final Group group = testee.createGroup(context);
        assertThat(group.isValid()).isTrue();
        testee.deleteGroup(group);
        assertThat(group.isValid()).isFalse();
    }

    @Test
    public void test_deleteGroup_ThrowsIllegalArgumentExceptionIfNUllIsPassed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.deleteGroup(null);
            }
        });
    }

    @Test
    public void test_addEntityToGroup_ThrowsIllegalArgumentExceptionWhenPassedEntityRefIsNull()
    {
        final Group testGroup = mock(Group.class);

        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addEntityToGroup(null, testGroup);
            }
        });
    }

    @Test
    public void test_addEntityToGroup_ThrowsInvalidEntityRefExceptionWhenPassedEntityRefIsInvalid()
    {
        final Group testGroup = mock(Group.class);
        final EntityRef ref = mock(EntityRef.class);

        when(ref.isInvalid()).thenReturn(true);
        when(ref.isValid()).thenReturn(false);
        when(ref.getContext()).thenReturn(context);

        assertThrows(InvalidEntityRefException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addEntityToGroup(ref, testGroup);
            }
        });
    }

    @Test
    public void test_addEntityToGroup_ThrowsIllegalArgumentExceptionWhenPassedGroupIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addEntityToGroup(testEntityA, null);
            }
        });
    }

    @Test
    public void test_addEntityToGroup_ThrowsInvalidGroupExceptionWhenPassedGroupIsInvalid()
    {
        final Group testGroup = mock(Group.class);

        when(testGroup.isInvalid()).thenReturn(true);
        when(testGroup.isValid()).thenReturn(false);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addEntityToGroup(testEntityA, testGroup);
            }
        });
    }

    @Test
    public void test_addEntityToGroup_ThrowsIllegalContextExceptionIfEntityAndGroupDoesNotBelongToTheSameContext()
    {
        final Context testContext = mock(Context.class);
        final Group testGroup = mock(Group.class);

        when(testGroup.getContext()).thenReturn(testContext);
        when(testGroup.isInvalid()).thenReturn(false);
        when(testGroup.isValid()).thenReturn(true);

        assertThrows(IllegalContextException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.addEntityToGroup(testEntityA, testGroup);
            }
        });
    }

    @Test
    public void test_removeEntityFromGroup_ThrowsIllegalArgumentExceptionWhenPassedEntityRefIsNull()
    {
        final Group testGroup = mock(Group.class);

        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.removeEntityFromGroup(null, testGroup);
            }
        });
    }

    @Test
    public void test_removeEntityFromGroup_ThrowsInvalidEntityRefExceptionWhenPassedEntityRefIsInvalid()
    {
        final Group testGroup = mock(Group.class);
        final EntityRef ref = mock(EntityRef.class);

        when(ref.isInvalid()).thenReturn(true);
        when(ref.isValid()).thenReturn(false);
        when(ref.getContext()).thenReturn(context);

        assertThrows(InvalidEntityRefException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.removeEntityFromGroup(ref, testGroup);
            }
        });
    }

    @Test
    public void test_removeEntityFromGroup_ThrowsIllegalArgumentExceptionWhenPassedGroupIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.removeEntityFromGroup(testEntityA, null);
            }
        });
    }

    @Test
    public void test_removeEntityFromGroup_ThrowsInvalidGroupExceptionWhenPassedGroupIsInvalid()
    {
        final Group testGroup = mock(Group.class);

        when(testGroup.isInvalid()).thenReturn(true);
        when(testGroup.isValid()).thenReturn(false);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.removeEntityFromGroup(testEntityA, testGroup);
            }
        });
    }

    @Test
    public void test_getEntitiesOfGroup()
    {
        final Group testGroup = testee.createGroup(context);
        testee.addEntityToGroup(testEntityA, testGroup);
        testee.addEntityToGroup(testEntityB, testGroup);

        assertThat(testee.getEntitiesOfGroup(testGroup)).contains(testEntityA, testEntityB);
    }

    @Test
    public void test_getEntitiesOfGroup_ThrowsIllegalArgumentExceptionWhenPassedGroupIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.getEntitiesOfGroup(null);
            }
        });
    }


    @Test
    public void test_getEntitiesOfGroup_ThrowsInvalidGroupExceptionWhenPassedGroupIsInvalid()
    {
        final Group testGroup = mock(Group.class);

        when(testGroup.isInvalid()).thenReturn(true);
        when(testGroup.isValid()).thenReturn(false);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.getEntitiesOfGroup(testGroup);
            }
        });
    }

    @Test
    public void test_findGroups_With_Context_Returns_the_expected_List_of_Groups()
    {
        final Group groupA = testee.createGroup(context);
        final Group groupB = testee.createGroup(context);

        final List<Group> groups = testee.findGroups(context);

        assertThat(groups)
            .withFailMessage("Expected that the result list contains the previously created groups!")
            .containsExactlyInAnyOrder(groupA, groupB);
    }

    @Test
    public void test_findGroups_With_EntityRef_Returns_the_expected_List_of_Groups()
    {
        final Group groupA = testee.createGroup(context);
        final Group groupB = testee.createGroup(context);
        final Group groupC = testee.createGroup(context);
        List<Group> groups;

        groupA.add(testEntityA);
        groupC.add(testEntityA);

        groups = testee.findGroups(testEntityA);

        assertThat(groups)
            .withFailMessage("Expected that the result list contains the groups %s and %s because the entity is member of these two groups!", groupA, groupB)
            .containsExactlyInAnyOrder(groupA, groupC);

        assertThat(groups)
            .withFailMessage("Expected that the result list does not contains group %s because the entity is not a member of this group!", groupA, groupB)
            .doesNotContain(groupB);
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
