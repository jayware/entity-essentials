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
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        componentFactory.prepareComponent(TestComponentA.class, TestComponentB.class);

        when(context.getComponentManager()).thenReturn(componentManager);
    }

    @Test
    public void testPullFrom()
    {
        final TestComponentA component = componentFactory.createComponent(TestComponentA.class).newInstance(context);

        component.pullFrom(refA);

        verify(componentManager).pullComponent(refA, component);
    }

    @Test
    public void testPushTo()
    {
        final TestComponentA component = componentFactory.createComponent(TestComponentA.class).newInstance(context);

        component.pushTo(refA);

        verify(componentManager).pushComponent(refA, component);
    }
}
