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
import org.jayware.e2.assembly.api.InvalidGroupException;
import org.jayware.e2.assembly.api.components.GroupComponent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityPath.path;


public class GroupTest
{
    private Group testee;

    private Context testContext;
    private EntityManager testEntityManager;
    private ComponentManager testComponentManager;

    private EntityRef testeeRef;
    private EntityRef testEntityA;
    private EntityRef testEntityB;

    @BeforeMethod
    public void setUp()
    {
        testContext = ContextProvider.getInstance().createContext();
        testEntityManager = testContext.getEntityManager();
        testComponentManager = testContext.getComponentManager();

        testeeRef = testEntityManager.createEntity(testContext, path("/testee"));
        testComponentManager.addComponent(testeeRef, GroupComponent.class);

        testEntityA = testEntityManager.createEntity(testContext, path("/a"));
        testEntityB = testEntityManager.createEntity(testContext, path("/b"));

        testee = new GroupImpl(testeeRef);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_add_ThrowsInvalidGroupExceptionIfContextIsDisposed()
    {
        testContext.dispose();
        testee.add(testEntityA);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_add_ThrowsInvalidGroupExceptionIfBackingEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testeeRef);
        testee.add(testEntityA);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_add_ThrowsInvalidGroupExceptionIfBackingEntityDoesNotHaveGroupComponent()
    {
        testComponentManager.removeComponent(testeeRef, GroupComponent.class);
        testee.add(testEntityA);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_add_ThrowsIllegalArgumentExceptionIfNullIsPassed()
    {
        testee.add(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_add_ThrowsIllegalStateExceptionIfPassedEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testEntityA);
        testee.add(testEntityA);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_remove_ThrowsInvalidGroupExceptionIfContextIsDisposed()
    {
        testContext.dispose();
        testee.remove(testEntityA);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_remove_ThrowsInvalidGroupExceptionIfBackingEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testeeRef);
        testee.remove(testEntityA);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_remove_ThrowsInvalidGroupExceptionIfBackingEntityDoesNotHaveGroupComponent()
    {
        testComponentManager.removeComponent(testeeRef, GroupComponent.class);
        testee.remove(testEntityA);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_remove_ThrowsIllegalArgumentExceptionIfNullIsPassed()
    {
        testee.remove(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_remove_ThrowsIllegalStateExceptionIfPassedEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testEntityA);
        testee.remove(testEntityA);
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_members_ThrowsInvalidGroupExceptionIfContextIsDisposed()
    {
        testContext.dispose();
        testee.members();
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_members_ThrowsInvalidGroupExceptionIfBackingEntityIsInvalid()
    {
        testEntityManager.deleteEntity(testeeRef);
        testee.members();
    }

    @Test(expectedExceptions = InvalidGroupException.class)
    public void test_members_ThrowsInvalidGroupExceptionIfBackingEntityDoesNotHaveGroupComponent()
    {
        testComponentManager.removeComponent(testeeRef, GroupComponent.class);
        testee.members();
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