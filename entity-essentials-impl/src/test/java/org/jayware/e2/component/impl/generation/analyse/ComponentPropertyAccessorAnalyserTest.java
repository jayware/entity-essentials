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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessor.AccessorType.FLUENT_WRITE;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessor.AccessorType.READ;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessor.AccessorType.WRITE;


public class ComponentPropertyAccessorAnalyserTest
{
    private ComponentPropertyAccessorAnalyser testee;

    @BeforeEach
    public void setup()
    {
        testee = new ComponentPropertyAccessorAnalyserImpl();
    }

    @Test
    public void should_analyse_a_simple_prefixed_getter()
    throws NoSuchMethodException
    {
        final Method method = TestComponent.class.getDeclaredMethod("getText");

        final ComponentPropertyAccessor accessor = testee.analyse(method);

        assertThat(accessor.getAccessorName()).isEqualTo("getText");
        assertThat(accessor.getAccessorType()).isEqualTo(READ);
        assertThat(accessor.getPropertyName()).isEqualTo("text");
        assertThat(accessor.getPropertyType()).isEqualTo(String.class);
    }

    @Test
    public void should_analyse_a_simple_prefixed_setter()
    throws NoSuchMethodException
    {
        final Method method = TestComponent.class.getDeclaredMethod("setText", String.class);

        final ComponentPropertyAccessor accessor = testee.analyse(method);

        assertThat(accessor.getAccessorName()).isEqualTo("setText");
        assertThat(accessor.getAccessorType()).isEqualTo(WRITE);
        assertThat(accessor.getPropertyName()).isEqualTo("text");
        assertThat(accessor.getPropertyType()).isEqualTo(String.class);
    }

    @Test
    public void should_analyse_a_fluent_prefixed_setter()
    throws NoSuchMethodException
    {
        final Method method = TestComponent.class.getDeclaredMethod("withText", String.class);

        final ComponentPropertyAccessor accessor = testee.analyse(method);

        assertThat(accessor.getAccessorName()).isEqualTo("withText");
        assertThat(accessor.getAccessorType()).isEqualTo(FLUENT_WRITE);
        assertThat(accessor.getPropertyName()).isEqualTo("text");
        assertThat(accessor.getPropertyType()).isEqualTo(String.class);
    }

    @Test
    public void should_fail_with_a_MalformedComponentException_if_a_invalid_accessor_is_passed()
    throws NoSuchMethodException
    {
        final Method method = TestComponent.class.getDeclaredMethod("invalid", String.class);

        try
        {
            testee.analyse(method);
            fail("Expected a MalformedComponentException!");
        }
        catch (MalformedComponentException ignored)
        {
        }
    }

    public interface TestComponent
    extends Component
    {
        String getText();

        void setText(String text);

        TestComponent withText(String text);

        Object invalid(String bar);
    }
}