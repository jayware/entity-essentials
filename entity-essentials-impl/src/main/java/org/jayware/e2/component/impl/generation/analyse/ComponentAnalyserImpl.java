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
import org.jayware.e2.component.api.generation.analyse.ComponentAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentAnalyserFactory;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptorBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptorBuilder.ComponentDescriptorBuilderDescribe;
import org.jayware.e2.component.api.generation.analyse.ComponentHierarchyAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDeclarationAnalyser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;


public class ComponentAnalyserImpl
implements ComponentAnalyser
{
    private final ComponentAnalyserFactory myAnalyserFactory;

    private final ComponentDescriptorBuilder descriptorBuilder;
    private final ComponentHierarchyAnalyser hierarchyAnalyser;
    private final ComponentPropertyAccessorAnalyser propertyAccessorAnalyser;
    private final ComponentPropertyDeclarationAnalyser propertyDeclarationAnalyser;

    public ComponentAnalyserImpl(ComponentAnalyserFactory analyserFactory)
    {
        myAnalyserFactory = analyserFactory;

        descriptorBuilder = myAnalyserFactory.createDescriptorBuilder();
        hierarchyAnalyser = myAnalyserFactory.createHierarchyAnalyser();
        propertyAccessorAnalyser = myAnalyserFactory.createPropertyAccessorAnalyser();
        propertyDeclarationAnalyser = myAnalyserFactory.createPropertyDeclarationAnalyser();
    }

    @Override
    public ComponentDescriptor analyse(Class<? extends Component> component)
    {
        final ComponentDescriptorBuilderDescribe builder = descriptorBuilder.describe(component);
        final Set<Class<? extends Component>> classes = hierarchyAnalyser.analyse(component);

        builder.hierarchy(classes);

        for (Class<? extends Component> aClass : classes)
        {
            for (Field field : aClass.getDeclaredFields())
            {
                builder.addProperty(propertyDeclarationAnalyser.analyse(field));
            }

            for (Method method : aClass.getDeclaredMethods())
            {
                builder.addAccessor(propertyAccessorAnalyser.analyse(method));
            }
        }

        return builder.build();
    }
}
