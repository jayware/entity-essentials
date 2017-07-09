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
package org.jayware.e2.event.api;


import org.jayware.e2.util.ReferenceType;


/**
 * A <code>Subscription</code> is a triple of a subscriber, a {@link EventFilter} and an {@link EventDispatcher}.
 *
 * @see EventManager
 * @see EventFilter
 * @see EventDispatcher
 *
 * @since 1.0
 */
public interface Subscription
{
    /**
     * Returns this {@link Subscription Subscription's} subscriber.
     *
     * @return the subscriber, never <code>null</code>.
     */
    Object getSubscriber();

    /**
     * Returns this {@link Subscription Subscription's} {@link EventFilter}.
     *
     * @return the {@link EventFilter}, never <code>null</code>.
     */
    EventFilter[] getFilters();

    /**
     * Returns this {@link Subscription Subscription's} {@link EventDispatcher}.
     *
     * @return the {@link EventDispatcher}, never <code>null</code>.
     */
    EventDispatcher getEventDispatcher();

    /**
     * Returns the {@link Subscription Subscription's} {@link ReferenceType}.
     *
     * @return the {@link ReferenceType}, never <code>null</code>.
     */
    ReferenceType getReferenceType();

    /**
     * Invalidates this <code>Subscription</code>.
     */
    void invalidate();

    /**
     * Returns whether this <code>Subscription</code> is still a valid <code>Subscription</code>.
     *
     * @return <code>true</code> if this <code>Subscription</code> is valid, otherwise <code>false</code>.
     */
    boolean isValid();
}