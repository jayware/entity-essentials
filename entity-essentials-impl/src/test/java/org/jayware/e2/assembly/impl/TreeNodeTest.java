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

import org.jayware.e2.assembly.api.TreeManager;
import org.jayware.e2.assembly.api.TreeNode;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class TreeNodeTest
{
    private Context testContext;
    private TreeManager testTreeManager;

    private EntityRef testRefA;
    private EntityRef testRefB;
    private EntityRef testRefC;

    private TreeNode testNodeA;
    private TreeNode testNodeB;
    private TreeNode testNodeC;

    @BeforeMethod
    public void setUp()
    throws Exception
    {
        testContext = ContextProvider.getInstance().createContext();
        testTreeManager = testContext.getService(TreeManager.class);

        final EntityManager entityManager = testContext.getService(EntityManager.class);

        testRefA = entityManager.createEntity(testContext);
        testRefB = entityManager.createEntity(testContext);
        testRefC = entityManager.createEntity(testContext);

        testNodeA = testTreeManager.createTreeNodeFor(testRefA);
        testNodeB = testTreeManager.createTreeNodeFor(testRefB);
        testNodeC = testTreeManager.createTreeNodeFor(testRefC);
    }

    @AfterMethod
    public void tearDown()
    {
        testContext.dispose();
    }

    @Test
    public void test_children_Returns_a_list_containing_the_expected_TreeNodes()
    {
        testNodeA.addChild(testNodeB);
        testNodeA.addChild(testNodeC);

        assertThat(testNodeA.children()).containsExactlyInAnyOrder(testNodeB, testNodeC);
    }

    @Test
    public void test_children_Returns_an_empty_list_if_TreeNode_does_not_have_any_children()
    {
        assertThat(testNodeA.children()).isEmpty();
    }
}
