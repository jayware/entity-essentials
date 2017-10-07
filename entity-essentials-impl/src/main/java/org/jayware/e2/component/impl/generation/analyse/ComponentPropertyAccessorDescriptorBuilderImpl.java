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
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder.ComponentPropertyAccessorDescriptorMethodNameBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder.ComponentPropertyAccessorDescriptorMethodNameBuilder.ComponentPropertyAccessorDescriptorMethodTypeBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder.ComponentPropertyAccessorDescriptorPropertyBuilder;

import java.lang.reflect.Method;


public class ComponentPropertyAccessorDescriptorBuilderImpl
implements ComponentPropertyAccessorDescriptorBuilder, ComponentPropertyAccessorDescriptorPropertyBuilder,
           ComponentPropertyAccessorDescriptorMethodNameBuilder, ComponentPropertyAccessorDescriptorMethodTypeBuilder
{
    private Method myCurrentAccessor;
    private String myCurrentAccessorName;
    private AccessorType myCurrentAccessorType;
    private String myCurrentPropertyName;
    private Class myCurrentPropertyType;

    @Override
    public ComponentPropertyAccessorDescriptorMethodNameBuilder accessor(Method accessor)
    {
        myCurrentAccessor = accessor;
        return this;
    }

    @Override
    public ComponentPropertyAccessorDescriptorMethodTypeBuilder name(String name)
    {
        myCurrentAccessorName = name;
        return this;
    }

    @Override
    public ComponentPropertyAccessorDescriptorBuilder type(AccessorType type)
    {
        myCurrentAccessorType = type;
        return this;
    }

    @Override
    public ComponentPropertyAccessorDescriptorPropertyBuilder property(String name)
    {
        myCurrentPropertyName = name;
        return this;
    }

    @Override
    public ComponentPropertyAccessorDescriptorBuilder type(Class<?> type)
    {
        myCurrentPropertyType = type;
        return this;
    }

    @Override
    public ComponentPropertyAccessorDescriptor build()
    {
        return new ComponentPropertyAccessorDescriptorImpl(myCurrentAccessor, myCurrentAccessorName, myCurrentAccessorType, myCurrentPropertyName, myCurrentPropertyType);
    }

    private static class ComponentPropertyAccessorDescriptorImpl
    implements ComponentPropertyAccessorDescriptor
    {
        private final Method accessor;
        private final String accessorName;
        private final AccessorType accessorType;
        private final String propertyName;
        private final Class propertyType;

        private ComponentPropertyAccessorDescriptorImpl(Method accessor, String accessorName, AccessorType accessorType, String propertyName, Class propertyType)
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
        public boolean hasAccessorType(final AccessorType type)
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

        @Override
        public Class<? extends Component> getDeclaringComponent()
        {
            return (Class<? extends Component>) accessor.getDeclaringClass();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (!(obj instanceof ComponentPropertyAccessorDescriptor))
            {
                return false;
            }

            final ComponentPropertyAccessorDescriptor that = (ComponentPropertyAccessorDescriptor) obj;

            return getAccessor().equals(that.getAccessor());
        }

        @Override
        public int hashCode()
        {
            return accessor.hashCode();
        }
    }
}
