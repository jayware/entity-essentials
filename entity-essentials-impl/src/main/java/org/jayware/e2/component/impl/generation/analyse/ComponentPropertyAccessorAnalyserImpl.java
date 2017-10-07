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
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.FLUENT_WRITE;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.READ;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.WRITE;


public class ComponentPropertyAccessorAnalyserImpl
implements ComponentPropertyAccessorAnalyser
{
    private final Set<AccessorMatcher> myMatchers;
    private final ComponentPropertyAccessorDescriptorBuilder myBuilder;

    public ComponentPropertyAccessorAnalyserImpl()
    {
        myBuilder = new ComponentPropertyAccessorDescriptorBuilderImpl();

        Set<AccessorMatcher> matcherSet = new HashSet<AccessorMatcher>();
        matcherSet.add(new SimplePrefixedGetterMatcher(myBuilder));
        matcherSet.add(new SimplePrefixedSetterMatcher(myBuilder));
        matcherSet.add(new FluentPrefixedSetterMatcher(myBuilder));

        myMatchers = Collections.unmodifiableSet(matcherSet);
    }

    @Override
    public ComponentPropertyAccessorDescriptor analyse(Method method)
    {
        for (AccessorMatcher matcher : myMatchers)
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

        ComponentPropertyAccessorDescriptor parse(Method method);
    }

    private abstract static class AbstractAccessorMatcher
    implements AccessorMatcher
    {
        private final ComponentPropertyAccessorDescriptorBuilder myBuilder;

        private AbstractAccessorMatcher(ComponentPropertyAccessorDescriptorBuilder builder)
        {
            myBuilder = builder;
        }

        @Override
        public boolean matches(final Method method)
        {
            throw new UnsupportedOperationException("AbstractAccessorMatcher.matches");
        }

        @Override
        public ComponentPropertyAccessorDescriptor parse(final Method method)
        {
            return myBuilder.accessor(method).name(parseAccessorName(method)).type(parseAccessorType(method))
                            .property(parsePropertyName(method)).type(parsePropertyType(method))
                            .build();
        }

        protected abstract String parseAccessorName(Method method);

        protected abstract AccessorType parseAccessorType(Method method);

        protected abstract String parsePropertyName(Method method);

        protected abstract Class parsePropertyType(Method method);
    }

    private static class SimplePrefixedGetterMatcher extends AbstractAccessorMatcher
    implements AccessorMatcher
    {
        private SimplePrefixedGetterMatcher(ComponentPropertyAccessorDescriptorBuilder builder)
        {
            super(builder);
        }

        @Override
        public boolean matches(Method method)
        {
            final String methodName = method.getName();
            final Class<?> returnType = method.getReturnType();

            return methodName.startsWith("get") && !void.class.equals(returnType) && method.getParameterTypes().length == 0;
        }

        @Override
        protected String parseAccessorName(Method method)
        {
            return method.getName();
        }

        @Override
        protected AccessorType parseAccessorType(final Method method)
        {
            return READ;
        }

        @Override
        protected String parsePropertyName(Method method)
        {
            String propertyName = method.getName().substring(3);
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

            return propertyName;
        }

        @Override
        protected Class parsePropertyType(Method method)
        {
            return method.getReturnType();
        }
    }

    private static class SimplePrefixedSetterMatcher extends AbstractAccessorMatcher
    implements AccessorMatcher
    {
        private SimplePrefixedSetterMatcher(ComponentPropertyAccessorDescriptorBuilder builder)
        {
            super(builder);
        }

        @Override
        public boolean matches(Method method)
        {
            final String methodName = method.getName();
            final Class<?> returnType = method.getReturnType();

            return methodName.startsWith("set") && void.class.equals(returnType) && method.getParameterTypes().length == 1;
        }

        @Override
        protected String parseAccessorName(Method method)
        {
            return method.getName();
        }

        @Override
        protected AccessorType parseAccessorType(final Method method)
        {
            return WRITE;
        }

        @Override
        protected String parsePropertyName(Method method)
        {
            String propertyName = method.getName().substring(3);
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

            return propertyName;
        }

        @Override
        protected Class parsePropertyType(Method method)
        {
            return method.getParameterTypes()[0];
        }
    }

    private static class FluentPrefixedSetterMatcher extends AbstractAccessorMatcher
    implements AccessorMatcher
    {
        private FluentPrefixedSetterMatcher(ComponentPropertyAccessorDescriptorBuilder builder)
        {
            super(builder);
        }

        @Override
        public boolean matches(Method method)
        {
            final String methodName = method.getName();
            final Class<?> returnType = method.getReturnType();

            return methodName.startsWith("with") && Component.class.isAssignableFrom(returnType) && method.getParameterTypes().length == 1;
        }

        @Override
        protected String parseAccessorName(Method method)
        {
            return method.getName();
        }

        @Override
        protected AccessorType parseAccessorType(Method method)
        {
            return FLUENT_WRITE;
        }

        @Override
        protected String parsePropertyName(Method method)
        {
            String propertyName = method.getName().substring(4);
            propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

            return propertyName;
        }

        @Override
        protected Class parsePropertyType(Method method)
        {
            return method.getParameterTypes()[0];
        }
    }
}
