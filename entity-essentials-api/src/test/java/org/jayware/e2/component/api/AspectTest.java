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
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.jayware.e2.component.api.Aspect.EMPTY;
import static org.jayware.e2.component.api.Aspect.aspect;
import static org.jayware.e2.component.api.Aspect.collectNames;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class AspectTest
{
    private @Mocked Context testContext;
    private @Mocked ComponentManager testComponentManager;
    private @Mocked EntityRef testRefA, testRefB;

    @BeforeEach
    public void setUp()
    {
        new Expectations()
        {{
            testContext.getService(ComponentManager.class); result = testComponentManager; minTimes = 0;
        }};
    }

    @Test
    public void test_withAllOf_VarArg_throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                aspect().withAllOf((Class<? extends Component>[]) null);
            }
        });
    }

    @Test
    public void test_withAllOf_VarArg_throws_IllegalAspectException_if_a_passed_Components_are_already_part_of_the_aspect_but_in_another_set()
    {
        try
        {
            aspect().withNoneOf(TestComponentA.class).withAllOf(TestComponentA.class);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the difference set!");
        }
        catch (IllegalAspectException e) {}

        try
        {
            aspect().withOneOf(TestComponentA.class).withAllOf(TestComponentA.class);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the unification set!");
        }
        catch (IllegalAspectException e) {}
    }

    @Test
    public void test_withOneOf_VarArg_throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                aspect().withOneOf((Class<? extends Component>[]) null);
            }
        });
    }

    @Test
    public void test_withOneOf_VarArg_throws_IllegalAspectException_if_a_passed_Components_are_already_part_of_the_aspect_but_in_another_set()
    {
        try
        {
            aspect().withAllOf(TestComponentA.class).withOneOf(TestComponentA.class);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the intersection set!");
        }
        catch (IllegalAspectException e) {}

        try
        {
            aspect().withNoneOf(TestComponentA.class).withOneOf(TestComponentA.class);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the difference set!");
        }
        catch (IllegalAspectException e) {}
    }

    @Test
    public void test_withNoneOf_VarArg_throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                aspect().withNoneOf((Class<? extends Component>[]) null);
            }
        });
    }

    @Test
    public void test_withNoneOf_VarArg_throws_IllegalAspectException_if_a_passed_Components_are_already_part_of_the_aspect_but_in_another_set()
    {
        try
        {
            aspect().withAllOf(TestComponentA.class).withNoneOf(TestComponentA.class);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the intersection set!");
        }
        catch (IllegalAspectException e) {}

        try
        {
            aspect().withOneOf(TestComponentA.class).withNoneOf(TestComponentA.class);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the unification set!");
        }
        catch (IllegalAspectException e) {}
    }

    @Test
    public void test_withAllOf_Collection_throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                aspect().withAllOf((Collection<Class<? extends Component>>) null);
            }
        });
    }

    @Test
    public void test_withAllOf_Collection_throws_IllegalAspectException_if_a_passed_Components_are_already_part_of_the_aspect_but_in_another_set()
    {
        final Collection<Class<? extends Component>> components = asList(TestComponentA.class, TestComponentB.class);

        try
        {
            aspect().withNoneOf(TestComponentA.class).withAllOf(components);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the difference set!");
        }
        catch (IllegalAspectException e) {}

        try
        {
            aspect().withOneOf(TestComponentA.class).withAllOf(components);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the unification set!");
        }
        catch (IllegalAspectException e) {}
    }

    @Test
    public void test_withOneOf_Collection_throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                aspect().withOneOf((Collection<Class<? extends Component>>) null);
            }
        });
    }

    @Test
    public void test_withOneOf_Collection_throws_IllegalAspectException_if_a_passed_Components_are_already_part_of_the_aspect_but_in_another_set()
    {
        final Collection<Class<? extends Component>> components = asList(TestComponentA.class, TestComponentB.class);

        try
        {
            aspect().withNoneOf(TestComponentA.class).withAllOf(components);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the difference set!");
        }
        catch (IllegalAspectException e) {}

        try
        {
            aspect().withOneOf(TestComponentA.class).withAllOf(components);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the unification set!");
        }
        catch (IllegalAspectException e) {}
    }

    @Test
    public void test_withNoneOf_Collection_throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                aspect().withNoneOf((Collection<Class<? extends Component>>) null);
            }
        });
    }

    @Test
    public void test_withNoneOf_Collection_throws_IllegalAspectException_if_a_passed_Components_are_already_part_of_the_aspect_but_in_another_set()
    {
        final Collection<Class<? extends Component>> components = asList(TestComponentA.class, TestComponentB.class);

        try
        {
            aspect().withAllOf(TestComponentA.class).withNoneOf(components);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the intersection set!");
        }
        catch (IllegalAspectException e) {}

        try
        {
            aspect().withOneOf(TestComponentA.class).withNoneOf(components);
            fail("Expected an IllegalAspectException because TestComponentA is already part of the unification set!");
        }
        catch (IllegalAspectException e) {}
    }

    @Test
    public void test_that_an_empty_Aspect_matches_an_entity_with_no_components()
    {
        final Aspect aspect = aspect();

        assertThat(aspect.matches(testRefA)).isTrue();
    }

    @Test
    public void test_matches_()
    {
        new Expectations()
        {{
            testComponentManager.getComponentTypes(testRefA); result = asList(TestComponentA.class, TestComponentB.class, TestComponentC.class);
            testComponentManager.getComponentTypes(testRefB); result = asList(TestComponentD.class);
        }};

        assertThat(aspect().withAllOf(TestComponentA.class, TestComponentB.class).matches(testRefA)).isTrue();
        assertThat(aspect().withAllOf(TestComponentA.class, TestComponentB.class).matches(testRefB)).isFalse();
        assertThat(aspect().withAllOf(TestComponentA.class, TestComponentB.class).withNoneOf(TestComponentD.class).matches(testRefA)).isTrue();
        assertThat(aspect().withOneOf(TestComponentA.class, TestComponentB.class, TestComponentC.class).matches(testRefA)).isTrue();
        assertThat(aspect().withOneOf(TestComponentA.class, TestComponentB.class, TestComponentC.class).matches(testRefB)).isFalse();
        assertThat(aspect().withAllOf(TestComponentA.class, TestComponentB.class).withOneOf(TestComponentC.class, TestComponentD.class).matches(testRefA)).isTrue();
        assertThat(aspect().withNoneOf(TestComponentA.class, TestComponentB.class).matches(testRefA)).isFalse();
        assertThat(aspect().withNoneOf(TestComponentA.class, TestComponentB.class).matches(testRefB)).isTrue();
        assertThat(aspect().withOneOf(TestComponentA.class, TestComponentD.class).matches(testRefA)).isTrue();
        assertThat(aspect().withOneOf(TestComponentA.class, TestComponentD.class).matches(testRefB)).isTrue();
    }

    @Test
    public void test_aspect_Returns_an_empty_Aspect()
    {
        assertThat(aspect()).isEqualTo(EMPTY);
    }

    @Test
    public void test_that_getIntersectionSet_Returns_a_Set_containing_the_expected_Component_classes()
    {
        assertThat(aspect().getIntersectionSet()).isNotNull().isEmpty();

        assertThat(aspect().withAllOf(TestComponentA.class, TestComponentB.class).getIntersectionSet()).containsExactlyInAnyOrder(TestComponentA.class, TestComponentB.class);
    }

    @Test
    public void test_getUnificationSet_Returns_a_Set_containing_the_expected_Component_classes()
    {
        assertThat(aspect().getUnificationSet()).isNotNull().isEmpty();

        assertThat(aspect().withOneOf(TestComponentA.class, TestComponentB.class).getUnificationSet()).containsExactlyInAnyOrder(TestComponentA.class, TestComponentB.class);
    }

    @Test
    public void test_getDifferenceSet_Returns_a_Set_containing_the_expected_Component_classes()
    {
        assertThat(aspect().getDifferenceSet()).isNotNull().isEmpty();

        assertThat(aspect().withNoneOf(TestComponentA.class, TestComponentB.class).getDifferenceSet()).containsExactlyInAnyOrder(TestComponentA.class, TestComponentB.class);
    }

    @Test
    public void test_that_and_combines_two_Aspects()
    {
        final Aspect a = aspect().withAllOf(TestComponentA.class).withOneOf(TestComponentB.class).withNoneOf(TestComponentC.class);
        final Aspect b = aspect().withAllOf(TestComponentD.class).withOneOf(TestComponentE.class).withNoneOf(TestComponentF.class);
        final Aspect testee = a.and(b);

        assertThat(testee).isNotNull();
        assertThat(testee.getIntersectionSet()).containsExactlyInAnyOrder(TestComponentA.class, TestComponentD.class);
        assertThat(testee.getUnificationSet()).containsExactlyInAnyOrder(TestComponentB.class, TestComponentE.class);
        assertThat(testee.getDifferenceSet()).containsExactlyInAnyOrder(TestComponentC.class, TestComponentF.class);
    }

    @Test
    public void test_that_equals_returns_false_if_the_passed_object_is_not_an_instance_of_Aspect()
    {
        assertThat(aspect().equals(new Object())).isFalse();
    }

    @Test
    public void test_equlas_and_hashcode()
    {
        final Aspect
            a1 = aspect(),
            b1 = aspect(),
            a2 = aspect().withAllOf(TestComponentA.class),
            b2 = aspect().withAllOf(TestComponentA.class),
            a3 = aspect().withAllOf(TestComponentA.class).withOneOf(TestComponentC.class),
            b3 = aspect().withAllOf(TestComponentA.class).withOneOf(TestComponentC.class),
            a4 = aspect().withAllOf(TestComponentA.class).withOneOf(TestComponentB.class).withNoneOf(TestComponentC.class),
            b4 = aspect().withAllOf(TestComponentA.class).withOneOf(TestComponentB.class).withNoneOf(TestComponentC.class);

        assertThat(a1).isEqualTo(b1);
        assertThat(a1).isNotEqualTo(b4);
        assertThat(a1.hashCode()).isEqualTo(b1.hashCode());

        assertThat(a2).isEqualTo(b2);
        assertThat(a2).isNotEqualTo(b3);
        assertThat(a2.hashCode()).isEqualTo(b2.hashCode());

        assertThat(a3).isEqualTo(b3);
        assertThat(a3).isNotEqualTo(b2);
        assertThat(a3.hashCode()).isEqualTo(b3.hashCode());

        assertThat(a4).isEqualTo(b4);
        assertThat(a4).isNotEqualTo(b1);
        assertThat(a4.hashCode()).isEqualTo(b4.hashCode());
    }

    @Test
    public void test_that_collectNames_Returns_the_expected_List_of_Component_names()
    {
        List<Class<? extends Component>> components = Arrays.<Class<? extends Component>>asList(TestComponentA.class, TestComponentB.class);

        assertThat(collectNames(components)).containsExactly("TestComponentA", "TestComponentB");
    }

    public interface TestComponentA extends Component {}

    public interface TestComponentB extends Component {}

    public interface TestComponentC extends Component {}

    public interface TestComponentD extends Component {}

    public interface TestComponentE extends Component {}

    public interface TestComponentF extends Component {}
}