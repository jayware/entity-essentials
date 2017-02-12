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


import static java.lang.String.format;
import static org.jayware.e2.event.api.Inspector.generateReport;


public class EventDispatchException
extends RuntimeException
{
    private EventDispatchException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public static void throwEventDispatchException(Throwable cause, String message, Object... args)
    {
        throw new EventDispatchException(format(message, args), cause);
    }

    public static void throwEventDispatchExceptionWithReport(Throwable cause, Event event, String message, Object... args)
    {
        throw new EventDispatchException(generateReport(format(message, args), event), cause);
    }
}
