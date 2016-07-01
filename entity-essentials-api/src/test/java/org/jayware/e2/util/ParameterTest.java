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
package org.jayware.e2.util;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.util.Parameter.parametersFrom;


public class ParameterTest
{
    private Parameter testParameter1;
    private Parameter testParameter2;
    private Parameter testParameter3;

    @BeforeMethod
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