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
import org.jayware.e2.event.api.EventType.Command;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.EventType.RootEvent;


public class InspectorTest
{
    private static final String testId = "0cd38cbb-b9d5-4e80-ac06-0337c1708225";

    private @Mocked Event testEvent;
    private @Mocked Parameter testParameter1, testParameter2;
    private @Mocked ReadOnlyParameters testParameters;

    @Test
    public void test_that_generateReport_()
    {
        new Expectations() {{
            testEvent.getType(); result = Command.class;
            testEvent.getId(); result = fromString("0cd38cbb-b9d5-4e80-ac06-0337c1708225");
            testEvent.getParameters(); result = testParameters;
            testParameters.iterator(); result = Arrays.asList(testParameter1, testParameter2).iterator();
            testParameter1.getName(); result = "foo";
            testParameter1.getValue(); result = "42";
            testParameter2.getName(); result = "bar";
            testParameter2.getValue(); result = 73;
        }};

        final String report = Inspector.generateReport("Fubar", testEvent);

        assertThat(report).startsWith("Fubar");
        assertThat(report).contains(testId);
        assertThat(report).contains(Command.class.getName());
        assertThat(report).contains(RootEvent.class.getName());
        assertThat(report).contains("Name: " + "foo");
        assertThat(report).contains("Value: " + "42");
        assertThat(report).contains("Type: " + "java.lang.String");
        assertThat(report).contains("Name: " + "bar");
        assertThat(report).contains("Value: " + "73");
        assertThat(report).contains("Type: " + "java.lang.Integer");
        assertThat(report).endsWith("---");
    }
}