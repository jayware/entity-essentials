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


import org.jayware.e2.assembly.api.TreeEvent.FindChildrenQuery;
import org.jayware.e2.assembly.api.TreeManager;
import org.jayware.e2.assembly.api.TreeManagerException;
import org.jayware.e2.assembly.api.TreeNode;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.MissingResultException;
import org.jayware.e2.event.api.Result;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.util.Key;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jayware.e2.assembly.api.Preconditions.checkNodeNotNullAndValid;
import static org.jayware.e2.assembly.api.TreeEvent.CreateTreeNodeEvent;
import static org.jayware.e2.assembly.api.TreeEvent.DeleteTreeNodeEvent;
import static org.jayware.e2.assembly.api.TreeEvent.FindChildrenQuery.ChildrenParam;
import static org.jayware.e2.assembly.api.TreeEvent.NodeParam;
import static org.jayware.e2.assembly.api.TreeEvent.NodePendantParam;
import static org.jayware.e2.entity.api.Preconditions.checkRefNotNullAndValid;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Query.State.Success;


public class TreeManagerImpl
implements TreeManager
{
    private static final long COMMON_TIMEOUT_IN_MILLIS = 5000;

    static final Key<TreeHub> TREE_HUB = Key.createKey("org.jayware.e2.TreeHub");

    @Override
    public TreeNode createTreeNodeFor(EntityRef pendant)
    {
        checkRefNotNullAndValid(pendant);

        final Context context = pendant.getContext();
        final EventManager eventManager;
        final ResultSet resultSet;

        eventManager = context.getService(EventManager.class);
        resultSet = eventManager.query(CreateTreeNodeEvent.class,
            param(ContextParam, context),
            param(NodePendantParam, pendant)
        );

        await(resultSet);

        try
        {
            return resultSet.get(NodeParam);
        }
        catch (MissingResultException e)
        {
            throw new TreeManagerException("Failed to create TreeNode!", e);
        }
    }

    @Override
    public void deleteTreeNode(TreeNode node)
    {
        checkNodeNotNullAndValid(node);

        final Context context = node.getContext();
        final EventManager eventManager = context.getService(EventManager.class);

        eventManager.send(DeleteTreeNodeEvent.class,
                          param(ContextParam, context),
                          param(NodeParam, node));
    }

    @Override
    public List<TreeNode> findChildrenOf(TreeNode node)
    {
        return queryChildrenOf(node).find();
    }

    @Override
    public Result<List<TreeNode>> queryChildrenOf(TreeNode node)
    {
        checkNodeNotNullAndValid(node);

        final Context context = node.getContext();
        final EventManager eventManager = context.getService(EventManager.class);

        final ResultSet resultSet = eventManager.query(
            FindChildrenQuery.class,
            param(ContextParam, context),
            param(NodeParam, node)
        );

        return resultSet.resultOf(ChildrenParam);
    }

    private ResultSet await(ResultSet resultSet)
    {
        if (!resultSet.await(Success, 30, SECONDS))
        {
            throw new TreeManagerException("Query did not succeed within " + COMMON_TIMEOUT_IN_MILLIS + "ms");
        }

        return resultSet;
    }
}
