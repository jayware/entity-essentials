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

import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;


/**
 * An <code>EventType</code>.
 *
 * @see Event
 * @see RootEvent
 * @see EventManager
 *
 * @since 1.0
 */
public interface EventType
{
    @SanityCheck(RootEventSanityChecker.class)
    interface RootEvent
    extends EventType
    {
        /**
         * The event's {@link Context}.
         */
        String ContextParam = "org.jayware.e2.api.event.param.Context";
    }

    class RootEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(RootEvent.class).param(ContextParam, "ContextParam").instanceOf(Context.class).notNull().done();
        }
    }
}
