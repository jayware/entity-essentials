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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @BeforeEach
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
