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
import org.jayware.e2.context.api.Contextual;
import org.testng.annotations.Test;

import static org.jayware.e2.assertj.Common.ID;
import static org.testng.Assert.fail;


public class ContextualAssertionsTest
{
    private @Mocked Context testContext;
    private @Mocked Contextual testContextual;
    private @Mocked Contextual testContextual2;

    @Test
    public void test_assertThat_does_not_return_null()
    throws Exception
    {
        Assertions.assertThat(ContextAssertions.assertThat(testContext)).isNotNull();
    }

    @Test
    public void test_that_belongsTo_fails_if_the_Contextual_does_not_belong_to_the_passed_Context()
    {
        new Expectations() {{
            testContext.getId(); result = ID;
            testContextual.belongsTo(testContext); result = false;
        }};

        try
        {
            ContextualAssertions.assertThat(testContextual).belongsTo(testContext);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_belongsTo_fails_if_the_Contextual_does_not_belong_to_the_same_Context_as_the_passed_Contextual()
    {
        new Expectations() {{
            testContextual.belongsTo(testContextual2); result = false;
        }};

        try
        {
            ContextualAssertions.assertThat(testContextual).belongsTo(testContextual2);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }
}