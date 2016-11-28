package org.jayware.e2.assertj;

import mockit.Expectations;
import mockit.Mocked;
import org.assertj.core.api.Assertions;
import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.jayware.e2.assertj.Common.ID;
import static org.jayware.e2.assertj.Common.ID2;
import static org.testng.Assert.fail;


public class EntityAssertionsTest
{
    private @Mocked Context testContext;
    private @Mocked EntityRef testRef;
    private @Mocked ComponentManager testComponentManager;
    private @Mocked Aspect testAspect;

    @Test
    public void test_assertThat_does_not_return_null()
    throws Exception
    {
        Assertions.assertThat(EntityAssertions.assertThat(testRef)).isNotNull();
    }

    @Test
    public void test_that_isValid_fails_if_the_passed_EntityRef_is_invalid()
    {
        new Expectations() {{
            testRef.getId(); result = ID;
            testRef.isInvalid(); result = true;
        }};

        try
        {
            EntityAssertions.assertThat(testRef).isValid();
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_isInvalid_fails_if_the_passed_EntityRef_is_valid()
    {
        new Expectations() {{
            testRef.getId(); result = ID;
            testRef.isValid(); result = true;
        }};

        try
        {
            EntityAssertions.assertThat(testRef).isInvalid();
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_matches_fails_if_the_Entity_denoted_by_the_passed_EntityRef_does_not_match_the_specified_aspect()
    {
        new Expectations() {{
            testRef.getId(); result = ID;
            testAspect.matches(testRef); result = false;
        }};

        try
        {
            EntityAssertions.assertThat(testRef).matches(testAspect);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_hasAtLeast_fails_if_the_Entity_denoted_by_the_passed_EntityRef_does_not_have_the_expected_Components()
    {
        new Expectations() {{
            testRef.getId(); result = ID;
            testComponentManager.getComponentTypes(testRef); result = asList(ComponentC.class);
        }};

        try
        {
            EntityAssertions.assertThat(testRef).hasAtLeast(ComponentA.class, ComponentB.class);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_hasExactly_fails_if_the_Entity_denoted_by_the_passed_EntityRef_does_not_have_the_expected_Components()
    {
        new Expectations() {{
            testRef.getId(); result = ID;
            testComponentManager.getComponentTypes(testRef); result = asList(ComponentC.class);
        }};

        try
        {
            EntityAssertions.assertThat(testRef).hasExactly(ComponentA.class, ComponentB.class);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_hasId_with_String_fails_if_the_EntityRef_does_not_have_the_expected_Id()
    {
        new Expectations() {{
            testRef.getId(); result = ID;
        }};

        try
        {
            EntityAssertions.assertThat(testRef).hasId(ID2.toString());
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    @Test
    public void test_that_hasId_with_UUID_fails_if_the_EntityRef_does_not_have_the_expected_UUID()
    {
        new Expectations() {{
            testRef.getId(); result = ID;
        }};

        try
        {
            EntityAssertions.assertThat(testRef).hasId(ID2);
        }
        catch (AssertionError ignored)
        {
            return;
        }

        fail("Expected an AssertionError.");
    }

    private interface ComponentA
    extends Component
    {

    }

    private interface ComponentB
    extends Component
    {

    }

    private interface ComponentC
    extends Component
    {

    }
}