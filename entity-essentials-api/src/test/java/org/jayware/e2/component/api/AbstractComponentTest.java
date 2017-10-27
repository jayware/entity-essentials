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

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.entity.api.EntityRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class AbstractComponentTest
{
    private TestImplementationOfAbstractComponent testee;

    private @Mocked Context testContext;
    private @Mocked Context otherTestContext;
    private @Mocked ComponentManager testComponentManager;
    private @Mocked Contextual testContextual;
    private @Mocked EntityRef testRef;

    @BeforeEach
    private void setup()
    {
        new Expectations() {{
           testContext.getService(ComponentManager.class); result = testComponentManager;
        }};

        testee = new TestImplementationOfAbstractComponent(testContext);
    }

    @Test
    public void test_that_pullFrom_invokes_pullComponent()
    {
        testee.pullFrom(testRef);

        new Verifications() {{
           testComponentManager.pullComponent(testRef, testee);
        }};
    }

    @Test
    public void test_that_pushTo_invokes_pushComponent()
    {
        testee.pushTo(testRef);

        new Verifications() {{
            testComponentManager.pushComponent(testRef, testee);
        }};
    }

    @Test
    public void test_that_addTo_invokes_addComponent()
    {
        testee.addTo(testRef);

        new Verifications() {{
            testComponentManager.addComponent(testRef, testee);
        }};
    }

    @Test
    public void test_that_getContext_Reteurns_the_Context_of_the_Component()
    {
        assertThat(testee.getContext()).isEqualTo(testContext);
    }

    @Test
    public void test_that_belongsTo_With_Context_Returns_true_if_the_Component_belongs_to_the_passed_Context()
    {
        assertThat(testee.belongsTo(testContext)).isTrue();
    }

    @Test
    public void test_that_belongsTo_With_Context_Returns_false_if_the_Component_does_not_belong_to_the_passed_Context()
    {
        assertThat(testee.belongsTo(otherTestContext)).isFalse();
    }

    @Test
    public void test_that_belongsTo_With_Context_Returns_false_if_the_passed_Context_is_null()
    {
        assertThat(testee.belongsTo((Context) null)).isFalse();
    }

    @Test
    public void test_that_belongsTo_With_Contextual_Returns_true_if_the_Component_belongs_to_the_same_Context_as_the_passed_Contextual()
    {
        new Expectations() {{
            testContextual.getContext(); result = testContext;
        }};

        assertThat(testee.belongsTo(testContextual)).isTrue();
    }

    @Test
    public void test_that_belongsTo_With_Contextual_Returns_false_if_the_Component_does_not_belong_to_the_same_Context_as_the_passed_Contextual()
    {
        new Expectations() {{
           testContextual.getContext(); result = otherTestContext;
        }};

        assertThat(testee.belongsTo(testContextual)).isFalse();
    }

    @Test
    public void test_that_belongsTo_With_Contextual_Returns_false_if_the_passed_Contextual_is_null()
    {
        assertThat(testee.belongsTo((Contextual) null)).isFalse();
    }

    private class TestImplementationOfAbstractComponent
    extends AbstractComponent
    {
        public TestImplementationOfAbstractComponent(final Context context)
        {
            super(context);
        }

        @Override
        public List<String> getPropertyNames()
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.getPropertyNames");
        }

        @Override
        public List<Class> getPropertyTypes()
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.getPropertyTypes");
        }

        @Override
        public Object get(String name)
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.get");
        }

        @Override
        public <T> T get(final ComponentProperty<T> property)
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.get");
        }

        @Override
        public boolean set(String name, Object value)
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.set");
        }

        @Override
        public <T> boolean set(final ComponentProperty<T> property, final T value)
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.set");
        }

        @Override
        public boolean has(String name)
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.has");
        }

        @Override
        public <T extends Component> T copy()
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.copy");
        }

        @Override
        public <T extends Component> T copy(T src)
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.copy");
        }

        @Override
        public Class<? extends Component> type()
        {
            throw new UnsupportedOperationException("TestImplementationOfAbstractComponent.type");
        }
    }
}