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
package org.jayware.e2.assembly.api;


public interface TreeEvent
extends AssemblyEvent
{
    String NodeParam = "org.jayware.e2.event.param.Node";

    String ParentNodeParam = "org.jayware.e2.event.param.NodePendantParam";

    String NodePendantParam = "org.jayware.e2.event.param.NodePendantParam";

    interface CreateTreeNodeEvent extends TreeEvent {}

    interface TreeNodeCreatedEvent extends TreeEvent {}

    interface DeleteTreeNodeEvent extends TreeEvent {}

    interface DeletingTreeNodeEvent extends TreeEvent {}

    interface TreeNodeDeletedEvent extends TreeEvent {}

    interface AddChildNodeEvent extends TreeEvent {}

    interface ChildNodeAddedEvent extends TreeEvent {}

    interface RemoveChildNodeEvent extends TreeEvent {}

    interface ChildNodeRemovedEvent extends TreeEvent {}

    interface FindChildrenQuery
    extends TreeEvent
    {
        String ChildrenParam = "org.jayware.e2.query.param.ChildrenParam";
    }
}
