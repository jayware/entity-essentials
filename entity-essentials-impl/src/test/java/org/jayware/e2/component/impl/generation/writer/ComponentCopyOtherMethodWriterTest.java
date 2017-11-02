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
package org.jayware.e2.component.impl.generation.writer;

import mockit.Mocked;
import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentFactory;
import org.jayware.e2.component.api.ComponentProperty;
import org.jayware.e2.component.impl.ComponentFactoryImpl;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.ComponentProperty.property;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ComponentCopyOtherMethodWriterTest
{
    private ComponentFactory componentFactory;
    private @Mocked Context testContext;

    private TestComponent testComponent, anotherComponent;
    private StrangerComponent strangerComponent;

    @BeforeEach
    void setUp()
    {
        componentFactory = new ComponentFactoryImpl();

        testComponent = componentFactory.createComponent(TestComponent.class).newInstance(testContext);
        anotherComponent = componentFactory.createComponent(TestComponent.class).newInstance(testContext);
        strangerComponent = componentFactory.createComponent(StrangerComponent.class).newInstance(testContext);
    }

    @Test
    void test_copy()
    {
        ((AbstractComponent) anotherComponent).set(TestComponent.text, "Hello World");

        ((AbstractComponent) testComponent).copy(anotherComponent);

        assertThat(((AbstractComponent) testComponent).get(TestComponent.text)).isEqualTo("Hello World");
    }

    @Test
    void test_that_copy_fails_if_the_passed_component_does_not_implement_the_same_Type()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                ((AbstractComponent) testComponent).copy(strangerComponent);
            }
        });
    }

    @Test
    void test_that_copy_fails_if_the_passed_component_does_not_implement_AbstractComponet()
    {
        final CustomImpl custom = new CustomImpl();

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                ((AbstractComponent) testComponent).copy(custom);
            }
        });
    }

    public interface TestComponent
    extends Component
    {
        ComponentProperty<String> text = property(String.class);

        void setInt(int value);

        int getInt();
    }

    public interface StrangerComponent
    extends Component
    {

    }

    private static class CustomImpl
    implements TestComponent
    {
        @Override
        public void setInt(final int value)
        {
            throw new UnsupportedOperationException("CustomImpl.setInt");
        }

        @Override
        public int getInt()
        {
            throw new UnsupportedOperationException("CustomImpl.getInt");
        }

        @Override
        public void pullFrom(final EntityRef ref)
        {
            throw new UnsupportedOperationException("CustomImpl.pullFrom");
        }

        @Override
        public void pushTo(final EntityRef ref)
        {
            throw new UnsupportedOperationException("CustomImpl.pushTo");
        }

        @Override
        public void addTo(final EntityRef ref)
        {
            throw new UnsupportedOperationException("CustomImpl.addTo");
        }

        @Override
        public Class<? extends Component> type()
        {
            throw new UnsupportedOperationException("CustomImpl.type");
        }
    }
}