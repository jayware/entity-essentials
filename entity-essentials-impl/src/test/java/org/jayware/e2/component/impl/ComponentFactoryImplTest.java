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


import org.jayware.e2.component.api.ComponentFactoryException;
import org.jayware.e2.component.api.ComponentInstancer;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.context.api.Context;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;


public class ComponentFactoryImplTest
{
    @Mock private Context context;
    @Mock private ComponentManager componentManager;

    ComponentFactoryImpl testee;

    @BeforeMethod
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        testee = new ComponentFactoryImpl();

        when(context.getComponentManager()).thenReturn(componentManager);
    }

    @Test
    public void testPrepare()
    {
        testee.prepareComponent(TestComponentA.class);
    }

    @Test
    public void testCreate()
    {
        testee.prepareComponent(TestComponentA.class);
        final ComponentInstancer<TestComponentA> component = testee.createComponent(TestComponentA.class);

        assertThat(component).isNotNull();
    }

    @Test
    public void testCreateComponentFromClassWithNullClass()
    {
        try
        {
            testee.createComponent((Class) null);
            fail("IllegalArgumentException expected!");
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testCreateComponentFromClassFailsIfComponentIsNotPrepared()
    {
        try
        {
            testee.createComponent(TestComponentA.class);
            fail("ComponentFactoryException expected!");
        }
        catch (ComponentFactoryException e)
        {

        }
    }

    @Test(expectedExceptions = MalformedComponentException.class)
    public void testPrepareComponentShouldFailWhenParameterTypesDoNotMatch()
    {
        testee.prepareComponent(TestComponents.TestComponentWithParameterTypeMismatch.class);
    }
}
