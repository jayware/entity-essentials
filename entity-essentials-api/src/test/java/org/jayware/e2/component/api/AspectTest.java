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
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.Aspect.ANY;
import static org.jayware.e2.component.api.Aspect.EMPTY;
import static org.jayware.e2.component.api.Aspect.NONE;
import static org.jayware.e2.component.api.Aspect.aspect;


public class AspectTest
{
    private @Mocked Context testContext;
    private @Mocked ComponentManager testComponentManager;
    private @Mocked EntityRef testRefA, testRefB;

    @Test
    public void test_that_two_Aspects_are_equal_if_both_contain_the_same_set_of_Components()
    {
        final Aspect aspectA = aspect(TestComponentA.class, TestComponentB.class);
        final Aspect aspectB = aspect(TestComponentA.class, TestComponentB.class);
        final Aspect aspectC = aspect(TestComponentA.class, TestComponentB.class, TestComponentC.class);

        assertThat(aspectA).isEqualTo(aspectB);
        assertThat(aspect()).isEqualTo(aspect());
        assertThat(aspectA).isNotEqualTo(aspectC);
        assertThat(aspectA).isNotEqualTo(aspect());
    }

    @Test
    public void test_that_two_Aspects_return_the_same_hashcode_if_both_contain_the_same_set_of_Components()
    {
        final Aspect aspectA = aspect(TestComponentA.class, TestComponentB.class);
        final Aspect aspectB = aspect(TestComponentA.class, TestComponentB.class);
        final Aspect aspectC = aspect(TestComponentA.class, TestComponentB.class, TestComponentC.class);

        assertThat(aspectA.hashCode()).isEqualTo(aspectB.hashCode());
        assertThat(aspect().hashCode()).isEqualTo(aspect().hashCode());
        assertThat(aspectA.hashCode()).isNotEqualTo(aspectC.hashCode());
        assertThat(aspectA.hashCode()).isNotEqualTo(aspect().hashCode());
    }

    @Test
    public void test_Aspect_ANY_Matches_every_EntityRef()
    {
        assertThat(ANY.matches(testRefA)).isTrue();
    }

    @Test
    public void test_Aspect_ANY_Satisfies_every_Aspect()
    {
        assertThat(ANY.satisfies(aspect())).isTrue();
    }

    @Test
    public void test_Aspect_ANY_Contains_every_Component()
    {
        assertThat(ANY.contains(TestComponentA.class)).isTrue();
    }

    @Test
    public void test_Aspect_EMPTY_Matches_only_an_EntityRef_if_the_referenced_Entity_does_not_have_any_Component()
    {
        new Expectations()
        {{
            testContext.getService(ComponentManager.class); result = testComponentManager;
            testComponentManager.numberOfComponents(testRefA); result = 0;
            testComponentManager.numberOfComponents(testRefB); result = 42;
        }};

        assertThat(EMPTY.matches(testRefA)).isTrue();
        assertThat(EMPTY.matches(testRefB)).isFalse();
    }

    @Test
    public void test_Aspect_EMPTY_Satisfies_no_Aspect()
    {
        assertThat(EMPTY.satisfies(aspect())).isFalse();
    }

    @Test
    public void test_Aspect_EMPTY_Contains_no_Component()
    {
        assertThat(EMPTY.contains(TestComponentA.class)).isFalse();
    }

    @Test
    public void test_Aspect_NONE_Matches_never()
    {
        assertThat(NONE.matches(testRefA)).isFalse();
    }

    @Test
    public void test_Aspect_NONE_Satisfies_never()
    {
        assertThat(NONE.satisfies(aspect())).isFalse();
    }

    @Test
    public void test_Aspect_NONE_Contains_no_Component()
    {
        assertThat(NONE.contains(TestComponentA.class)).isFalse();
    }

    public interface TestComponentA
    extends Component
    {

    }

    public interface TestComponentB
    extends Component
    {

    }

    public interface TestComponentC
    extends Component
    {

    }
}