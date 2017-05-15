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
package org.jayware.e2.event.api;

import org.jayware.e2.event.api.Parameters.Parameter;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.Parameters.param;


public class ParametersTest
{
    @Test
    public void test_that_the_copy_constructor_works()
    {
        final Parameters testee = new Parameters();
        testee.set("foo", "bar");
        testee.set("muh", "kuh");

        assertThat(new Parameters(testee)).containsExactlyInAnyOrder(param("foo", "bar"), param("muh", "kuh"));
    }

    @Test
    public void test_that_an_array_of_parameters_can_be_passed()
    {
        final Parameter[] expectedParameters = {param("foo", 42), param("muh", 73)};
        final Parameters testee = new Parameters(expectedParameters);

        assertThat(testee).containsExactlyInAnyOrder(expectedParameters);
    }

    @Test
    public void test_that_param_creates_a_Parameter_with_the_expected_name_and_value()
    {
        final Parameter parameter = param("foo", "bar");

        assertThat(parameter.getName()).isEqualTo("foo");
        assertThat(parameter.getValue()).isEqualTo("bar");
    }

    @Test
    public void test_that_clear_removes_all_parameters()
    {
        final Parameters testee = new Parameters();
        testee.set("foo", "bar");
        testee.set("muh", "kuh");

        testee.clear();

        assertThat(testee).isEmpty();
    }

    @Test
    public void test_that_get_returns_the_expected_parameter()
    {
        final Parameters testee = new Parameters();
        testee.set("foo", "bar");
        testee.set("bar", 42);

        assertThat(testee.get("foo")).isEqualTo("bar");
        assertThat(testee.get("bar")).isEqualTo(42);
        assertThat(testee.get("fubar")).isNull();
    }

    @Test
    public void test_that_contains_returns_true_for_existing_parameters()
    {
        final Parameters testee = new Parameters();
        testee.set("foo", 73);
        testee.set("bar", null);

        assertThat(testee.contains("foo")).isTrue();
        assertThat(testee.contains("bar")).isTrue();
        assertThat(testee.contains("fubar")).isFalse();
    }

    @Test
    public void test_that_two_Parameters_are_equals_if_their_names_and_values_are_equals()
    {
        assertThat(param("fubar", 42).equals(param("fubar", 73)))
            .withFailMessage("Expected that the parameters are not equal, because their values are different!")
            .isFalse();

        assertThat(param("foo", "bar").equals(param("bar", "bar")))
            .withFailMessage("Expected that the parameters are not equal, because their names are different!")
            .isFalse();

        assertThat(param("foo", 42).equals(param("bar", 73)))
            .withFailMessage("Expected that the parameters are not equal, because their names and values are different!")
            .isFalse();

        assertThat(param("foo", "bar").equals(param("foo", "bar")))
            .withFailMessage("Expected that the parameters are equal!")
            .isTrue();

        final Parameter parameter = param("foo", "bar");
        assertThat(parameter.equals(parameter))
            .withFailMessage("Expected that a parameter is equals to it self!")
            .isTrue();
    }

    @Test
    public void test_that_two_Parameters_have_the_same_hash_code_if_they_have_the_same_name()
    {
        assertThat(param("foo", "bar").hashCode())
            .withFailMessage("Expected that the parameters do not have the same hash code, because their names are different!")
            .isNotEqualTo(param("bar", "bar").hashCode());

        assertThat(param("foo", 42).hashCode())
            .withFailMessage("Expected that the parameters do not have the same hash code, because their names are different!")
            .isNotEqualTo(param("bar", 73).hashCode());

        assertThat(param("fubar", 42).hashCode())
            .withFailMessage("Expected that the parameters do have the same hash code, because they have the same name!")
            .isEqualTo(param("fubar", 73).hashCode());

        assertThat(param("foo", "bar").hashCode())
            .withFailMessage("Expected that the parameters do have the same hash code, because they have the same name!")
            .isEqualTo(param("foo", "bar").hashCode());
    }

    @Test
    public void test_that_toString_returns_an_String_containing_the_name_and_value_of_a_parameter()
    {
        assertThat(param("fubar", 42).toString()).contains(asList("name='fubar'", "value=42"));
    }
}