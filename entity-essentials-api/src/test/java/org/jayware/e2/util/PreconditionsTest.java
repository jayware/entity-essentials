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

import static org.assertj.core.api.Assertions.fail;


public class PreconditionsTest
{
    @Test
    public void test_checkArgument_Passes_if_the_Precondition_returns_true()
    {
        Preconditions.checkArgument(new Preconditions.Precondition()
        {
            @Override
            public boolean check()
            {
                return true;
            }
        });
    }

    @Test
    public void test_checkArgument_Throws_an_IllegalArgumentException_if_the_passed_Precondition_returns_false()
    {
        try
        {
            Preconditions.checkArgument(new Preconditions.Precondition()
            {
                @Override
                public boolean check()
                {
                    return false;
                }
            });

            fail("Expected to not reach this line because of an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {

        }
    }

    @Test
    public void test_checkArgument_Passes_if_true_is_passed_in()
    {
        Preconditions.checkArgument(true);
    }

    @Test
    public void test_checkArgument_Throws_an_IllegalArgumentException_if_false_is_passed()
    {
        try
        {
            Preconditions.checkArgument(false);
            fail("Expected to not reach this line because of an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {

        }
    }

    @Test
    public void test_checkState_Passes_if_the_Precondition_returns_true()
    {
        Preconditions.checkState(new Preconditions.Precondition()
            {
                @Override
                public boolean check()
                {
                    return true;
                }
            });
    }

    @Test
    public void test_checkState_Throws_an_IllegalStateException_if_the_passed_Precondition_returns_false()
    {
        try
        {
            Preconditions.checkState(new Preconditions.Precondition()
            {
                @Override
                public boolean check()
                {
                    return false;
                }
            });

            fail("Expected to not reach this line because of an IllegalStateException!");
        }
        catch (IllegalStateException ignored)
        {

        }
    }

    @Test
    public void test_checkState_Passes_if_true_is_passed_in()
    {
        Preconditions.checkState(true);
    }

    @Test
    public void test_checkState_Throws_an_IllegalStateException_if_false_is_passed()
    {
        try
        {
            Preconditions.checkState(false);
            fail("Expected to not reach this line because of an IllegalStateException!");
        }
        catch (IllegalStateException ignored)
        {

        }
    }

    @Test
    public void test_checkNotNull_Throws_an_IllegalArgumentException_if_null_is_passed()
    {
        try
        {
            Preconditions.checkNotNull(null);
            fail("Expected to not reach this line because of an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {

        }

        try
        {
            Preconditions.checkNotNull(null, "Got it!");
            fail("Expected to not reach this line because of an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {

        }
    }

    @Test
    public void test_checkStringNotEmpty_Passes_if_the_String_is_neither_null_nor_empty()
    {
        Preconditions.checkStringNotEmpty("this should pass the check!");
    }

    @Test
    public void test_checkStringNotEmpty_Throws_IllegalArgumentException_if_the_passed_String_is_null()
    {
        try
        {
            Preconditions.checkStringNotEmpty(null);
            fail("Expected to not reach this line because of an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {

        }
    }

    @Test
    public void test_checkStringNotEmpty_Throws_IllegalArgumentException_if_the_passed_String_is_empty()
    {
        try
        {
            Preconditions.checkStringNotEmpty("");
            fail("Expected to not reach this line because of an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {

        }
    }
}