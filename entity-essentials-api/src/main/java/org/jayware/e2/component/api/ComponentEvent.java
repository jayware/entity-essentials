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
package org.jayware.e2.component.api;

import org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent;
import org.jayware.e2.event.api.EventType.RootEvent;


public interface ComponentEvent
extends RootEvent
{
    /**
     * The component's type ({@link Class}) which is subject of the event.
     */
    String ComponentTypeParam = "org.jayware.e2.event.param.ComponentType";

    interface PrepareComponentEvent extends ComponentEvent {}

    interface ComponentPreparedEvent extends ComponentEvent {}

    interface AddComponentEvent extends ComponentEvent, EntityChangedEvent {}

    interface ComponentAddedEvent extends ComponentEvent, EntityChangedEvent {}

    interface RemoveComponentEvent extends ComponentEvent, EntityChangedEvent {}

    interface ComponentRemovedEvent extends ComponentEvent, EntityChangedEvent {}

    interface ComponentChangeEvent
    extends ComponentEvent, EntityChangedEvent
    {
        /**
         * The {@link Component} which is subject of the event.
         */
        String ComponentParam = "org.jayware.e2.event.param.Component";
    }

    interface PullComponentEvent extends ComponentChangeEvent {}

    interface PushComponentEvent extends ComponentChangeEvent {}

    interface ComponentPulledEvent
    extends ComponentChangeEvent
    {
        /**
         * The {@link Component} which is subject of the event before it was pulled.
         */
        String OldComponentParam = "org.jayware.e2.event.param.OldComponent";
    }

    interface ComponentPushedEvent
    extends ComponentChangeEvent
    {
        /**
         * The {@link Component} which is subject of the event before it was pushed.
         */
        String OldComponentParam = "org.jayware.e2.event.param.OldComponent";
    }
}