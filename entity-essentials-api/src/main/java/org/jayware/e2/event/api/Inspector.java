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


import org.jayware.e2.event.api.Parameters.Parameter;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.util.Arrays.asList;


public class Inspector
{
    public static final int TAB_SIZE = 3;

    private Inspector()
    {

    }

    public static String generateReport(Event event)
    {
        return generateReport(null, event);
    }

    public static String generateReport(String message, Event event)
    {
        final StringBuilder builder = new StringBuilder();

        appendMessage(message, builder);

        appendHeading(event, builder);
        appendHierarchy(event, builder);
        appendParameters(event, builder);
        appendEnd(builder);

        return builder.toString();
    }

    private static void appendMessage(final String message, final StringBuilder builder)
    {
        if (message != null)
        {
            builder.append(message).append('\n');
        }
    }

    private static void appendHeading(Event event, StringBuilder builder)
    {
        builder.append(tabs(1)).append(event.getType().getSimpleName()).append(':');
        builder.append(" <").append(event.getId()).append("> ");
        builder.append(event.isQuery() ? "(Query)" : "(Event)").append('\n');
    }

    private static void appendHierarchy(Event event, StringBuilder builder)
    {
        final Queue<HierarchyLevel> hierarchy = new LinkedList<HierarchyLevel>();

        builder.append('\n').append(tabs(2)).append("Hierarchy:\n");

        hierarchy.add(new HierarchyLevel(0, asList((Class) event.getType())));
        while (!hierarchy.isEmpty())
        {
            final HierarchyLevel actual = hierarchy.poll();

            for (Class type : actual.classes)
            {
                if (!type.equals(EventType.class))
                {
                    builder.append(tabs(3 + actual.level)).append(type.getName()).append('\n');
                    hierarchy.add(new HierarchyLevel(actual.level + 1, asList(type.getInterfaces())));
                }
            }
        }

        builder.append('\n');
    }

    private static void appendParameters(Event event, StringBuilder builder)
    {
        builder.append(tabs(2)).append("Parameters:");
        for (Parameter parameter : event.getParameters())
        {
            appendParameter(parameter, builder);
        }
    }

    private static void appendParameter(Parameter parameter, StringBuilder builder)
    {
        final String name = parameter.getName();
        final Object value = parameter.getValue();
        final Class<?> type = value != null ? value.getClass() : null;

        builder.append('\n');
        builder.append(tabs(3)).append("Name: ").append(name).append('\n');
        builder.append(tabs(3)).append("Value: ").append(value != null ? value : "<null>").append('\n');
        builder.append(tabs(3)).append("Type: ").append(value != null ? type.getName() : "<null>").append('\n');
    }

    private static void appendEnd(StringBuilder builder)
    {
        builder.append(tabs(1)).append("---");
    }

    private static char[] tabs(final int count)
    {
        final char[] tabs = new char[count * TAB_SIZE];

        for (int i = 0; i < tabs.length; i++)
        {
            tabs[i] = ' ';
        }

        return tabs;
    }

    private static class HierarchyLevel
    {
        private final int level;
        private final List<Class> classes;

        private HierarchyLevel(final int level, final List<Class> classes)
        {
            this.level = level;
            this.classes = classes;
        }
    }
}
