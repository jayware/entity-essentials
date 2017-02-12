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
package org.jayware.e2.assembly.api;

import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.fail;
import static org.jayware.e2.assembly.api.Preconditions.checkGroupNotNullAndValid;
import static org.jayware.e2.assembly.api.Preconditions.checkNodeNotNullAndValid;


public class PreconditionsTest
{
    private @Mocked Group testGroup;
    private @Mocked Node testNode;
    private @Mocked EntityRef testNodeRef;

    @Test
    public void test_that_checkGroupNotNullAndValid_Throws_an_IllegalArgumentException_if_the_passed_Group_is_null()
    {
        try
        {
            checkGroupNotNullAndValid(null);
            fail("Expected to not reach this line due to an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored) {}
    }

    @Test
    public void test_that_checkGroupNotNullAndValid_Throws_an_InvalidGroupException_if_the_passed_Group_is_invalid()
    {
        new Expectations() {{
            testGroup.isInvalid(); result = true;
        }};

        try
        {
            checkGroupNotNullAndValid(testGroup);
            fail("Expected to not reach this line due to an InvalidGroupException!");
        }
        catch (InvalidGroupException ignored) {}
    }

    @Test
    public void test_that_checkNodeNotNullAndValid_Throws_an_IllegalArgumentException_if_the_passed_Node_is_null()
    {
        try
        {
            checkNodeNotNullAndValid(null);
            fail("Expected to not reach this line due to an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored) {}
    }

    @Test
    public void test_that_checkNodeNotNullAndValid_Throws_an_InvalidNodeException_if_the_passed_Node_is_invalid()
    {
        new Expectations() {{
           testNode.isInvalid(); result = true;
        }};

        try
        {
            checkNodeNotNullAndValid(testNode);
            fail("Expected to not reach this line due to an InvalidNodeException!");
        }
        catch (InvalidNodeException ignored) {}
    }

    @Test
    public void test_that_checkNodeNotNullAndValid_Throws_an_InvalidNodeException_if_the_EntityRef_of_the_passed_Node_is_null()
    {
        new Expectations() {{
            testNode.isInvalid(); result = false;
            testNode.getNodeRef(); result = null;
        }};

        try
        {
            checkNodeNotNullAndValid(testNode);
            fail("Expected to not reach this line due to an InvalidNodeException!");
        }
        catch (InvalidNodeException ignored) {}
    }

    @Test
    public void test_that_checkNodeNotNullAndValid_Throws_an_InvalidNodeException_if_the_EntityRef_of_the_passed_Node_is_invalid()
    {
        new Expectations() {{
            testNode.isInvalid(); result = false;
            testNode.getNodeRef(); result = testNodeRef;
            testNodeRef.isInvalid(); result = true;
        }};

        try
        {
            checkNodeNotNullAndValid(testNode);
            fail("Expected to not reach this line due to an InvalidNodeException!");
        }
        catch (InvalidNodeException ignored) {}
    }
}