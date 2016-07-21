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

import org.jayware.e2.entity.api.EntityEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Presence;


public interface ComponentEvent
extends RootEvent
{
    /**
     * The component's type ({@link Class}) which is subject of the event.
     */
    String ComponentTypeParam = "org.jayware.e2.event.param.ComponentType";

    /**
     * The {@link Component} which is subject of the event.
     */
    String ComponentParam = "org.jayware.e2.event.param.Component";

    interface PrepareComponentEvent extends ComponentEvent {}

    interface ComponentPreparedEvent extends ComponentEvent {}

    interface CreateComponentEvent extends ComponentEvent {}

    interface ComponentCreatedEvent extends ComponentEvent {}

    /**
     * Signals the adding of a component to an entity.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link ComponentEvent#ComponentTypeParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link ComponentEvent#ComponentParam}</td><td>{@link Presence#Optional}</td></tr>
     * </table>
     */
    interface AddComponentEvent extends ComponentEvent, EntityChangedEvent {}

    /**
     * Signals that a component has been added to an entity.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link ComponentEvent#ComponentTypeParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link ComponentEvent#ComponentParam}</td><td>{@link Presence#Required}</td></tr>
     * </table>
     */
    interface ComponentAddedEvent extends ComponentEvent, EntityChangedEvent {}

    /**
     * Signals the removal of a component from an entity.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link ComponentEvent#ComponentTypeParam}</td><td>{@link Presence#Required}</td></tr>
     * </table>
     */
    interface RemoveComponentEvent extends ComponentEvent, EntityChangedEvent {}

    /**
     * Signals that a component has been removed from an entity.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link ComponentEvent#ComponentParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link ComponentEvent#ComponentTypeParam}</td><td>{@link Presence#Required}</td></tr>
     * </table>
     */
    interface ComponentRemovedEvent extends ComponentEvent, EntityChangedEvent {}

    interface ComponentChangeEvent
    extends ComponentEvent, EntityChangedEvent
    {

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