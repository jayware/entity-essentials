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

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;


public class ConfigurationUtilTest
{
    private Dictionary<String, String> testDictionary;
    private Properties testProperties;

    @BeforeTest
    public void setup()
    {
        testDictionary = new Hashtable<String, String>();
        testProperties = new Properties();
    }

    @Test
    public void test_that_getPropertyOrDefault_returns_the_value_specified_for_the_passed_key()
    {
        testDictionary.put("fubar", "42");
        testProperties.put("fubar", "42");

        assertThat(ConfigurationUtil.getPropertyOrDefault(testDictionary, "fubar", null)).isEqualTo("42");
        assertThat(ConfigurationUtil.getPropertyOrDefault(testProperties, "fubar", null)).isEqualTo("42");
    }

    @Test
    public void test_that_getPropertyOrDefault_returns_the_specified_default_value_if_the_passed_Dictionary_is_null()
    {
        assertThat(ConfigurationUtil.getPropertyOrDefault((Dictionary<String, ?>) null, "test", "defaultValue")).isEqualTo("defaultValue");
        assertThat(ConfigurationUtil.getPropertyOrDefault((Properties) null, "test", "defaultValue")).isEqualTo("defaultValue");
    }
}