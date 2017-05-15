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
package org.jayware.e2.event.api;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.jupiter.api.Test;

import static org.jayware.e2.event.api.Preconditions.checkEventNotNullAndHasOneOfTypes;
import static org.junit.jupiter.api.Assertions.fail;


public class PreconditionsTest
{
    private @Mocked Event testEvent;

    @Test
    public void test_checkEventNotNullAndIsOfType_Throws_IllegalArgumentException_if_the_passed_Event_is_null()
    {
        try
        {
            checkEventNotNullAndHasOneOfTypes(null);

            fail("Expected an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {
        }
    }

    @Test
    public void test_checkEventNotNullAndIsOfType_Throws_IllegalArgumentException_if_the_passed_Event_does_not_have_the_expected_type()
    {
        new Expectations() {{
            testEvent.getType(); result = EventType.Command.class;
        }};

        try
        {
            checkEventNotNullAndHasOneOfTypes(testEvent, EventType.Notification.class);

            fail("Expected an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {
        }
    }

    @Test
    public void test_checkEventNotNullAndIsOfType_Throws_IllegalArgumentException_if_the_passed_Event_does_not_have_one_of_expected_types()
    {
        new Expectations() {{
            testEvent.getType(); result = EventType.Command.class;
        }};

        try
        {
            checkEventNotNullAndHasOneOfTypes(testEvent, EventType.Notification.class, EventType.Query.class);

            fail("Expected an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {
        }
    }

    @Test
    public void test_checkEventNotNullAndIsOfType_Does_not_throw_IllegalArgumentException_if_the_passed_Event_does_have_one_of_expected_types()
    {
        new Expectations() {{
            testEvent.getType(); result = EventType.Command.class;
        }};

        checkEventNotNullAndHasOneOfTypes(testEvent, EventType.Notification.class, EventType.Command.class, EventType.Query.class);
    }
}