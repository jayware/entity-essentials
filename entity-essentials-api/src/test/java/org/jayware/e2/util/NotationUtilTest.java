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

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.jayware.e2.util.NotationUtil.shortNotationOf;


public class NotationUtilTest
{
    @Test
    public void test_that_the_expected_short_notation_is_returned()
    {
        assertThat(shortNotationOf(NANOSECONDS)).isEqualTo("ns");
        assertThat(shortNotationOf(MICROSECONDS)).isEqualTo("µs");
        assertThat(shortNotationOf(MILLISECONDS)).isEqualTo("ms");
        assertThat(shortNotationOf(SECONDS)).isEqualTo("s");
        assertThat(shortNotationOf(MINUTES)).isEqualTo("m");
        assertThat(shortNotationOf(HOURS)).isEqualTo("h");
        assertThat(shortNotationOf(DAYS)).isEqualTo("d");
    }

    @Test
    public void test_an_IllegalArgumentException_is_thrown_when_null_is_passed()
    {
        try
        {
            shortNotationOf(null);
            fail("Expected to not reach this line because of an IllegalArgumentException!");
        }
        catch (IllegalArgumentException ignored)
        {

        }
    }
}