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


public class Preconditions
{
    /**
     * Ensures that a {@link Group} passed as a parameter to the calling method is not null and valid.
     *
     * @param group a {@link Group}
     *
     * @return the {@link Group} that was validated.
     *
     * @throws IllegalArgumentException if {@link Group} is null.
     * @throws InvalidGroupException if {@link Group} is invalid.
     */
    public static Group checkGroupNotNullAndValid(Group group)
    {
        if (group == null)
        {
            throw new IllegalArgumentException();
        }

        if (group.isInvalid())
        {
            throw new InvalidGroupException(group);
        }

        return group;
    }

    /**
     * Ensures that a {@link Node} passed as a parameter to the calling method is not null and valid.
     *
     * @param node a {@link Node}.
     * @param <N> the concrete type.
     *
     * @return the {@link Node} that was validated.
     *
     * @throws IllegalArgumentException if {@link Node} is null.
     * @throws InvalidNodeException if {@link Node} is invalid.
     * @throws InvalidNodeException if the {@link Node}'s pendant is invalid.
     */
    public static <N extends Node> N checkNodeNotNullAndValid(N node)
    {
        // TODO: Write better exception messages!
        if (node == null)
        {
            throw new IllegalArgumentException();
        }

        if (node.isInvalid())
        {
            throw new InvalidNodeException("Pendant entity of node is invalid!", node);
        }

        if (node.getNodeRef() == null)
        {
            throw new InvalidNodeException("Entity of node is null!", node);
        }

        if (node.getNodeRef().isInvalid())
        {
            throw new InvalidNodeException("Entity of node is invalid!", node);
        }

        return node;
    }
}
