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


import org.jayware.e2.context.api.Context;
import org.jayware.e2.util.Filter;


/**
 * An <code>EventFilter</code> is every time applied when an Event is dispatched to a subscriber.
 *
 * @see Event
 * @see EventManager
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