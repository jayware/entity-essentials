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

import org.assertj.core.api.AbstractAssert;
import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;

import java.util.List;


public class ComponentAssertions
extends AbstractAssert<ComponentAssertions, AbstractComponent>
{
    private ComponentAssertions(AbstractComponent actual)
    {
        super(actual, ComponentAssertions.class);
    }

    public static ComponentAssertions assertThat(Component component)
    {
        return new ComponentAssertions((AbstractComponent) component);
    }

    public void hasProperty(String expectedProperty)
    {
        final List<String> propertyNames;

        isNotNull();

        propertyNames = actual.getPropertyNames();

        if (!propertyNames.contains(expectedProperty))
        {
            failWithMessage("Expected Component <%s> to have a property named <%s>", actual.getClass().getName(), expectedProperty);
        }
    }

    public void hasProperty(String expectedProperty, Class<?> expectedType)
    {
        final List<String> propertyNames;
        final List<Class> propertyTypes;
        final int indexOfProperty;

        isNotNull();

        propertyNames = actual.getPropertyNames();
        propertyTypes = actual.getPropertyTypes();
        indexOfProperty = propertyNames.indexOf(expectedProperty);

        if (indexOfProperty < 0)
        {
            failWithMessage("Expected Component <%s> to have a property named <%s>", actual.getClass().getName(), expectedProperty);
        }

        if (!propertyTypes.get(indexOfProperty).equals(expectedType))
        {
            failWithMessage("Expected Property <%s> of Component <%s> to be of type <%s>", expectedProperty, actual.getClass().getName(), expectedType.getName());

        }
    }

    public ComponentPropertyAssertions property(final String property)
    {
        isNotNull();
        hasProperty(property);

        return new ComponentPropertyAssertions(property);
    }

    public class ComponentPropertyAssertions
    extends AbstractAssert<ComponentPropertyAssertions, String>
    {
        private ComponentPropertyAssertions(String actual)
        {
            super(actual, ComponentPropertyAssertions.class);
        }

        public void hasValue(Object expectedValue)
        {
            final Object actualValue = ComponentAssertions.this.actual.get(actual);

            if (actualValue == null || !actualValue.equals(expectedValue))
            {
                failWithMessage("Expected Property <%s> of Component <%s> does have the value: %s", actual, ComponentAssertions.this.actual.getClass().getName(), expectedValue);
            }
        }

        public void hasNotValue(Object expectedValue)
        {
            final Object actualValue = ComponentAssertions.this.actual.get(actual);

            if (actualValue == null || actualValue.equals(expectedValue))
            {
                failWithMessage("Expected Property <%s> of Component <%s> does not have the value: %s", actual, ComponentAssertions.this.actual.getClass().getName(), expectedValue);
            }
        }

        public void hasType(Class<?> expectedType)
        {
            hasProperty(actual, expectedType);
        }
    }
}
