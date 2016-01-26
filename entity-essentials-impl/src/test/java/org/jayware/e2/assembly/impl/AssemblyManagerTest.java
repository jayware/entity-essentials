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


import org.jayware.e2.assembly.api.AssemblyManager;
import org.jayware.e2.assembly.api.Group;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityPath;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityPath.path;


public class AssemblyManagerTest
{
    private AssemblyManager testee;

    private Context testContext;

    private EntityRef testEntityA;
    private EntityRef testEntityB;

    @BeforeMethod
    public void setUp()
    throws Exception
    {
        testee = new AssemblyManagerImpl();

        testContext = ContextProvider.getInstance().createContext();

        final EntityManager entityManager = testContext.getEntityManager();
        testEntityA = entityManager.createEntity(testContext, path("/a"));
        testEntityB = entityManager.createEntity(testContext, path("/b"));
    }

    @AfterMethod
    public void tearDown()
    throws Exception
    {
        testContext.dispose();
    }

    @Test
    public void testCreateGroup()
    throws Exception
    {
        assertThat(testee.createGroup(testContext)).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateGroupWithNullContext()
    {
        testee.createGroup(null);
    }

    @Test
    public void testMembership()
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
