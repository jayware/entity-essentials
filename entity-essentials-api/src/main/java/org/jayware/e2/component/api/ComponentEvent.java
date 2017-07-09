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
package org.jayware.e2.component.api;

import org.jayware.e2.entity.api.EntityEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Presence;

import java.util.Set;


public interface ComponentEvent
extends RootEvent
{
    /**
     * The component's type ({@link Class}) which is subject of the event.
     */
    String ComponentTypeParam = "org.jayware.e2.event.param.ComponentType";

    /**
     * A {@link Set} of component types ({@link Class}) which is subject to the event.
     */
    String ComponentTypeCollectionParam = "org.jayware.e2.event.param.ComponentTypeCollectionParam";

    /**
     * The {@link Component} which is subject of the event.
     */
    String ComponentParam = "org.jayware.e2.event.param.Component";

    interface PrepareComponentEvent extends ComponentEvent, Command {}

    interface ComponentPreparedEvent extends ComponentEvent, Notification {}

    interface CreateComponentEvent extends ComponentEvent, Command {}

    interface ComponentCreatedEvent extends ComponentEvent, Notification {}

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
     *     <caption>Parameters</caption>
     * </table>
     */
    interface AddComponentEvent extends ComponentEvent, EntityChangedEvent, Command {}

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
     *     <caption>Parameters</caption>
     * </table>
     */
    interface ComponentAddedEvent extends ComponentEvent, EntityChangedEvent, Notification {}

    /**
     * Signals the removal of all components with the specified type from the entity referenced by the given {@link EntityRef}.
     * If the {@link ComponentEvent#ComponentTypeCollectionParam} is omitted, then all components are removed.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link ComponentEvent#ComponentTypeCollectionParam}</td><td>{@link Presence#Optional}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    interface RemoveComponentEvent extends ComponentEvent, EntityChangedEvent, Command {}

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
     *     <caption>Parameters</caption>
     * </table>
     */
    interface ComponentRemovedEvent extends ComponentEvent, EntityChangedEvent, Notification {}

    interface ComponentChangeEvent
    extends ComponentEvent, EntityChangedEvent
    {

    }

    interface PullComponentEvent extends ComponentChangeEvent, Command {}

    interface PushComponentEvent extends ComponentChangeEvent, Command {}

    interface ComponentPulledEvent
    extends ComponentChangeEvent, Notification
    {
        /**
         * The {@link Component} which is subject of the event before it was pulled.
         */
        String OldComponentParam = "org.jayware.e2.event.param.OldComponent";
    }

    interface ComponentPushedEvent
    extends ComponentChangeEvent, Notification
    {
        /**
         * The {@link Component} which is subject of the event before it was pushed.
         */
        String OldComponentParam = "org.jayware.e2.event.param.OldComponent";
    }

    /**
     * Queries all types of {@link Component}s of an entity.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    interface ComponentTypesQuery
    extends RootEvent
    {

    }
}