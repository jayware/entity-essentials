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


public class Inspector
{
    public static String generateReport(Event event)
    {
        return generateReport(null, event);
    }

    public static String generateReport(String message, Event event)
    {
        final StringBuilder messageBuilder = new StringBuilder();

        if (message != null)
        {
            messageBuilder.append(message).append('\n');
        }

        messageBuilder.append('\t').append(event.isQuery() ? "Query: " : "Event: ").append(event.getType().getName()).append('\n');
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
