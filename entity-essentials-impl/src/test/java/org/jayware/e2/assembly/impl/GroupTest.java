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
import org.jayware.e2.assembly.api.InvalidGroupException;
import org.jayware.e2.assembly.api.components.GroupComponent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.entity.api.InvalidEntityRefException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.assembly.impl.GroupImpl.createGroup;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class GroupTest
{
    private static final String GROUP_NAME = "fubar";
    private Group testee;

    private Context testContext;
    private EntityManager testEntityManager;
    private ComponentManager testComponentManager;

    private EntityRef testeeRef;
    private EntityRef testEntityA;
    private EntityRef testEntityB;

    @BeforeEach
    public void setUp()
    {
        testContext = ContextProvider.getInstance().createContext();
        testEntityManager = testContext.getService(EntityManager.class);
        testComponentManager = testContext.getService(ComponentManager.class);

        testeeRef = testEntityManager.createEntity(testContext);
        testComponentManager.addComponent(testeeRef, GroupComponent.class);

        testEntityA = testEntityManager.createEntity(testContext);
        testEntityB = testEntityManager.createEntity(testContext);

        testee = createGroup(testeeRef);
    }

    @Test
    public void test_name()
    {
        testee.setName(GROUP_NAME);
        assertThat(testee.getName()).isEqualTo(GROUP_NAME);
    }

    @Test
    public void test_getName_ThrowsInvalidGroupExceptionIfGroupIsInvalid()
    {
        testEntityManager.deleteEntity(testeeRef);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.getName();
            }
        });
    }

    @Test
    public void test_setName_ThrowsInvalidGroupExceptionIfGroupIsInvalid()
    {
        testEntityManager.deleteEntity(testeeRef);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.setName("fubar");
            }
        });
    }

    @Test
    public void test_add_ThrowsInvalidGroupExceptionIfContextIsDisposed()
    {
        testContext.dispose();

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.add(testEntityA);
            }
        });
    }

    @Test
    public void test_add_ThrowsInvalidGroupExceptionIfBackingEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testeeRef);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.add(testEntityA);
            }
        });
    }

    @Test
    public void test_add_ThrowsInvalidGroupExceptionIfBackingEntityDoesNotHaveGroupComponent()
    {
        testComponentManager.removeComponent(testeeRef, GroupComponent.class);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.add(testEntityA);
            }
        });
    }

    @Test
    public void test_add_ThrowsIllegalArgumentExceptionIfNullIsPassed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.add(null);
            }
        });
    }

    @Test
    public void test_add_ThrowsInvalidEntityRefExceptionIfPassedEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testEntityA);

        assertThrows(InvalidEntityRefException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.add(testEntityA);
            }
        });
    }

    @Test
    public void test_remove_ThrowsInvalidGroupExceptionIfContextIsDisposed()
    {
        testContext.dispose();

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.remove(testEntityA);
            }
        });
    }

    @Test
    public void test_remove_ThrowsInvalidGroupExceptionIfBackingEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testeeRef);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.remove(testEntityA);
            }
        });
    }

    @Test
    public void test_remove_ThrowsInvalidGroupExceptionIfBackingEntityDoesNotHaveGroupComponent()
    {
        testComponentManager.removeComponent(testeeRef, GroupComponent.class);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.remove(testEntityA);
            }
        });
    }

    @Test
    public void test_remove_ThrowsIllegalArgumentExceptionIfNullIsPassed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.remove(null);
            }
        });
    }

    @Test
    public void test_remove_ThrowsInvalidEntityRefExceptionIfPassedEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testEntityA);

        assertThrows(InvalidEntityRefException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.remove(testEntityA);
            }
        });
    }

    @Test
    public void test_members_ThrowsInvalidGroupExceptionIfContextIsDisposed()
    {
        testContext.dispose();

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.members();
            }
        });
    }

    @Test
    public void test_members_ThrowsInvalidGroupExceptionIfBackingEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testeeRef);

        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.members();
            }
        });
    }

    @Test
    public void test_members_ThrowsInvalidGroupExceptionIfBackingEntityDoesNotHaveGroupComponent()
    {
        testComponentManager.removeComponent(testeeRef, GroupComponent.class);
        assertThrows(InvalidGroupException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.members();
            }
        });
    }

    @Test
    public void test_members_ContainAppropriateEntityRefs()
    {
        assertThat(testee.members()).isEmpty();
        testee.add(testEntityA);
        assertThat(testee.members()).containsOnlyOnce(testEntityA);
        testee.add(testEntityB);
        assertThat(testee.members()).containsOnlyOnce(testEntityA, testEntityB);
        testee.remove(testEntityA);
        assertThat(testee.members()).containsOnlyOnce(testEntityB);
        testee.remove(testEntityB);
        assertThat(testee.members()).isEmpty();
    }
}
