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


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ArrayUtilTest
{
    @Test
    public void test_that_newArray_returns_an_array_of_the_expected_type_and_length()
    {
        final Long[] actualArray = ArrayUtil.newArray(new Long[4], 2);

        assertThat(actualArray).hasSize(2);
        assertThat(actualArray.getClass().getComponentType()).isEqualTo(Long.class);
    }

    @Test
    public void test_that_append_adds_the_passed_element_at_the_end_of_the_array()
    {
        final String[] actualArray = ArrayUtil.append("42", new String[73]);

        assertThat(actualArray).hasSize(74);
        assertThat(actualArray[actualArray.length - 1]).isEqualTo("42");
    }

    @Test
    public void test_that_prepend_adds_the_passed_element_at_the_beginning_of_the_array()
    {
        final String[] actualArray = ArrayUtil.prepend("42", new String[73]);

        assertThat(actualArray).hasSize(74);
        assertThat(actualArray[0]).isEqualTo("42");
    }

}