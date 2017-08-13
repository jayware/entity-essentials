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
import org.jayware.e2.component.api.generation.analyse.ComponentHierarchyAnalyser;
import org.jayware.e2.component.impl.TestComponents.MalformedCombinedTestComponent;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.component.impl.TestComponents.TestComponentACB;
import org.jayware.e2.component.impl.TestComponents.TestComponentB;
import org.jayware.e2.component.impl.TestComponents.TestComponentCB;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class ComponentHierarchyAnalyserImplTest
{
    private ComponentHierarchyAnalyser testee;

    @BeforeMethod
    public void setup()
    {
        testee = new ComponentHierarchyAnalyserImpl();
    }

    @Test
    public void should_return_a_Set_containing_all_component_interfaces()
    {
        final Set<Class<? extends Component>> result = testee.analyse(TestComponentACB.class);

        assertThat(result).containsExactlyInAnyOrder(TestComponentA.class, TestComponentB.class, TestComponentCB.class, TestComponentACB.class);
    }

    @Test
    public void should_throw_a_MalformedComponentException_if_one_of_the_interfaces_does_not_extend_the_Component_interface()
    {
        try
        {
            testee.analyse(MalformedCombinedTestComponent.class);
            fail("Expected a MalformedCombinedTestComponent");
        }
        catch (MalformedComponentException ignored)
        {

        }
    }
}