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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.util.Parameter.parametersFrom;


public class ParameterTest
{
    private Parameter testParameter1;
    private Parameter testParameter2;
    private Parameter testParameter3;

    @BeforeEach
    public void setUp()
    throws Exception
    {
        final Method methodB = TestClass.class.getMethod("methodB", String.class, Object.class);

        testParameter1 = new Parameter(String.class);
        testParameter2 = new Parameter(int.class);
        testParameter3 = new Parameter(Object.class, methodB.getParameterAnnotations()[1]);
    }

    @Test
    public void test_parametersFrom_Returns_a_correct_List_of_parameters_for_a_Method_with_annotated_Parameters()
    throws Exception
    {
        final Method methodB = TestClass.class.getMethod("methodB", String.class, Object.class);

        assertThat(parametersFrom(methodB)).containsExactly(testParameter1, testParameter3);
    }

    @Test
    public void test_parametersFrom_Returns_a_correct_List_of_parameters_for_a_Method_with_two_Parameters()
    throws Exception
    {
        final Method methodA = TestClass.class.getMethod("methodA", String.class, int.class);

        assertThat(parametersFrom(methodA)).containsExactly(testParameter1, testParameter2);
    }

    @Test
    public void test_parametersFrom_Returns_a_empty_List_for_a_Method_with_no_Parameters()
    throws Exception
    {
        final Method methodA = TestClass.class.getMethod("methodC");

        assertThat(parametersFrom(methodA)).isEmpty();
    }

    private static class TestClass
    {
        public void methodA(String s, int i)
        {

        }

        public void methodB(String s, @Deprecated Object o)
        {

        }

        public void methodC()
        {

        }
    }
}