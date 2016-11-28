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