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
package org.jayware.e2.entity.api;


import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityEvent.EntityDeletingEvent;
import org.jayware.e2.event.api.Event;
import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEventRefFilter.filterEntities;


public class EntityEventRefFilterTest
{
    private @Mocked Context testContext;
    private @Mocked Event testEvent;
    private @Mocked EntityRef testRefA, testRefB, testRefC, testRefD;

    private EntityEventRefFilter testee;

    @Test
    public void test_That_the_Filter_accepts_an_EntityRef_which_is_contained_in_the_default_include_set()
    {
        new Expectations()
        {{
            testEvent.getType(); result = EntityEvent.class;
            testEvent.getParameter(EntityRefParam); result = testRefA;
        }};

        testee = filterEntities(testRefA);

        assertThat(testee.accepts(testContext, testEvent))
            .withFailMessage("The filter does not accept the expected EntityRef!")
            .isTrue();
    }

    @Test
    public void test_That_the_Filter_accepts_an_EntityRef_which_is_contained_in_include_set()
    {
        new Expectations()
        {{
            testEvent.getType(); result = EntityEvent.class;
            testEvent.getParameter(EntityRefParam); result = testRefB;
        }};

        testee = filterEntities().include(testRefB);

        assertThat(testee.accepts(testContext, testEvent))
            .withFailMessage("The filter does not accept the expected EntityRef!")
            .isTrue();
    }

    @Test
    public void test_That_the_Filter_does_not_accept_an_EntityRef_which_is_contained_in_the_exclude_set()
    {
        new Expectations()
        {{
            testEvent.getType(); result = EntityEvent.class;
            testEvent.getParameter(EntityRefParam); result = testRefA;
        }};

        testee = filterEntities().exclude(testRefA);

        assertThat(testee.accepts(testContext, testEvent))
            .withFailMessage("The filter accepts an EntityRef which is contained in the exclude-set!")
            .isFalse();
    }

    @Test
    public void test_That_the_Filter_does_not_accept_an_EntityRef_which_is_contained_in_both_the_include_set_and_the_exclude_set()
    {
        new Expectations()
        {{
            testEvent.getType(); result = EntityEvent.class;
            testEvent.getParameter(EntityRefParam); result = testRefC;
        }};

        testee = filterEntities().include(testRefA).exclude(testRefC);

        assertThat(testee.accepts(testContext, testEvent))
            .withFailMessage("The filter accepts an EntityRef which is contained in the include- and the exclude-set!")
            .isFalse();
    }

    @Test
    public void test_That_the_Filter_can_handle_sub_event_types()
    {
        new Expectations()
        {{
            testEvent.getType(); result = EntityDeletingEvent.class;
            testEvent.getParameter(EntityRefParam); result = testRefD;
        }};

        testee = filterEntities(testRefD);

        assertThat(testee.accepts(testContext, testEvent))
            .withFailMessage("The filter does not accept the expected EntityRef!")
            .isTrue();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_include_With_VarArg_Throws_IllegalArgumentException_if_null_is_passed_in()
    {
        testee = filterEntities().include((EntityRef[]) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_exclude_With_VarArg_Throws_IllegalArgumentException_if_null_is_passed_in()
    {
        testee = filterEntities().exclude((EntityRef[]) null);
    }
}