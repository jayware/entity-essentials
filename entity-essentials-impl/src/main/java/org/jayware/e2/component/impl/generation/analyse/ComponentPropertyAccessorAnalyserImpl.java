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
package org.jayware.e2.component.impl.generation.analyse;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorAnalyser;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessor.AccessorType.FLUENT_WRITE;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessor.AccessorType.READ;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessor.AccessorType.WRITE;


public class ComponentPropertyAccessorAnalyserImpl
implements ComponentPropertyAccessorAnalyser
{
    private final Set<AccessorMatcher> matchers;

    public ComponentPropertyAccessorAnalyserImpl()
    {
        Set<AccessorMatcher> matcherSet = new HashSet<AccessorMatcher>();
        matcherSet.add(new SimplePrefixedGetterMatcher());
        matcherSet.add(new SimplePrefixedSetterMatcher());
        matcherSet.add(new FluentPrefixedSetterMatcher());

        matchers = Collections.unmodifiableSet(matcherSet);
    }

    @Override
    public ComponentPropertyAccessor analyse(Method method)
    {
        for (AccessorMatcher matcher : matchers)
        {
            if (matcher.matches(method))
            {
                return matcher.parse(method);
            }
        }

        throw new MalformedComponentException("Invalid component property accessor: " + method);
    }

    private interface AccessorMatcher
    {
        boolean matches(Method method);

        ComponentPropertyAccessor parse(Method method);
    }

    private static class SimplePrefixedGetterMatcher
    implements AccessorMatcher
    {
        @Override
        public boolean matches(Method method)
        {
            final String methodName = method.getName();
            final Class<?> returnType = method.getReturnType();

            return methodName.startsWith("get") && !void.class.equals(returnType) && method.getParameterTypes().length == 0;
        }

        @Override
        public ComponentPropertyAccessor parse(final Method method)
        {
            final String accessorName = parseAccessorName(method);
            final String propertyName = parsePropertyName(method);
            final Class propertyType = parsePropertyType(method);

            return new ComponentPropertyAccessorImpl(method, accessorName, READ, propertyName, propertyType);
        }

        private String parseAccessorName(final Method method)
        {
            return method.getName();
        }

        private String parsePropertyName(final Method method)
        {
            String propertyName = method.getName().substring(3);
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

            return propertyName;
        }

        private Class parsePropertyType(final Method method)
        {
            return method.getReturnType();
        }
    }

    private static class SimplePrefixedSetterMatcher
    implements AccessorMatcher
    {
        @Override
        public boolean matches(Method method)
        {
            final String methodName = method.getName();
            final Class<?> returnType = method.getReturnType();

            return methodName.startsWith("set") && void.class.equals(returnType) && method.getParameterTypes().length == 1;
        }

        @Override
        public ComponentPropertyAccessor parse(final Method method)
        {
            final String accessorName = parseAccessorName(method);
            final String propertyName = parsePropertyName(method);
            final Class propertyType = parsePropertyType(method);

            return new ComponentPropertyAccessorImpl(method, accessorName, WRITE, propertyName, propertyType);
        }

        private String parseAccessorName(final Method method)
        {
            return method.getName();
        }

        private String parsePropertyName(final Method method)
        {
            String propertyName = method.getName().substring(3);
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

            return propertyName;
        }

        private Class parsePropertyType(final Method method)
        {
            return method.getParameterTypes()[0];
        }
    }


    private static class FluentPrefixedSetterMatcher
    implements AccessorMatcher
    {
        @Override
        public boolean matches(Method method)
        {
            final String methodName = method.getName();
            final Class<?> returnType = method.getReturnType();

            return methodName.startsWith("with") && Component.class.isAssignableFrom(returnType) && method.getParameterTypes().length == 1;
        }

        @Override
        public ComponentPropertyAccessor parse(final Method method)
        {
            final String accessorName = parseAccessorName(method);
            final String propertyName = parsePropertyName(method);
            final Class propertyType = parsePropertyType(method);

            return new ComponentPropertyAccessorImpl(method, accessorName, FLUENT_WRITE, propertyName, propertyType);
        }

        private String parseAccessorName(final Method method)
        {
            return method.getName();
        }

        private String parsePropertyName(final Method method)
        {
            String propertyName = method.getName().substring(4);
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

            return propertyName;
        }

        private Class parsePropertyType(final Method method)
        {
            return method.getParameterTypes()[0];
        }
    }

    private static class ComponentPropertyAccessorImpl
    implements ComponentPropertyAccessor
    {
        private final Method accessor;
        private final String accessorName;
        private final AccessorType accessorType;
        private final String propertyName;
        private final Class propertyType;

        private ComponentPropertyAccessorImpl(Method accessor, String accessorName, AccessorType accessorType, String propertyName, Class propertyType)
        {
            this.accessor = accessor;
            this.accessorName = accessorName;
            this.accessorType = accessorType;
            this.propertyName = propertyName;
            this.propertyType = propertyType;
        }

        @Override
        public Method getAccessor()
        {
            return accessor;
        }

        @Override
        public String getAccessorName()
        {
            return accessorName;
        }

        @Override
        public AccessorType getAccessorType()
        {
            return accessorType;
        }

        @Override
        public boolean isAccessorType(final AccessorType type)
        {
            return accessorType.equals(type);
        }

        @Override
        public String getPropertyName()
        {
            return propertyName;
        }

        @Override
        public Class getPropertyType()
        {
            return propertyType;
        }
    }
}
