/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2016 Elmar Schug <elmar.schug@jayware.org>,
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
package org.jayware.e2.component.api;

import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.jayware.e2.component.api.Aspect.EMPTY;
import static org.jayware.e2.component.api.Aspect.aspect;


public class AspectTest
{
    private @Mocked Context testContext;
    private @Mocked ComponentManager testComponentManager;
    private @Mocked EntityRef testRefA, testRefB;

    @BeforeMethod
    public void setUp()
    {
        new Expectations()
        {{
            testContext.getService(ComponentManager.class); result = testComponentManager; minTimes = 0;
        }};
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withAllOf_VarArg_throws_IllegalArgumentException_if_null_is_passed()
    {
        aspect().withAllOf((Class<? extends Component>[]) null);
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withOneOf_VarArg_throws_IllegalArgumentException_if_null_is_passed()
    {
        aspect().withOneOf((Class<? extends Component>[]) null);
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withNoneOf_VarArg_throws_IllegalArgumentException_if_null_is_passed()
    {
        aspect().withNoneOf((Class<? extends Component>[]) null);
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withAllOf_Collection_throws_IllegalArgumentException_if_null_is_passed()
    {
        aspect().withAllOf((Collection<Class<? extends Component>>) null);
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withOneOf_Collection_throws_IllegalArgumentException_if_null_is_passed()
    {
        aspect().withOneOf((Collection<Class<? extends Component>>) null);
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

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_withNoneOf_Collection_throws_IllegalArgumentException_if_null_is_passed()
    {
        aspect().withNoneOf((Collection<Class<? extends Component>>) null);
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
            testComponentManager.getComponentTypes(testRefA); result = asList(TestComponentA.class, TestComponentB.class, TestComponentC.class); minTimes = 0;
            testComponentManager.getComponentTypes(testRefB); result = asList(TestComponentD.class); minTimes = 0;
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

    public interface TestComponentA extends Component {}

    public interface TestComponentB extends Component {}

    public interface TestComponentC extends Component {}

    public interface TestComponentD extends Component {}

    public interface TestComponentE extends Component {}

    public interface TestComponentF extends Component {}
}