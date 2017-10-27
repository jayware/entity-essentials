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
import org.jayware.e2.component.api.PropertyDeclarationException;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDeclarationAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.ComponentProperty.property;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("ComponentPropertyDeclarationAnalyser")
class ComponentPropertyDeclarationAnalyserImplTest
{
    private ComponentPropertyDeclarationAnalyser testee;
    private Field textPropertyField, numberPropertyField, arrayPropertyField, invalidPropertyField, aConstantField;

    @BeforeEach
    void setUp()
    throws Exception
    {
        testee = new ComponentPropertyDeclarationAnalyserImpl();

        textPropertyField = TestComponent.class.getDeclaredField("text");
        numberPropertyField = TestComponent.class.getDeclaredField("number");
        arrayPropertyField = TestComponent.class.getDeclaredField("array");
        invalidPropertyField = TestComponent.class.getDeclaredField("invalid");
        aConstantField = TestComponent.class.getDeclaredField("aConstant");
    }

    @Test
    void should_return_the_expected_ComponentPropertyDeclaration()
    {
        ComponentPropertyDescriptor declaration;

        declaration = testee.analyse(textPropertyField);

        assertThat(declaration.getPropertyName()).isEqualTo("text");
        assertThat(declaration.getPropertyType()).isEqualTo(String.class);
        assertThat(declaration.getDeclaringComponent()).isEqualTo(TestComponent.class);

        declaration = testee.analyse(numberPropertyField);

        assertThat(declaration.getPropertyName()).isEqualTo("number");
        assertThat(declaration.getPropertyType()).isEqualTo(int.class);
        assertThat(declaration.getDeclaringComponent()).isEqualTo(TestComponent.class);

        declaration = testee.analyse(arrayPropertyField);

        assertThat(declaration.getPropertyName()).isEqualTo("array");
        assertThat(declaration.getPropertyType()).isEqualTo(float[].class);
        assertThat(declaration.getDeclaringComponent()).isEqualTo(TestComponent.class);
    }

    @Test
    void should_throw_a_PropertyDeclarationException_if_a_Property_is_null()
    {
        assertThrows(PropertyDeclarationException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.analyse(invalidPropertyField);
            }
        });
    }

    @Test
    void should_throw_a_IllegalArgumentException_if_the_passed_Field_is_not_of_type_Property()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.analyse(aConstantField);
            }
        });
    }

    interface TestComponent
    extends Component
    {
        ComponentProperty text = property(String.class);
        ComponentProperty number = property(int.class);
        ComponentProperty array = property(float[].class);
        ComponentProperty invalid = null;

        String aConstant = "Fubar";
    }
}