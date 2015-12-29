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


import org.jayware.e2.context.api.Context;
import org.jayware.e2.util.Filter;


/**
 * An <code>EventFilter</code> is every time applied when an {@link EventManager} dispatches an {@link Event} to a subscriber.
 *
 * @see Event
 * @see EventManager
 *
 * @since 1.0
 */
public interface EventFilter
extends Filter<Event>
{
    /**
     * A default filter which <b>accepts</b> all events.
     */
    EventFilter ANY = new EventFilter()
    {
        @Override
        public boolean accepts(Context context, Event event)
        {
            return true;
        }
    };

    /**
     * A default filter which <b>rejects</b> all events.
     */
    EventFilter NONE = new EventFilter()
    {
        @Override
        public boolean accepts(Context context, Event event)
        {
            return false;
        }
    };
}