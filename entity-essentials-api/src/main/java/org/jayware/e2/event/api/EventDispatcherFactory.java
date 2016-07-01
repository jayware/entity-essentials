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


/**
 * A <code>EventDispatcherFactory</code> is responsible for constructing {@link EventDispatcher EventDispatchers}.
 *
 * @see EventManager
 * @see EventDispatcher
 * @since 1.0
 */
public interface EventDispatcherFactory {

	String PROPERTY_OUT_DIRECTORY = "org.jayware.e2.event.api.EventDispatcherFactory.outdir";
	String PROPERTY_SILENT_EVENT_DISPATCHERS = "org.jayware.e2.event.api.EventDispatcherFactory.silent-event-dispatchers";

	/**
	 * Creates an {@link EventDispatcher} according to the specified target.
	 * <p>
	 * An implementation may cache previous constructed {@link EventDispatcher EventDispatchers}
	 * </p>
	 *
	 * @param target a target for which an {@link EventDispatcher} is created
	 * @return an {@link EventDispatcher} for the specified target
	 */
	EventDispatcher createEventDispatcher(Class<?> target) throws EventDispatcherFactoryException;
}