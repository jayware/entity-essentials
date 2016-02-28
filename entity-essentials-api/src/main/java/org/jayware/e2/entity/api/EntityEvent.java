/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
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


import org.jayware.e2.event.api.DeclarativeSanityChecker;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.SanityCheck;

import static org.jayware.e2.entity.api.EntityEvent.ChildrenEntityEvent.ChildRefParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent.EntityRefParam;


@SanityCheck(EntityEvent.EntityEventSanityChecker.class)
public interface EntityEvent
extends RootEvent
{
    /**
     * The {@link EntityPath} of the entity which is subject of the event.
     */
    String EntityPathParam = "org.jayware.e2.event.param.EntityPath";

    interface CreateEntityEvent extends EntityEvent
    {
        /**
         * The id ({@link String}) of the entity which is subject of the event.
         */
        String EntityIdParam = "org.jayware.e2.event.param.EntityId";

        /**
         * The {@link EntityRef} of the entity which was created due to this event.
         */
        String EntityRefParam = "org.jayware.e2.event.param.EntityRef";
    }

    interface EntityCreatedEvent extends EntityEvent {}

    interface EntityDeletedEvent extends EntityEvent {}

    interface DeleteEntityEvent extends EntityEvent {}

    interface EntityDeletingEvent extends EntityEvent {}

    @SanityCheck(EntityChangedEvent.EntityChangedEventSanityChecker.class)
    interface EntityChangedEvent extends EntityEvent
    {
        /**
         * The {@link EntityRef} of the entity which is subject of the event.
         */
        String EntityRefParam = "org.jayware.e2.event.param.EntityRef";

        class EntityChangedEventSanityChecker
        extends DeclarativeSanityChecker
        {
            @Override
            protected void setup(SanityCheckerRuleBuilder checker)
            {
                checker.check(EntityChangedEvent.class).param(EntityRefParam, "EntityRefParam").notNull().instanceOf(EntityRef.class).done();
            }
        }
    }

    interface ChildrenEntityEvent extends EntityChangedEvent
    {
        /**
         * The {@link EntityRef} of the child which is subject of the event.
         */
        String ChildRefParam = "org.jayware.e2.event.param.ChildRef";
    }

    interface ChildAddedEntityEvent extends ChildrenEntityEvent {}

    interface ChildRemovedEntityEvent extends ChildrenEntityEvent {}

    class EntityEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            // TODO: Create new checks according to the new query-api!
//            checker.check(EntityEvent.class).param(EntityPathParam, "EntityPathParam").instanceOf(EntityPath.class).notNull().done();
            checker.check(EntityChangedEvent.class).param(EntityRefParam, "EntityRefParam").instanceOf(EntityRef.class).notNull().done();
            checker.check(ChildrenEntityEvent.class).param(ChildRefParam, "ChildRefParam").instanceOf(EntityRef.class).notNull().done();
        }
    }
}
