package org.jayware.e2.assertj;

import mockit.Expectations;
import mockit.Mocked;
import org.assertj.core.api.Assertions;
import org.jayware.e2.context.api.Context;
import org.testng.annotations.Test;

import static org.jayware.e2.assertj.Common.ID;
import static org.jayware.e2.assertj.Common.ID2;
import static org.testng.Assert.fail;


public class ContextAssertionsTest
{
    private @Mocked Context testContext;

    @Test
    public void test_assertThat_does_not_return_null()
    throws Exception
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