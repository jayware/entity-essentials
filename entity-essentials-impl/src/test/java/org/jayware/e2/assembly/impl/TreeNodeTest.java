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
