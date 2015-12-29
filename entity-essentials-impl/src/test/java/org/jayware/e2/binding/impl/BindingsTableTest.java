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
package org.jayware.e2.binding.impl;


import org.jayware.e2.binding.api.BindingRule;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class BindingsTableTest
{
    private BindingsTable testee;

    @Mock private Context testContext;

    @Mock private EventManager testEventManager;
    @Mock private ComponentManager testComponentManager;

    @Mock private EntityRef testRefSrcA;
    @Mock private EntityRef testRefTarA;

    @Mock private TestComponentA testComponentA1;
    @Mock private TestComponentA testComponentA2;

    @Mock private BindingRule testRule;

    private ComponentBindingImpl testBindingA;

    @BeforeMethod
    public void setup()
    {
        initMocks(this);

        when(testContext.getEventManager()).thenReturn(testEventManager);
        when(testContext.getComponentManager()).thenReturn(testComponentManager);

        when(testComponentManager.findComponent(testRefSrcA, TestComponentA.class)).thenReturn(testComponentA1);
        when(testComponentManager.findComponent(testRefTarA, TestComponentA.class)).thenReturn(testComponentA2);

        when(testComponentA1.type()).thenReturn((Class) TestComponentA.class);
        when(testComponentA2.type()).thenReturn((Class) TestComponentA.class);

        testBindingA = new ComponentBindingImpl(testRefSrcA, TestComponentA.class, testRefTarA, TestComponentA.class, testRule);

        testee = new BindingsTable(testContext);
    }

    @Test
    public void testAddBinding()
    {
        testee.addBinding(testBindingA);
        assertThat(testee.bindings()).containsOnly(testBindingA);

        testee.addBinding(testBindingA);
        assertThat(testee.bindings()).containsOnly(testBindingA);
    }

    @Test
    public void testRemoveBinding()
    {
        testee.addBinding(testBindingA);
        testee.removeBinding(testBindingA);
        assertThat(testee.bindings()).isEmpty();

        testee.removeBinding(testBindingA);
    }

    @Test
    public void testDispose()
    {
        testee.addBinding(testBindingA);
        testee.dispose(testContext);

        assertThat(testee.bindings()).isEmpty();

        testee.dispose(testContext);
    }
}
