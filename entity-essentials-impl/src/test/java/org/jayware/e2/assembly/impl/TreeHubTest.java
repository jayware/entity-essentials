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

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.jayware.e2.assembly.api.TreeEvent.TreeNodeCreatedEvent;
import org.jayware.e2.assembly.api.TreeNode;
import org.jayware.e2.assembly.api.components.TreeNodeComponent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.assembly.api.TreeEvent.FindChildrenQuery.ChildrenParam;
import static org.jayware.e2.assembly.api.TreeEvent.NodeParam;
import static org.jayware.e2.assembly.api.TreeEvent.NodePendantParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class TreeHubTest
{
    private TreeHub testee;

    private @Mocked Context testContext;
    private @Mocked EventManager testEventManager;
    private @Mocked ComponentManager testComponentManager;
    private @Mocked EntityManager testEntityManager;

    private @Mocked Query testEvent;
    private @Mocked Query testQuery;

    private @Mocked EntityRef testTreeNodeRef;
    private @Mocked TreeNode testTreeNode;
    private @Mocked TreeNodeComponent testTreeNodeComponent;

    private @Mocked EntityRef testRefA;
    private @Mocked EntityRef testRefB;
    private @Mocked EntityRef testRefC;

    @BeforeMethod
    public void setUp()
    throws Exception
    {
        new Expectations() {{
            testContext.getService(EventManager.class); result = testEventManager;
            testContext.getService(EntityManager.class); result = testEntityManager;
            testContext.getService(ComponentManager.class); result = testComponentManager;
            testQuery.isQuery(); result = true; minTimes = 0;
        }};

        testee = new TreeHub(testContext);
    }

    @Test
    public void test_handleCreateTreeNodeEvent_Returns_a_TreeNode_as_query_result_for_passed_pendant()
    {
        new Expectations() {{
            testTreeNodeComponent.getPendant(); result = testRefA;
        }};

        testee.handleCreateTreeNodeEvent(testQuery, testRefA);

        new Verifications() {{
            final String paramName;
            final TreeNode paramValue;
            testQuery.result(paramName = withCapture(), paramValue = withCapture());

            assertThat(paramName).isEqualTo(NodeParam);
            assertThat(paramValue.getPendantRef()).isEqualTo(testRefA);
        }};
    }

    @Test
    public void test_handleCreateTreeNodeEvent_Fires_TreeNodeCreatedEvent_with_appropriate_parameters()
    {
        new Expectations() {{
            testTreeNodeComponent.getPendant(); result = testRefA;
        }};

        testee.handleCreateTreeNodeEvent(testQuery, testRefA);

        new Verifications() {{
            final TreeNode paramValue;
            final Parameter[] parameters;

            testEventManager.post(
                TreeNodeCreatedEvent.class,
                parameters = withCapture()
            ); times = 1;

            testQuery.result(NodeParam, paramValue = withCapture());

            assertThat(parameters).contains(param(ContextParam, testContext), param(NodeParam, paramValue), param(NodePendantParam, testRefA));
        }};
    }

    @Test
    public void test_handleDeleteTreeNodeEvent_Fires_TreeNodeDeletedEvent_with_appropriate_parameters()
    {
//        testee.handleDeleteTreeNodeEvent();
    }


    public void test_handleRemoveChildNodeEvent_Removes_the_expected_node()
    {

    }

    @Test
    public void test_handleFindChildrenQuery_Returns_the_expected_nodes_as_query_result()
    throws Exception
    {
        expectATreeNodeWithThreeChildren();

        testee.handleFindChildrenQuery(testQuery, testTreeNode, null);

        new Verifications() {{
            final String paramName;
            final List<TreeNode> paramValue;
            final List<EntityRef> refs = new ArrayList<EntityRef>();

            testQuery.result(paramName = withCapture(), paramValue = withCapture());

            for (TreeNode treeNode : paramValue)
            {
                refs.add(treeNode.getNodeRef());
            }

            assertThat(paramName).isEqualTo(ChildrenParam);
            assertThat(refs).containsExactlyInAnyOrder(testRefA, testRefB, testRefC);
        }};
    }

    @Test
    public void test_handleFindChildrenQuery_Fills_a_passed_list_instead_of_instantiating_a_new_one()
    throws Exception
    {
        final List<TreeNode> resultList = new ArrayList<TreeNode>();

        expectATreeNodeWithThreeChildren();

        testee.handleFindChildrenQuery(testQuery, testTreeNode, resultList);

        new Verifications() {{
            final List<TreeNode> paramValue;

            testQuery.result(anyString, paramValue = withCapture());

            assertThat(paramValue).isSameAs(resultList);
        }};
    }

    private void expectATreeNodeWithThreeChildren()
    {
        new Expectations() {{
            testTreeNode.getNodeRef(); result = testTreeNodeRef;
            testComponentManager.getComponent(testTreeNodeRef, TreeNodeComponent.class); result = testTreeNodeComponent;
            testTreeNodeComponent.getChildren(); result = new EntityRef[] {testRefA, testRefB, testRefC};
        }};
    }
}
