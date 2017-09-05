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


public class ObjectUtilTest
{
    @Test
    public void test_that_getClassNameOf_Returns_the_name_of_the_class_of_the_passed_object()
    {
        assertThat(ObjectUtil.getClassNameOf(new Object())).isEqualTo(Object.class.getName());
    }
}