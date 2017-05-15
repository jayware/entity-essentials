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
package org.jayware.e2.assertj;

import mockit.Expectations;
import mockit.Mocked;
import org.assertj.core.api.Assertions;
import org.jayware.e2.context.api.Context;
import org.junit.jupiter.api.Test;

import static org.jayware.e2.assertj.Common.ID;
import static org.jayware.e2.assertj.Common.ID2;
import static org.junit.jupiter.api.Assertions.fail;


public class ContextAssertionsTest
{
    private @Mocked Context testContext;

    @Test
    public void test_assertThat_does_not_return_null()
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