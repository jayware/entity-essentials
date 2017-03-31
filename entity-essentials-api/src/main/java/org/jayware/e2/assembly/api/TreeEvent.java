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
package org.jayware.e2.assembly.api;


public interface TreeEvent
extends AssemblyEvent
{
    String NodeParam = "org.jayware.e2.event.param.Node";

    String ParentNodeParam = "org.jayware.e2.event.param.NodePendantParam";

    String NodePendantParam = "org.jayware.e2.event.param.NodePendantParam";

    interface CreateTreeNodeEvent extends TreeEvent, Command {}

    interface TreeNodeCreatedEvent extends TreeEvent, Notification {}

    interface DeleteTreeNodeEvent extends TreeEvent, Command {}

    interface DeletingTreeNodeEvent extends TreeEvent, Notification {}

    interface TreeNodeDeletedEvent extends TreeEvent, Notification {}

    interface AddChildNodeEvent extends TreeEvent, Command {}

    interface ChildNodeAddedEvent extends TreeEvent, Notification {}

    interface RemoveChildNodeEvent extends TreeEvent, Command {}

    interface ChildNodeRemovedEvent extends TreeEvent, Notification {}

    interface FindChildrenQuery
    extends TreeEvent, Query
    {
        String ChildrenParam = "org.jayware.e2.query.param.ChildrenParam";
    }
}
