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
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder.ComponentPropertyAccessorDescriptorBuilderTerminal;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder.ComponentPropertyAccessorDescriptorDeclaringComponentBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder.ComponentPropertyAccessorDescriptorMethodNameBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder.ComponentPropertyAccessorDescriptorMethodNameBuilder.ComponentPropertyAccessorDescriptorMethodTypeBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder.ComponentPropertyAccessorDescriptorPropertyBuilder;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

import static org.objectweb.asm.Type.getMethodDescriptor;
import static org.objectweb.asm.Type.getType;


public class ComponentPropertyAccessorDescriptorBuilderImpl
implements ComponentPropertyAccessorDescriptorBuilder, ComponentPropertyAccessorDescriptorPropertyBuilder,
           ComponentPropertyAccessorDescriptorMethodNameBuilder, ComponentPropertyAccessorDescriptorMethodTypeBuilder,
           ComponentPropertyAccessorDescriptorDeclaringComponentBuilder, ComponentPropertyAccessorDescriptorBuilderTerminal
{
    private Class<? extends Component> myCurrentDeclaringComponent;
    private String myCurrentMethodDescriptor;
    private String myCurrentAccessorName;
    private AccessorType myCurrentAccessorType;
    private String myCurrentPropertyName;
    private Class myCurrentPropertyType;

    @Override
    public ComponentPropertyAccessorDescriptorMethodNameBuilder accessor(Method accessor)
    {
        myCurrentMethodDescriptor = getMethodDescriptor(accessor);
        myCurrentDeclaringComponent = (Class<? extends Component>) accessor.getDeclaringClass();
        return this;
    }

    @Override
    public ComponentPropertyAccessorDescriptorDeclaringComponentBuilder accessor(Class returnType, Class... parameters)
    {
        final Type[] parameterTypes = new Type[parameters.length];
        for (int i = 0; i < parameters.length; i++)
        {
            parameterTypes[i] = getType(parameters[i]);
        }

        myCurrentMethodDescriptor = getMethodDescriptor(getType(returnType), parameterTypes);
        return this;
    }

    @Override
    public ComponentPropertyAccessorDescriptorMethodNameBuilder declaringComponent(Class<? extends Component> component)
    {
        myCurrentDeclaringComponent = component;
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
    public ComponentPropertyAccessorDescriptorBuilderTerminal type(Class<?> type)
    {
        myCurrentPropertyType = type;
        return this;
    }

    @Override
    public ComponentPropertyAccessorDescriptor build()
    {
        return new ComponentPropertyAccessorDescriptorImpl(myCurrentDeclaringComponent, myCurrentMethodDescriptor, myCurrentAccessorName, myCurrentAccessorType, myCurrentPropertyName, myCurrentPropertyType);
    }

    private static class ComponentPropertyAccessorDescriptorImpl
    implements ComponentPropertyAccessorDescriptor
    {
        private final Class<? extends Component> declaringComponent;
        private final String methodDescriptor;
        private final String accessorName;
        private final AccessorType accessorType;
        private final String propertyName;
        private final Class propertyType;

        private ComponentPropertyAccessorDescriptorImpl(Class<? extends Component> declaringComponent, String methodDescriptor, String accessorName, AccessorType accessorType, String propertyName, Class propertyType)
        {
            this.declaringComponent = declaringComponent;
            this.methodDescriptor = methodDescriptor;
            this.accessorName = accessorName;
            this.accessorType = accessorType;
            this.propertyName = propertyName;
            this.propertyType = propertyType;
        }

        @Override
        public String getAccessorMethodDescriptor()
        {
            return methodDescriptor;
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
            return declaringComponent;
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (!(o instanceof ComponentPropertyAccessorDescriptorImpl))
            {
                return false;
            }

            final ComponentPropertyAccessorDescriptorImpl that = (ComponentPropertyAccessorDescriptorImpl) o;

            if (!getDeclaringComponent().equals(that.getDeclaringComponent()))
            {
                return false;
            }
            if (!methodDescriptor.equals(that.methodDescriptor))
            {
                return false;
            }
            return getAccessorName().equals(that.getAccessorName());
        }

        @Override
        public int hashCode()
        {
            int result = getDeclaringComponent().hashCode();
            result = 31 * result + methodDescriptor.hashCode();
            result = 31 * result + getAccessorName().hashCode();
            return result;
        }
    }
}
