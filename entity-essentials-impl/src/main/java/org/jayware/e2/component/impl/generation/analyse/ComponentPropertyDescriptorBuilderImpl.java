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
import org.jayware.e2.component.api.ComponentProperty;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptorBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptorBuilder.ComponentPropertyDescriptorNameBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptorBuilder.ComponentPropertyDescriptorPropertyBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptorBuilder.ComponentPropertyDescriptorTypeBuilder;

import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptorBuilder.ComponentPropertyDescriptorDeclaringComponentBuilder;


public class ComponentPropertyDescriptorBuilderImpl
implements ComponentPropertyDescriptorBuilder, ComponentPropertyDescriptorTypeBuilder, ComponentPropertyDescriptorDeclaringComponentBuilder, ComponentPropertyDescriptorPropertyBuilder, ComponentPropertyDescriptorNameBuilder
{
    private String myCurrentName;
    private Class myCurrentType;
    private Class<? extends Component> myCurrentDeclaringComponent;
    private ComponentProperty myCurrentProperty = null;

    @Override
    public ComponentPropertyDescriptorPropertyBuilder property(ComponentProperty property)
    {
        myCurrentType = property.type;
        myCurrentDeclaringComponent = property.component;
        myCurrentProperty = property;
        return this;
    }

    @Override
    public ComponentPropertyDescriptorTypeBuilder property(String name)
    {
        myCurrentName = name;
        return this;
    }

    @Override
    public ComponentPropertyDescriptorDeclaringComponentBuilder type(Class<?> type)
    {
        myCurrentType = type;
        return this;
    }

    @Override
    public ComponentPropertyDescriptorBuilder declaringComponent(Class<? extends Component> declaringComponent)
    {
        myCurrentDeclaringComponent = declaringComponent;
        return this;
    }

    @Override
    public ComponentPropertyDescriptorNameBuilder name(String name)
    {
        myCurrentName = name;
        return this;
    }

    @Override
    public ComponentPropertyDescriptor build()
    {
        return new ComponentPropertyDescriptorImpl(myCurrentName, myCurrentType, myCurrentDeclaringComponent, myCurrentProperty);
    }

    private static class ComponentPropertyDescriptorImpl
    implements ComponentPropertyDescriptor
    {
        private final String myName;
        private final Class myType;
        private final Class<? extends Component> myDeclaringComponent;
        private final ComponentProperty myProperty;

        ComponentPropertyDescriptorImpl(String name, Class type, Class<? extends Component> declaringComponent, ComponentProperty property)
        {
            myName = name;
            myType = type;
            myDeclaringComponent = declaringComponent;
            myProperty = property;
        }

        @Override
        public String getPropertyName()
        {
            return myName;
        }

        @Override
        public Class getPropertyType()
        {
            return myType;
        }

        @Override
        public Class<? extends Component> getDeclaringComponent()
        {
            return myDeclaringComponent;
        }

        @Override
        public ComponentProperty getProperty()
        {
            return myProperty;
        }
    }
}
