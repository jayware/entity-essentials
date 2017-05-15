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
import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.context.api.Context;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;


public class ComponentAssertionsTest
{
    private @Mocked TestComponent testComponent;

    @Test
    public void test_that_assertThat_does_not_return_null()
    {
        Assertions.assertThat(ComponentAssertions.assertThat(testComponent)).isNotNull();
    }

    @Test
    public void test_that_hasProperty_Does_not_fail_if_the_Component_has_the_expected_Property()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("address", "name", "age");
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).hasProperty("name");
        }
        catch (AssertionError e)
        {
            fail("Unexpected AssertionError!", e);
        }
    }

    @Test
    public void test_that_hasProperty_Fails_if_the_Component_does_not_have_the_expected_Property()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("address", "name", "age");
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).hasProperty("fubar");
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_hasProperty_with_Class_Does_not_fail_if_the_Property_is_of_the_expected_type()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("address", "name", "age");
            testComponent.getPropertyTypes(); result = asList(String.class, String.class, Integer.class);
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).hasProperty("name", String.class);
        }
        catch (AssertionError e)
        {
            fail("Unexpected AssertionError.", e);
        }
    }

    @Test
    public void test_that_hasProperty_with_Class_Fails_if_the_Property_is_not_of_the_expected_type()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("address", "name", "age");
            testComponent.getPropertyTypes(); result = asList(String.class, String.class, Integer.class);
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).hasProperty("name", Integer.class);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_hasProperty_with_Class_Fails_if_the_Component_does_not_have_the_expected_Property()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("address", "name", "age");
            testComponent.getPropertyTypes(); result = asList(String.class, String.class, Integer.class);
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).hasProperty("fubar", Integer.class);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_property_hasValue_Does_not_fail_if_the_property_has_the_expected_value()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("name");
            testComponent.get("name"); result = "Elmar";
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).property("name").hasValue("Elmar");
        }
        catch (AssertionError e)
        {
            fail("Unexpected AssertionError!", e);
        }
    }

    @Test
    public void test_that_property_hasValue_Fails_if_the_property_does_not_have_the_expected_value()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("name");
            testComponent.get("name"); result = "Markus";
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).property("name").hasValue("Elmar");
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_property_hasNotValue_Does_not_fail_if_the_property_does_not_have_the_expected_value()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("name");
            testComponent.get("name"); result = "Markus";
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).property("name").hasNotValue("Elmar");
        }
        catch (AssertionError e)
        {
            fail("Unexpected AssertionError!", e);
        }
    }

    @Test
    public void test_that_property_hasNotValue_Fails_if_the_property_has_the_expected_value()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("name");
            testComponent.get("name"); result = "Elmar";
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).property("name").hasNotValue("Elmar");
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_property_hasType_Does_not_fail_if_the_property_is_of_the_expected_type()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("name");
            testComponent.getPropertyTypes(); result = asList(String.class);
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).property("name").hasType(String.class);
        }
        catch (AssertionError e)
        {
            fail("Unexpected AssertionError!", e);
        }
    }

    @Test
    public void test_that_property_hasType_Fails_if_the_property_is_not_of_the_expected_type()
    {
        new Expectations() {{
            testComponent.getPropertyNames(); result = asList("name");
            testComponent.getPropertyTypes(); result = asList(Integer.class);
        }};

        try
        {
            ComponentAssertions.assertThat(testComponent).property("name").hasType(String.class);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    public abstract class TestComponent
    extends AbstractComponent
    {
        public TestComponent(Context context)
        {
            super(context);
        }

        public abstract String getName();

        public abstract void setName(String name);
    }
}