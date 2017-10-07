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
package org.jayware.e2.component.api;

public class Property<T>
{
    public final Class<T> type;
    public final Class<? extends Component> component;

    private Property(Class<T> type, Class<? extends Component> component)
    {
        this.type = type;
        this.component = component;
    }

    public static <T> Property<T> property(Class<T> type)
    {
        return new Property<T>(type, resolveDeclaringComponent());
    }

    @Override
    public String toString()
    {
        return "Property{" +
            "type='" + type.getName() + "', " +
            "component='" + component.getName() + "'" +
        '}';
    }

    private static Class<? extends Component> resolveDeclaringComponent()
    {
        StackTraceElement invocation = null;

        try
        {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            for (int i = 0; i < stackTrace.length; i++)
            {
                final StackTraceElement element = stackTrace[i];
                final String className = element.getClassName();
                final String methodName = element.getMethodName();

                final Class<?> declaringClass = Class.forName(className);

                if (isPropertyClass(className) && isPropertyMethod(methodName))
                {
                    invocation = stackTrace[i + 1];
                }

                if (isClassInitializer(methodName) && isComponent(declaringClass))
                {
                    return (Class<? extends Component>) declaringClass;
                }
            }
        }
        catch (Exception e)
        {
            throw new PropertyResolutionException("Failed to resolve the declaring component due to an Exception!", e);
        }

        throw new PropertyDeclarationException(invocation);
    }

    private static boolean isPropertyMethod(String methodName)
    {
        return "property".equals(methodName);
    }

    private static boolean isPropertyClass(String className)
    {
        return Property.class.getName().equals(className);
    }

    private static boolean isComponent(Class<?> declaringClass)
    {
        return Component.class.isAssignableFrom(declaringClass);
    }

    private static boolean isClassInitializer(String methodName)
    {
        return "<clinit>".equals(methodName);
    }
}
