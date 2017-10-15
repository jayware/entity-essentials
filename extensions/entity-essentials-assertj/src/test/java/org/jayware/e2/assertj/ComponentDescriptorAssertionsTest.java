package org.jayware.e2.assertj;

import mockit.Expectations;
import mockit.Injectable;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.READ;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.WRITE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;


class ComponentDescriptorAssertionsTest
{
    private @Injectable ComponentDescriptor testDescriptor;
    private @Injectable ComponentPropertyDescriptor testPropertyDescriptor;
    private @Injectable ComponentPropertyAccessorDescriptor testAccessorDescriptor;

    @Test
    void should_not_return_null()
    {
        assertThat(ComponentDescriptorAssertions.assertThat(testDescriptor)).isNotNull();
    }

    @Test
    void should_not_fail_if_component_matches()
    {
        new Expectations() {{
            testDescriptor.getDeclaringComponent(); result = TestComponentA.class;
        }};

        try
        {
            ComponentDescriptorAssertions.assertThat(testDescriptor).describesComponent(TestComponentA.class);
        }
        catch (AssertionError e)
        {
            fail("Unexpected AssertionError!", e);
        }
    }

    @Test
    void should_fail_with_a_descriptive_message_if_component_does_not_match()
    {
        new Expectations() {{
            testDescriptor.getDeclaringComponent(); result = TestComponentA.class;
        }};

        final AssertionError assertionError = assertThrows(AssertionError.class, new Executable()
        {
            @Override
            public void execute()
            {
                ComponentDescriptorAssertions.assertThat(testDescriptor).describesComponent(TestComponentB.class);
            }
        });

        assertThat(assertionError).hasMessageContaining(TestComponentA.class.getName());
        assertThat(assertionError).hasMessageContaining(TestComponentB.class.getName());
    }

    @Test
    void should_not_fail_if_ComponentDescriptor_describes_the_expected_accessor()
    {
        final Map<String, List<ComponentPropertyAccessorDescriptor>> accessorMap = new HashMap<String, List<ComponentPropertyAccessorDescriptor>>();

        accessorMap.put("fubar", asList(testAccessorDescriptor));

        new Expectations() {{
            testDescriptor.getPropertyAccessorDescriptors(); result = accessorMap;
            testAccessorDescriptor.getAccessorName(); result = "theAccessor";
            testAccessorDescriptor.getPropertyType(); result = String.class;
            testAccessorDescriptor.getAccessorType(); result = READ;
        }};

        try
        {
            ComponentDescriptorAssertions.assertThat(testDescriptor).describesAccessor("theAccessor", String.class, READ);
        }
        catch (AssertionError e)
        {
            fail("Unexpected AssertionError!", e);
        }
    }

    @Test
    void should_fail_if_ComponentDescriptor_does_not_describe_the_expected_accessor()
    {
        final Map<String, List<ComponentPropertyAccessorDescriptor>> accessorMap = new HashMap<String, List<ComponentPropertyAccessorDescriptor>>();

        accessorMap.put("fubar", asList(testAccessorDescriptor));

        new Expectations() {{
            testDescriptor.getPropertyAccessorDescriptors(); result = accessorMap;
            testAccessorDescriptor.getAccessorName(); result = "fubar";
            testAccessorDescriptor.getPropertyType(); result = String.class;
            testAccessorDescriptor.getAccessorType(); result = READ;
        }};

        final AssertionError assertionError = assertThrows(AssertionError.class, new Executable()
        {
            @Override
            public void execute()
            {
                ComponentDescriptorAssertions.assertThat(testDescriptor).describesAccessor("theAccessor", String.class, WRITE);
            }
        });

        assertThat(assertionError).hasMessageContaining("theAccessor");
        assertThat(assertionError).hasMessageContaining("fubar");
        assertThat(assertionError).hasMessageContaining("<READ>");
        assertThat(assertionError).hasMessageContaining("<WRITE>");
        assertThat(assertionError).hasMessageContaining(String.class.getName());
    }

    @Test
    void should_not_fail_if_ComponentDescriptor_describes_the_expected_property()
    {
        new Expectations() {{
            testDescriptor.getPropertyDescriptors(); result = asList(testPropertyDescriptor);
            testPropertyDescriptor.getPropertyName(); result = "bar";
            testPropertyDescriptor.getPropertyType(); result = String.class;
        }};

        try
        {
            ComponentDescriptorAssertions.assertThat(testDescriptor).describesProperty("bar", String.class);
        }
        catch (AssertionError e)
        {
            fail("Unexpected AssertionError!", e);
        }
    }

    @Test
    void should_fail_if_ComponentDescriptor_does_not_describe_the_expected_property()
    {
        new Expectations() {{
            testDescriptor.getPropertyDescriptors(); result = asList(testPropertyDescriptor);
            testPropertyDescriptor.getPropertyName(); result = "foo";
            testPropertyDescriptor.getPropertyType(); result = String.class;
        }};

        final AssertionError assertionError = assertThrows(AssertionError.class, new Executable()
        {
            @Override
            public void execute()
            {
                ComponentDescriptorAssertions.assertThat(testDescriptor).describesProperty("bar", String.class);
            }
        });

        assertThat(assertionError).hasMessageContaining("bar");
        assertThat(assertionError).hasMessageContaining("foo");
        assertThat(assertionError).hasMessageContaining(String.class.getName());
    }

    public interface TestComponentA
    extends Component
    {

    }

    public interface TestComponentB
    extends Component
    {

    }
}