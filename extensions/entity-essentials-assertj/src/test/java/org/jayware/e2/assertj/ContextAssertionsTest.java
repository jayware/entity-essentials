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
package org.jayware.e2.assertj;

import mockit.Expectations;
import mockit.Mocked;
import org.assertj.core.api.Assertions;
import org.jayware.e2.context.api.Context;
import org.testng.annotations.Test;

import static org.jayware.e2.assertj.Common.ID;
import static org.jayware.e2.assertj.Common.ID2;
import static org.testng.Assert.fail;


public class ContextAssertionsTest
{
    private @Mocked Context testContext;

    @Test
    public void test_assertThat_does_not_return_null()
    throws Exception
    {
        Assertions.assertThat(ContextAssertions.assertThat(testContext)).isNotNull();
    }

    @Test
    public void test_that_isDisposed_fails_if_the_passed_Context_is_not_disposed()
    {
        new Expectations() {{
            testContext.getId(); result = ID;
            testContext.isDisposed(); result = false;
        }};

        try
        {
            ContextAssertions.assertThat(testContext).isDisposed();
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_isNotDisposed_fails_if_the_passed_Context_is_disposed()
    {
        new Expectations() {{
            testContext.getId(); result = ID;
            testContext.isDisposed(); result = true;
        }};

        try
        {
            ContextAssertions.assertThat(testContext).isNotDisposed();
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_hasId_with_String_fails_if_the_Context_does_not_have_the_expected_Id()
    {
        new Expectations() {{
            testContext.getId(); result = ID;
        }};

        try
        {
            ContextAssertions.assertThat(testContext).hasId(ID2.toString());
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_hasId_with_UUID_fails_if_the_Context_does_not_have_the_expected_UUID()
    {
        new Expectations() {{
            testContext.getId(); result = ID;
        }};

        try
        {
            ContextAssertions.assertThat(testContext).hasId(ID2);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }
}