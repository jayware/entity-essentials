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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class PreconditionsTest
{
    @Mocked Context testContext;
    @Mocked EntityRef testRef;

    @Test
    public void test_that_checkRefNotNullAndValid_returns_the_passed_EntityRef_if_everything_is_fine()
    {
        new Expectations() {{
            testRef.isInvalid(); result = false;
        }};

        assertThat(Preconditions.checkRefNotNullAndValid(testRef)).isEqualTo(testRef);
    }

    @Test
    public void test_that_checkRefNotNullAndValid_throws_an_IllegalArgumentException_if_the_passed_EntityRef_is_null()
    {
        try
        {
            Preconditions.checkRefNotNullAndValid(null);
            fail("Expected an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {

        }
    }

    @Test
    public void test_that_checkRefNotNullAndValid_throws_an_InvalidEntityRefException_if_the_passed_EntityRef_is_invalid()
    {
        new Expectations() {{
            testRef.isInvalid(); result = true;
            testRef.getContext();  result = testContext;
            testContext.isDisposed(); result = false;
        }};

        try
        {
            Preconditions.checkRefNotNullAndValid(testRef);
            fail("Expected an InvalidEntityRefException!");
        }
        catch (InvalidEntityRefException ignored)
        {

        }
    }

    @Test
    public void test_that_checkRefNotNullAndValid_throws_an_IllegalStateException_if_the_passed_EntityRef_is_invalid_due_to_a_disposed_context()
    {
        new Expectations() {{
           testRef.isInvalid(); result = true;
           testRef.getContext();  result = testContext;
           testContext.isDisposed(); result = true;
        }};

        try
        {
            Preconditions.checkRefNotNullAndValid(testRef);
            fail("Expected an IllegalStateException!");
        }
        catch (IllegalStateException ignored)
        {

        }
    }
}