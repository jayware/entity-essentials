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
