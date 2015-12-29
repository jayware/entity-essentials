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


public class EventDispatchException
extends RuntimeException
{
    public EventDispatchException(String message, Event event)
    {
        super(buildMessage(message, event));
    }

    public EventDispatchException(String message, Event event, Throwable cause)
    {
        super(buildMessage(message, event), cause);
    }

    private static String buildMessage(String message, Event event)
    {
        final StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(message).append('\n');
        messageBuilder.append('\t').append("Event: ").append(event.getType().getName()).append('\n');
        messageBuilder.append('\t').append("Parameters: ");
        for (Parameters.Parameter parameter : event.getParameters())
        {
            messageBuilder.append('\n');
            messageBuilder.append("\t\t").append("Name: ").append(parameter.getName()).append('\n');
            messageBuilder.append("\t\t").append("Value: ").append(parameter.getValue()).append('\n');
        }

        return messageBuilder.toString();
    }
}
