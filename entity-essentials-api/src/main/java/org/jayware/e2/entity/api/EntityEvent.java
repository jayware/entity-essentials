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
package org.jayware.e2.entity.api;


import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.event.api.DeclarativeSanityChecker;
import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Presence;
import org.jayware.e2.event.api.SanityCheck;

import java.util.List;
import java.util.UUID;

@SanityCheck(EntityEvent.EntityEventSanityChecker.class)
public interface EntityEvent
extends RootEvent
{
    /**
     * A EntityIdParam is an instance of {@link String}.
     */
    String EntityIdParam = "org.jayware.e2.event.param.EntityId";

    /**
     * A EntityRefParam is an instance of {@link EntityRef}.
     */
    String EntityRefParam = "org.jayware.e2.event.param.EntityRef";

    String EntityRefListParam = "org.jayware.e2.event.param.EntityRefList";

    /**
     * A AspectParam is an instance of {@link Aspect}.
     */
    String AspectParam = "org.jayware.e2.event.param.AspectParam";

    /**
     * A FilterListParam is an instance of {@link List}.
     */
    String FilterListParam = "org.jayware.e2.event.param.FilterListParam";

    /**
     * Signals the creation of an entity.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Optional}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    @SanityCheck(CreateEntityEventSanityChecker.class)
    interface CreateEntityEvent extends EntityEvent, Command {}

    /**
     * Signals that an entity has been created.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    @SanityCheck(EntityCreatedEventSanityChecker.class)
    interface EntityCreatedEvent extends EntityEvent, Notification {}

    /**
     * Signals the deletion of an entity.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    @SanityCheck(DeleteEntityEventSanityChecker.class)
    interface DeleteEntityEvent extends EntityEvent, Command {}

    /**
     * Signals that an entity has been deleted.
     * <p>
     * <b>Note:</b> The {@link EntityRef} carried by an {@link Event} of this type will be invalid, because
     * at the time such an event is tiggered the entity has already been deleted.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    @SanityCheck(EntityDeletedEventSanityChecker.class)
    interface EntityDeletedEvent extends EntityEvent, Notification {}

    /**
     * Signals that an entity is going to be deleted.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    @SanityCheck(EntityDeletingEventSanityChecker.class)
    interface EntityDeletingEvent extends EntityEvent, Notification {}

    /**
     * Signals the deletion of entities within a {@link Context}.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#AspectParam}</td><td>{@link Presence#Optional}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    interface DeleteEntitiesEvent extends EntityEvent, Command {}

    /**
     * Signals that an entity has changed.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityRefParam}</td><td>{@link Presence#Required}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    @SanityCheck(EntityChangedEventSanityChecker.class)
    interface EntityChangedEvent extends EntityEvent, Notification {}

    /**
     * Signals that an {@link UUID} should be resolved to an {@link EntityRef}.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#EntityIdParam}</td><td>{@link Presence#Required}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    @SanityCheck(ResolveEntityEventSanityCheck.class)
    interface ResolveEntityEvent extends EntityEvent, Query {}

    /**
     * Signals that an {@link UUID} should be resolved to an {@link EntityRef}.
     * <p>
     * <b>Parameters:</b>
     * <table>
     *     <tr><td>{@link EntityEvent#ContextParam}</td><td>{@link Presence#Required}</td></tr>
     *     <tr><td>{@link EntityEvent#AspectParam}</td><td>{@link Presence#Optional}</td></tr>
     *     <tr><td>{@link EntityEvent#FilterListParam}</td><td>{@link Presence#Optional}</td></tr>
     *     <caption>Parameters</caption>
     * </table>
     */
    interface FindEntitiesEvent extends EntityEvent, Query {}

    class EntityEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(EntityChangedEvent.class).param(EntityRefParam, "EntityRefParam").instanceOf(EntityRef.class).notNull().done();
        }
    }

    class CreateEntityEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(CreateEntityEvent.class).param(EntityIdParam, "EntityIdParam").instanceOf(UUID.class).done();
        }
    }

    class EntityCreatedEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(EntityCreatedEvent.class).param(EntityIdParam, "EntityIdParam").instanceOf(UUID.class).notNull().done();
            checker.check(EntityCreatedEvent.class).param(EntityRefParam, "EntityRefParam").instanceOf(EntityRef.class).notNull().done();
        }
    }

    class DeleteEntityEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(EntityCreatedEvent.class).param(EntityIdParam, "EntityIdParam").instanceOf(UUID.class).notNull().done();
            checker.check(EntityCreatedEvent.class).param(EntityRefParam, "EntityRefParam").instanceOf(EntityRef.class).notNull().done();
        }
    }

    class EntityDeletedEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(EntityCreatedEvent.class).param(EntityIdParam, "EntityIdParam").instanceOf(UUID.class).notNull().done();
            checker.check(EntityCreatedEvent.class).param(EntityRefParam, "EntityRefParam").instanceOf(EntityRef.class).notNull().done();
        }
    }

    class EntityDeletingEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(EntityCreatedEvent.class).param(EntityIdParam, "EntityIdParam").instanceOf(UUID.class).notNull().done();
            checker.check(EntityCreatedEvent.class).param(EntityRefParam, "EntityRefParam").instanceOf(EntityRef.class).notNull().done();
        }
    }

    class DeleteEntitiesEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(EntityCreatedEvent.class).param(AspectParam, "AspectParam").instanceOf(Aspect.class).done();
        }
    }

    class EntityChangedEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(EntityCreatedEvent.class).param(EntityIdParam, "EntityIdParam").instanceOf(UUID.class).notNull().done();
            checker.check(EntityChangedEvent.class).param(EntityRefParam, "EntityRefParam").notNull().instanceOf(EntityRef.class).done();
        }
    }

    class ResolveEntityEventSanityCheck  extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(ResolveEntityEvent.class).param(EntityIdParam, "EntityIdParam").instanceOf(UUID.class).notNull().done();
        }
    }
}
