/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
 *                    Markus Neubauer <markus.neubauer@jayware.org>
 *
 *     This file is part of Entity Essentials.
 *
 *     Entity Essentials is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     Entity Essentials is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jayware.e2.component.impl;


import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.component.impl.TestComponents.MalformedCombinedTestComponent;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.component.impl.TestComponents.TestComponentAB;
import org.jayware.e2.component.impl.TestComponents.TestComponentB;
import org.jayware.e2.component.impl.TestComponents.TestComponentC;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentFactoryImpl_GeneratedComponentsTest
{
    @Mocked private Context testContext;
    @Mocked private ComponentManager componentManager;

    private ComponentFactoryImpl componentFactory;
    private EntityRef refA;

    @BeforeMethod
    public void setup()
    {
        componentFactory = new ComponentFactoryImpl();

        componentFactory.prepareComponent(TestComponentA.class);
        componentFactory.prepareComponent(TestComponentC.class);
        componentFactory.prepareComponent(TestComponentAB.class);
    }

    @Test
    public void test_pullFrom()
    {
        final TestComponentA component;

        new Expectations()
        {{
            testContext.getService(ComponentManager.class); result = componentManager;
        }};

        component = componentFactory.createComponent(TestComponentA.class).newInstance(testContext);

        component.pullFrom(refA);

        new Verifications()
        {{
            componentManager.pullComponent(refA, component);
        }};
    }

    @Test
    public void test_pushTo()
    {
        final TestComponentA component;

        new Expectations()
        {{
            testContext.getService(ComponentManager.class); result = componentManager;
        }};

        component = componentFactory.createComponent(TestComponentA.class).newInstance(testContext);

        component.pushTo(refA);

        new Verifications()
        {{
            componentManager.pushComponent(refA, component);
        }};
    }

    @Test
    public void test_addTo()
    {
        final TestComponentA component;

        new Expectations()
        {{
            testContext.getService(ComponentManager.class); result = componentManager;
        }};

        component = componentFactory.createComponent(TestComponentA.class).newInstance(testContext);

        component.addTo(refA);

        new Verifications()
        {{
            componentManager.addComponent(refA, component);
        }};
    }

    @Test
    public void test_combined_component()
    {
        final TestComponentAB component;

        component = componentFactory.createComponent(TestComponentAB.class).newInstance(testContext);

        assertThat(TestComponentA.class.isAssignableFrom(component.getClass())).isTrue();
        assertThat(TestComponentB.class.isAssignableFrom(component.getClass())).isTrue();
    }

    @Test(expectedExceptions = MalformedComponentException.class)
    public void test_combined_component_Fails_if_the_component_extends_a_non_component_type()
    {
        componentFactory.prepareComponent(MalformedCombinedTestComponent.class);
    }

    @Test
    public void test_set_Does_Not_set_an_unknown_property_and_returns_false()
    throws Exception
    {
        final AbstractComponent component = (AbstractComponent) componentFactory.createComponent(TestComponentC.class).newInstance(testContext);

        assertThat(component.set("xyz", "fubar")).isFalse();
    }

    @Test
    public void test_set_Accepts_null_value_for_primitive_properties_and_returns_true()
    {
        final AbstractComponent component = (AbstractComponent) componentFactory.createComponent(TestComponentC.class).newInstance(testContext);

        assertThat(component.set("primitiveBoolean", null)).isTrue();
        assertThat(component.set("primitiveByte", null)).isTrue();
        assertThat(component.set("primitiveShort", null)).isTrue();
        assertThat(component.set("primitiveInteger", null)).isTrue();
        assertThat(component.set("primitiveLong", null)).isTrue();
        assertThat(component.set("primitiveFloat", null)).isTrue();
        assertThat(component.set("primitiveDouble", null)).isTrue();
    }

    @Test
    public void test_set_Accepts_boxed_value_for_primitive_properties_and_returns_true()
    {
        final AbstractComponent component = (AbstractComponent) componentFactory.createComponent(TestComponentC.class).newInstance(testContext);

        assertThat(component.set("primitiveBoolean", new Boolean(true))).isTrue();
        assertThat(component.set("primitiveByte", new Byte((byte) 42))).isTrue();
        assertThat(component.set("primitiveShort", new Short((short) 73))).isTrue();
        assertThat(component.set("primitiveInteger", new Integer(1337))).isTrue();
        assertThat(component.set("primitiveLong", new Long(4711L))).isTrue();
        assertThat(component.set("primitiveFloat", new Float(42.73))).isTrue();
        assertThat(component.set("primitiveDouble", new Double(13.37))).isTrue();
    }
}
