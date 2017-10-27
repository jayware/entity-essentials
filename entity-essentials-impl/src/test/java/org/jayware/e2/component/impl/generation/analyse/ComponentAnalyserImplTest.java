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

import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentProperty;
import org.jayware.e2.component.api.generation.analyse.ComponentAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentAnalyserFactory;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptorBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentHierarchyAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDeclarationAnalyser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.jayware.e2.assertj.ComponentDescriptorAssertions.assertThat;
import static org.jayware.e2.component.api.ComponentProperty.property;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.READ;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.WRITE;


public class ComponentAnalyserImplTest
{
    private ComponentAnalyser testee;

    private ComponentDescriptorBuilder descriptorBuilder;
    private ComponentHierarchyAnalyser hierarchyAnalyser;
    private ComponentPropertyAccessorAnalyser propertyAccessorAnalyser;
    private ComponentPropertyDeclarationAnalyser propertyDeclarationAnalyser;

    private @Mocked ComponentAnalyserFactory analyserFactory;

    @BeforeEach
    void setUp()
    {
        descriptorBuilder = new ComponentDescriptorBuilderImpl();
        hierarchyAnalyser = new ComponentHierarchyAnalyserImpl();
        propertyAccessorAnalyser = new ComponentPropertyAccessorAnalyserImpl();
        propertyDeclarationAnalyser = new ComponentPropertyDeclarationAnalyserImpl();

        new Expectations() {{
           analyserFactory.createDescriptorBuilder(); result = descriptorBuilder;
           analyserFactory.createHierarchyAnalyser(); result = hierarchyAnalyser;
           analyserFactory.createPropertyAccessorAnalyser(); result = propertyAccessorAnalyser;
           analyserFactory.createPropertyDeclarationAnalyser(); result = propertyDeclarationAnalyser;
        }};

        testee = new ComponentAnalyserImpl(analyserFactory);
    }

    @Test
    void test()
    {
        final ComponentDescriptor descriptor = testee.analyse(TestComponentA.class);

        assertThat(descriptor).describesProperty("text", String.class);
        assertThat(descriptor).describesProperty("font", String.class);
        assertThat(descriptor).describesProperty("size", int.class);
        assertThat(descriptor).describesProperty("number", int.class);
        assertThat(descriptor).describesAccessor("getText", String.class, READ);
        assertThat(descriptor).describesAccessor("getNumber", int.class, READ);
        assertThat(descriptor).describesAccessor("setNumber", int.class, WRITE);
        assertThat(descriptor).describesAccessor("getSize", int.class, READ);
        assertThat(descriptor).describesAccessor("setSize", int.class, WRITE);
        assertThat(descriptor).describesComponent(TestComponentA.class);
    }

    interface TestComponentA
    extends TestComponentB
    {
        ComponentProperty text = property(String.class);

        ComponentProperty font = property(String.class);

        String getText();

        int getNumber();

        void setNumber(int number);
    }

    interface TestComponentB
    extends Component
    {
        ComponentProperty size = property(int.class);

        TestComponentB withSize(int size);

        void setSize(int size);

        int getSize();
    }
}
