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
package org.jayware.e2.component.impl.generation.analyse;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptorBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptorBuilder.ComponentDescriptorBuilderDescribe;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptorPart;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptorBuilder;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptorBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.READ;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.WRITE;
import static org.jayware.e2.util.Preconditions.checkNotNull;


public class ComponentDescriptorBuilderImpl
implements ComponentDescriptorBuilder, ComponentDescriptorBuilderDescribe
{
    private Class<? extends Component> myComponent;
    private final Set<Class<? extends Component>> myHierarchy = new HashSet<Class<? extends Component>>();
    private final Map<String, ComponentPropertyDescriptor> myPropertyDescriptors = new HashMap<String, ComponentPropertyDescriptor>();
    private final Map<String, List<ComponentPropertyAccessorDescriptor>> myPropertyAccessorDescriptors = new HashMap<String, List<ComponentPropertyAccessorDescriptor>>();

    private final ComponentPropertyDescriptorBuilder myPropertyDescriptorBuilder = new ComponentPropertyDescriptorBuilderImpl();
    private final ComponentPropertyAccessorDescriptorBuilder myAccessorDescriptorBuilder = new ComponentPropertyAccessorDescriptorBuilderImpl();

    @Override
    public ComponentDescriptorBuilderDescribe describe(Class<? extends Component> component)
    {
        checkNotNull(component);

        myComponent = component;
        return this;
    }

    @Override
    public ComponentDescriptorBuilderDescribe hierarchy(Collection<Class<? extends Component>> hierarchy)
    {
        checkNotNull(hierarchy);

        myHierarchy.addAll(hierarchy);
        return this;
    }

    @Override
    public ComponentDescriptorBuilderDescribe addProperty(ComponentPropertyDescriptor descriptor)
    {
        checkNotNull(descriptor);
        checkBelongsToCurrentComponent(descriptor);

        final String propertyName = descriptor.getPropertyName();

        if (myPropertyDescriptors.containsKey(propertyName))
        {
            throw new IllegalStateException("Property already exists!");
        }

        myPropertyDescriptors.put(propertyName, descriptor);

        return this;
    }

    @Override
    public ComponentDescriptorBuilderDescribe addAccessor(ComponentPropertyAccessorDescriptor descriptor)
    {
        checkNotNull(descriptor);
        checkBelongsToCurrentComponent(descriptor);

        final String propertyName = descriptor.getPropertyName();
        ComponentPropertyDescriptor propertyDescriptor = myPropertyDescriptors.get(propertyName);
        List<ComponentPropertyAccessorDescriptor> accessors;

        if (propertyDescriptor == null)
        {
            propertyDescriptor = myPropertyDescriptorBuilder.property(propertyName).type(descriptor.getPropertyType())
                                                            .declaringComponent(descriptor.getDeclaringComponent())
                                                            .build();

            myPropertyDescriptors.put(propertyName, propertyDescriptor);
        }
        else
        {
            if (!propertyDescriptor.getPropertyType().equals(descriptor.getPropertyType()))
            {
                throw new IllegalArgumentException();
            }
        }

        accessors = myPropertyAccessorDescriptors.get(propertyName);

        if (accessors == null)
        {
            accessors = new ArrayList<ComponentPropertyAccessorDescriptor>();
            myPropertyAccessorDescriptors.put(propertyName, accessors);
            accessors.add(descriptor);
        }
        else
        {
            for (ComponentPropertyAccessorDescriptor otherDescriptor : accessors)
            {
                if (descriptor.getAccessorMethodDescriptor().equals(otherDescriptor.getAccessorMethodDescriptor()))
                {
                    throw new IllegalStateException();
                }
            }

            accessors.add(descriptor);
        }

        return this;
    }

    @Override
    public ComponentDescriptor build()
    {
        addMissingAccessors();

        try
        {
            return new ComponentDescriptorImpl(myComponent, myPropertyDescriptors.values(), myPropertyAccessorDescriptors);
        }
        finally
        {
            reset();
        }
    }

    private void addMissingAccessors()
    {
        for (ComponentPropertyDescriptor propertyDescriptor : new HashSet<ComponentPropertyDescriptor>(myPropertyDescriptors.values()))
        {
            final String propertyName = propertyDescriptor.getPropertyName();
            final Class propertyType = propertyDescriptor.getPropertyType();
            final Class<? extends Component> declaringComponent = propertyDescriptor.getDeclaringComponent();
            final List<ComponentPropertyAccessorDescriptor> accessorDescriptors = myPropertyAccessorDescriptors.get(propertyName);

            boolean addGetter = true;
            boolean addSetter = true;

            if (accessorDescriptors != null)
            {
                for (ComponentPropertyAccessorDescriptor accessorDescriptor : accessorDescriptors)
                {
                    if (accessorDescriptor.getAccessorType() == READ)
                    {
                        addGetter = false;
                    }
                    else if (accessorDescriptor.getAccessorType() == WRITE)
                    {
                        addSetter = false;
                    }
                }
            }

            if (addGetter)
            {
                addAccessor(myAccessorDescriptorBuilder.accessor(propertyType)
                                                       .declaringComponent(declaringComponent)
                                                       .name(createMethodName("get", propertyName))
                                                       .type(READ)
                                                       .property(propertyName).type(propertyType)
                                                       .build()
                );
            }

            if (addSetter)
            {
                addAccessor(myAccessorDescriptorBuilder.accessor(void.class, propertyType)
                                                       .declaringComponent(declaringComponent)
                                                       .name(createMethodName("set", propertyName))
                                                       .type(WRITE)
                                                       .property(propertyName).type(propertyType)
                                                       .build()
                );
            }
        }
    }

    private void reset()
    {
        myComponent = null;
        myHierarchy.clear();
        myPropertyDescriptors.clear();
        myPropertyAccessorDescriptors.clear();
    }

    private String createMethodName(String prefix, String propertyName)
    {
        return prefix + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    private void checkBelongsToCurrentComponent(ComponentDescriptorPart part)
    {
        final Class<? extends Component> declaringComponent = part.getDeclaringComponent();
        if (!(myComponent.equals(declaringComponent) || myHierarchy.contains(declaringComponent)))
        {
            throw new IllegalArgumentException("Does not belong to current component!");
        }
    }

    private static class ComponentDescriptorImpl
    implements ComponentDescriptor
    {
        private final Class<? extends Component> myDeclaringComponent;
        private final List<ComponentPropertyDescriptor> myPropertyDescriptors;
        private final Map<String, List<ComponentPropertyAccessorDescriptor>> myPropertyAccessorDescriptors;
        private final String myStringRepresentation;

        private ComponentDescriptorImpl(Class<? extends Component> declaringComponent, Collection<ComponentPropertyDescriptor> propertyDescriptors, Map<String, List<ComponentPropertyAccessorDescriptor>> propertyAccessorDescriptors)
        {
            myDeclaringComponent = declaringComponent;
            myPropertyDescriptors = unmodifiableList(new ArrayList<ComponentPropertyDescriptor>(propertyDescriptors));
            myPropertyAccessorDescriptors = unmodifiableMap(new HashMap<String, List<ComponentPropertyAccessorDescriptor>>(propertyAccessorDescriptors));

            myStringRepresentation = toString(this);
        }

        @Override
        public Class<? extends Component> getDeclaringComponent()
        {
            return myDeclaringComponent;
        }

        @Override
        public List<ComponentPropertyDescriptor> getPropertyDescriptors()
        {
            return myPropertyDescriptors;
        }

        @Override
        public Map<String, List<ComponentPropertyAccessorDescriptor>> getPropertyAccessorDescriptors()
        {
            return myPropertyAccessorDescriptors;
        }

        @Override
        public List<ComponentPropertyAccessorDescriptor> getPropertyAccessorDescriptors(String name)
        {
            return myPropertyAccessorDescriptors.get(name);
        }

        @Override
        public ComponentPropertyAccessorDescriptor getPropertyAccessorDescriptor(String propertyName, AccessorType type)
        {
            for (ComponentPropertyAccessorDescriptor accessorDescriptor : getPropertyAccessorDescriptors(propertyName))
            {
                if (accessorDescriptor.getAccessorType().equals(type))
                {
                    return accessorDescriptor;
                }
            }

            throw new IllegalStateException();
        }

        @Override
        public String toString()
        {
            return myStringRepresentation;
        }

        private static String toString(ComponentDescriptor descriptor)
        {
            final StringBuilder result = new StringBuilder("{\"ComponentDescriptor\": {");

            final Class declaringComponent = descriptor.getDeclaringComponent();
            final List<ComponentPropertyDescriptor> propertyDescriptors = descriptor.getPropertyDescriptors();
            final Map<String, List<ComponentPropertyAccessorDescriptor>> propertyAccessorDescriptors = descriptor.getPropertyAccessorDescriptors();

            result.append("\"component\": \"").append(declaringComponent.getName()).append("\", ");

            appendProperties(result, propertyDescriptors, propertyAccessorDescriptors);

            result.append("}}");

            return result.toString();
        }

        private static void appendProperties(final StringBuilder result, final List<ComponentPropertyDescriptor> propertyDescriptors, final Map<String, List<ComponentPropertyAccessorDescriptor>> propertyAccessorDescriptors)
        {
            final Iterator<ComponentPropertyDescriptor> propertyDescriptorIterator = propertyDescriptors.iterator();

            result.append("\"properties\": [");

            while (propertyDescriptorIterator.hasNext())
            {
                final ComponentPropertyDescriptor propertyDescriptor = propertyDescriptorIterator.next();
                final String propertyName = propertyDescriptor.getPropertyName();

                result.append("{\"").append(propertyName).append("\": {");
                result.append("\"type\": \"").append(propertyDescriptor.getPropertyType().getName()).append("\", ");

                appendAccessors(result, propertyAccessorDescriptors.get(propertyName));

                if (propertyDescriptorIterator.hasNext())
                {
                    result.append("}}, ");
                }
                else
                {
                    result.append("}}");
                }
            }

            result.append("]");
        }

        private static void appendAccessors(StringBuilder result, List<ComponentPropertyAccessorDescriptor> propertyAccessorDescriptors)
        {
            result.append("\"accessors\": [");

            if (propertyAccessorDescriptors != null)
            {
                final Iterator<ComponentPropertyAccessorDescriptor> accessorDescriptorIterator = propertyAccessorDescriptors.iterator();
                while (accessorDescriptorIterator.hasNext())
                {
                    final ComponentPropertyAccessorDescriptor accessorDescriptor = accessorDescriptorIterator.next();

                    result.append("{\"").append(accessorDescriptor.getAccessorName()).append("\": {");
                    result.append("\"type\": \"").append(accessorDescriptor.getAccessorType()).append("\"");

                    result.append("}");

                    if (accessorDescriptorIterator.hasNext())
                    {
                        result.append("}, ");
                    }
                    else
                    {
                        result.append("}");
                    }
                }
            }
            result.append("]");
        }
    }
}
