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
import mockit.Injectable;
import mockit.Mocked;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptorBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("ComponentDescriptorBuilder")
class ComponentDescriptorBuilderImplTest
{
    private ComponentDescriptorBuilder testee;

    private @Mocked ComponentPropertyDescriptor propertyDescriptor;
    private @Mocked ComponentPropertyDescriptor anotherPropertyDescriptor;
    private @Mocked ComponentPropertyAccessorDescriptor accessorDescriptorA;
    private @Mocked ComponentPropertyAccessorDescriptor accessorDescriptorB;
    private @Mocked ComponentPropertyAccessorDescriptor accessorDescriptorC;
    private @Injectable Method accessorA, accessorC;

    @BeforeEach
    void setUp()
    {
        testee = new ComponentDescriptorBuilderImpl();
    }

    @Test
    void should_build_an_empty_ComponentDescriptor()
    {
        final ComponentDescriptor descriptor = testee.describe(TestComponent.class).build();

        assertThat(descriptor.getDeclaringComponent()).isEqualTo(TestComponent.class);
        assertThat(descriptor.getPropertyDescriptors()).isEmpty();
        assertThat(descriptor.getPropertyAccessorDescriptors()).isEmpty();
    }

    @Test
    void should_build_the_expected_ComponentDescriptor()
    {
        final ComponentDescriptor descriptor;
        final List<ComponentPropertyDescriptor> propertyDescriptors;

        new Expectations() {{
            accessorDescriptorA.getDeclaringComponent(); result = TestComponent.class;
            accessorDescriptorA.getPropertyName(); result = "text";
            accessorDescriptorA.getPropertyType(); result = String.class;
            accessorDescriptorA.getAccessor(); result = accessorA;
            accessorDescriptorC.getDeclaringComponent(); result = AnotherTestComponent.class;
            accessorDescriptorC.getPropertyName(); result = "text";
            accessorDescriptorC.getPropertyType(); result = String.class;
            accessorDescriptorC.getAccessor(); result = accessorC;
        }};

        descriptor = testee.describe(TestComponent.class).hierarchy(asList(TestComponent.class, AnotherTestComponent.class)).addAccessor(accessorDescriptorA).addAccessor(accessorDescriptorC).build();

        propertyDescriptors = descriptor.getPropertyDescriptors();

        assertThat(propertyDescriptors).hasSize(1);
        assertThat(propertyDescriptors.get(0).getDeclaringComponent()).isEqualTo(TestComponent.class);
        assertThat(propertyDescriptors.get(0).getPropertyName()).isEqualTo("text");
        assertThat(propertyDescriptors.get(0).getPropertyType()).isEqualTo(String.class);
        assertThat(descriptor.getPropertyAccessorDescriptors("text")).containsExactlyInAnyOrder(accessorDescriptorA, accessorDescriptorC);
    }

    @Test
    void should_throw_an_XXX_if_an_added_accessor_does_not_belong_to_the_current_Component()
    {
        new Expectations() {{
            accessorDescriptorC.getDeclaringComponent(); result = AnotherTestComponent.class;
        }};

        assertThrows(Exception.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.describe(TestComponent.class).addAccessor(accessorDescriptorC);
            }
        });
    }

    @Test
    void should_throw_an_XXX_if_an_added_property_does_not_belong_to_the_current_Component()
    {
        new Expectations() {{
            propertyDescriptor.getDeclaringComponent(); result = AnotherTestComponent.class;
        }};

        assertThrows(Exception.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.describe(TestComponent.class).addProperty(propertyDescriptor);
            }
        });
    }

    @Test
    void should_throw_an_XXX_if_an_added_accessor_is_for_the_same_property_but_has_another_type()
    {
        new Expectations() {{
            propertyDescriptor.getPropertyName(); result = "text";
            propertyDescriptor.getPropertyType(); result = String.class;
            propertyDescriptor.getDeclaringComponent(); result = TestComponent.class;
            accessorDescriptorC.getPropertyName(); result = "text";
            accessorDescriptorC.getPropertyType(); result = Integer.class;
            accessorDescriptorC.getDeclaringComponent(); result = TestComponent.class;
        }};

        assertThrows(Exception.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.describe(TestComponent.class).addProperty(propertyDescriptor).addAccessor(accessorDescriptorC);
            }
        });
    }

    @Test
    void should_throw_an_IllegalArgumentException_if_null_is_passed_into_describe()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.describe(null);
            }
        });
    }

    @Test
    void should_throw_an_IllegalArgumentException_if_null_is_passed_into_addProperty()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.describe(TestComponent.class).addProperty(null);
            }
        });
    }

    @Test
    void should_throw_an_IllegalArgumentException_if_null_is_passed_into_addAccessor()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.describe(TestComponent.class).addAccessor(null);
            }
        });
    }

    @Test
    void should_throw_an_IllegalStateException_if_a_property_already_exists()
    {
        new Expectations() {{
            propertyDescriptor.getPropertyName(); result = "text";
            propertyDescriptor.getDeclaringComponent(); result = TestComponent.class;
            anotherPropertyDescriptor.getPropertyName(); result = "text";
            anotherPropertyDescriptor.getDeclaringComponent(); result = TestComponent.class;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.describe(TestComponent.class).addProperty(propertyDescriptor).addProperty(anotherPropertyDescriptor);
            }
        });
    }

    @Test
    void should_throw_an_IllegalStateException_if_an_accessor_is_added_twice()
    {
        new Expectations() {{
            accessorDescriptorA.getPropertyType(); result = String.class;
            accessorDescriptorA.getDeclaringComponent(); result = TestComponent.class;
            accessorDescriptorA.getAccessor(); result = accessorA;
            accessorDescriptorB.getPropertyType(); result = String.class;
            accessorDescriptorB.getDeclaringComponent(); result = TestComponent.class;
            accessorDescriptorB.getAccessor(); result = accessorA;
        }};

        assertThrows(IllegalStateException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.describe(TestComponent.class).addAccessor(accessorDescriptorA).addAccessor(accessorDescriptorB);
            }
        });
    }

    interface TestComponent
    extends Component
    {

    }

    interface AnotherTestComponent
    extends Component
    {

    }

    interface AThirdComponent
    extends TestComponent, AnotherTestComponent
    {

    }
}