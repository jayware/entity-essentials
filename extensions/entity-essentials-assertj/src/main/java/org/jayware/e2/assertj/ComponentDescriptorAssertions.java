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
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;

import java.util.List;


public class ComponentDescriptorAssertions
extends AbstractAssert<ComponentDescriptorAssertions, ComponentDescriptor>
{
    private ComponentDescriptorAssertions(ComponentDescriptor actual)
    {
        super(actual, ComponentDescriptorAssertions.class);
    }

    public static ComponentDescriptorAssertions assertThat(ComponentDescriptor descriptor)
    {
        return new ComponentDescriptorAssertions(descriptor);
    }

    public ComponentDescriptorAssertions describesComponent(Class<? extends Component> component)
    {
        isNotNull();

        if (!actual.getDeclaringComponent().equals(component))
        {
            final String expectedComponentName = component.getName();
            final String actualComponentName = actual.getDeclaringComponent().getName();

            failWithMessage("Expected a ComponentDescriptor for: '%s',\n\tbut it describes: '%s'", expectedComponentName, actualComponentName);
        }

        return this;
    }

    public ComponentDescriptorAssertions describesAccessor(String name, Class<?> type)
    {
        return describesAccessor(name, type, null);
    }

    public ComponentDescriptorAssertions describesAccessor(String name, Class<?> type, AccessorType accessorType)
    {
        isNotNull();

        for (List<ComponentPropertyAccessorDescriptor> accessorDescriptorsList : actual.getPropertyAccessorDescriptors().values())
        {
            for (ComponentPropertyAccessorDescriptor accessorDescriptor : accessorDescriptorsList)
            {
                final Class propertyType = accessorDescriptor.getPropertyType();
                final String accessorName = accessorDescriptor.getAccessorName();

                if (accessorName.equals(name) && propertyType.equals(type) && (accessorDescriptor.getAccessorType() == accessorType || accessorType == null))
                {
                    return this;
                }
            }
        }

        failWithMessage("Expected an Accessor with name '%s' of type '%s' <%s>, but got:\n%s", name, type.getName(), accessorType, extractListOfAccessors(actual));

        return this;
    }

    public ComponentDescriptorAssertions describesProperty(String name, Class<?> type)
    {
        isNotNull();

        for (ComponentPropertyDescriptor propertyDescriptor : actual.getPropertyDescriptors())
        {
            final String propertyName = propertyDescriptor.getPropertyName();
            final Class propertyType = propertyDescriptor.getPropertyType();

            if (propertyName.equals(name) && propertyType.equals(type))
            {
                return this;
            }
        }

        failWithMessage("Expected a Property with name '%s' for type '%s', but got:\n%s", name, type.getName(), extractListOfProperties(actual));

        return this;
    }

    private static String extractListOfProperties(ComponentDescriptor descriptor)
    {
        StringBuilder result = new StringBuilder();

        for (ComponentPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
        {
            final String typeName = propertyDescriptor.getPropertyType().getName();
            final String propertyName = propertyDescriptor.getPropertyName();

            result.append("\n\t - ").append(propertyName).append(": ").append(typeName);
        }

        return result.toString();
    }

    private static String extractListOfAccessors(ComponentDescriptor descriptor)
    {
        final StringBuilder result = new StringBuilder();

        for (List<ComponentPropertyAccessorDescriptor> accessorDescriptorsList : descriptor.getPropertyAccessorDescriptors().values())
        {
            for (ComponentPropertyAccessorDescriptor accessorDescriptor : accessorDescriptorsList)
            {
                final String propertyType = accessorDescriptor.getPropertyType().getName();
                final String accessorName = accessorDescriptor.getAccessorName();
                final AccessorType accessorType = accessorDescriptor.getAccessorType();

                result.append("\n\t - ").append(accessorName).append(": ").append(propertyType).append(" <").append(accessorType).append(">");
            }
        }

        return result.toString();
    }
}
