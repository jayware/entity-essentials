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
package org.jayware.e2.entity.api;

import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.context.api.IllegalContextException;
import org.jayware.e2.util.Filter;

import java.util.List;


public interface ContextualEntityManager
extends Contextual
{
    EntityRef createEntity() throws IllegalStateException;

    void deleteEntity(EntityRef ref) throws IllegalArgumentException, IllegalStateException, IllegalContextException;

    List<EntityRef> findEntities() throws IllegalStateException;

    List<EntityRef> findEntities(Aspect aspect) throws IllegalArgumentException, IllegalStateException;

    List<EntityRef> findEntities(Filter<EntityRef>... filters) throws IllegalArgumentException, IllegalStateException;

    List<EntityRef> findEntities(Aspect aspect, Filter<EntityRef>... filters) throws IllegalArgumentException, IllegalStateException;
}
