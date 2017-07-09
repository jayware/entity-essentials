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


/**
 * EventDispatcher
 *
 * @since 1.0
 *
 * @see Event
 * @see EventManager
 */
public interface EventDispatcher
{
    /**
	 * Dispatches the specified {@link Event} to the specified target.
     *
	 * @param event an {@link Event}
	 * @param target a target.
	 */
	void dispatch(Event event, Object target);

    /**
     * Returns whether this {@link EventDispatcher} accepts the specified {@link EventType}.
     *
     * @param type an {@link EventType}'s {@link Class}
     *
     * @return true if this {@link EventDispatcher} accepts the specified {@link EventType}, otherwise false.
     */
    boolean accepts(Class<? extends EventType> type);
}