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