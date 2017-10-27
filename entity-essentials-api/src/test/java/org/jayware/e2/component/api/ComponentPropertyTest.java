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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.ComponentProperty.property;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("ComponentProperty")
class ComponentPropertyTest
{

    @Test
    void should_provide_the_expected_information_about_type_and_component()
    {
        assertThat(TestComponent.aProperty).isNotNull();
        assertThat(TestComponent.aProperty.type).isEqualTo(String.class);
        assertThat(TestComponent.aProperty.component).isEqualTo(TestComponent.class);
    }

    @Test
    void should_throw_a_PropertyDeclarationException_if_the_operation_is_not_invoked_within_an_component_interface()
    {
        assertThrows(PropertyDeclarationException.class, new Executable()
        {
            @Override
            public void execute()
            {
                property(boolean.class);
            }
        });
    }

    @Test
    void should_return_an_descriptive_string()
    {
        assertThat(TestComponent.aProperty.toString()).contains(String.class.getName(), TestComponent.class.getName());
    }

    interface TestComponent
    extends Component
    {
        ComponentProperty<String> aProperty = property(String.class);
    }
}