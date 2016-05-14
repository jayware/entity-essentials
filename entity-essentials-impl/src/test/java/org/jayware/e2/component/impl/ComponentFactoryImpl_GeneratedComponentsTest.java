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


import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.component.impl.TestComponents.MalformedCombinedTestComponent;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.component.impl.TestComponents.TestComponentAB;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.impl.TestComponents.TestComponentB;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class ComponentFactoryImpl_GeneratedComponentsTest
{
    @Mock private Context context;
    @Mock private ComponentManager componentManager;

    private ComponentFactoryImpl componentFactory;
    private EntityRef refA;

    @BeforeMethod
    public void setup()
    {
        initMocks(this);

        componentFactory = new ComponentFactoryImpl();

        when(context.getService(ComponentManager.class)).thenReturn(componentManager);
    }

    @Test
    public void test_pullFrom()
    {
        final TestComponentA component;
        componentFactory.prepareComponent(TestComponentA.class);
        component = componentFactory.createComponent(TestComponentA.class).newInstance(context);

        component.pullFrom(refA);

        verify(componentManager).pullComponent(refA, component);
    }

    @Test
    public void test_pushTo()
    {
        final TestComponentA component;
        componentFactory.prepareComponent(TestComponentA.class);
        component = componentFactory.createComponent(TestComponentA.class).newInstance(context);

        component.pushTo(refA);

        verify(componentManager).pushComponent(refA, component);
    }

    @Test
    public void test_combined_component()
    {
        final TestComponentAB component;

        componentFactory.prepareComponent(TestComponentAB.class);
        component = componentFactory.createComponent(TestComponentAB.class).newInstance(context);

        assertThat(TestComponentA.class.isAssignableFrom(component.getClass())).isTrue();
        assertThat(TestComponentB.class.isAssignableFrom(component.getClass())).isTrue();
    }

    @Test(expectedExceptions = MalformedComponentException.class)
    public void test_combined_component_Fails_if_the_component_extends_a_non_component_type()
    {
        componentFactory.prepareComponent(MalformedCombinedTestComponent.class);
    }
}
