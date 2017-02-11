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
package org.jayware.e2.util;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.util.Key.createKey;


public class KeyTest
{
    @Test
    public void test_that_a_key_is_equals_to_itself()
    {
        final Key<Object> key = createKey("fubar");
        assertThat(key).isEqualTo(key);
    }

    @Test
    public void test_that_a_key_is_not_equals_to_an_object_which_is_not_an_instance_of_Key()
    {
        assertThat(createKey("fubar")).isNotEqualTo(new Object());
    }

    @Test
    public void test_that_two_Keys_are_equals_if_they_have_the_same_key_string()
    {
        assertThat(createKey("fubar")).isEqualTo(createKey("fubar"));
    }

    @Test
    public void test_that_two_Keys_are_not_equals_if_they_have_different_key_strings()
    {
        assertThat(createKey("foo")).isNotEqualTo(createKey("bar"));
    }

    @Test
    public void test_that_two_Keys_with_the_same_key_string_have_the_same_hashcode()
    {
        assertThat(createKey("4711").hashCode()).isEqualTo(createKey("4711").hashCode());
    }

    @Test
    public void test_that_two_Keys_with_different_key_string_have_different_hashcode()
    {
        assertThat(createKey("42").hashCode()).isNotEqualTo(createKey("4711").hashCode());
    }

    @Test
    public void test_toString_contains_at_least_the_passed_key_string()
    {
        assertThat(createKey("fubar").toString()).contains("fubar");
    }
}