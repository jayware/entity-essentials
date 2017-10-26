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
package org.jayware.e2.component.api.generation.analyse;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.Property;


public interface ComponentPropertyDescriptorBuilder
{
    ComponentPropertyDescriptorPropertyBuilder property(Property property);

    ComponentPropertyDescriptorTypeBuilder property(String name);

    ComponentPropertyDescriptor build();

    interface ComponentPropertyDescriptorTypeBuilder
    {
        ComponentPropertyDescriptorDeclaringComponentBuilder type(Class<?> type);
    }

    interface ComponentPropertyDescriptorDeclaringComponentBuilder
    {
        ComponentPropertyDescriptorBuilder declaringComponent(Class<? extends Component> declaringComponent);
    }

    interface ComponentPropertyDescriptorPropertyBuilder
    {
        ComponentPropertyDescriptorNameBuilder name(String name);
    }

    interface ComponentPropertyDescriptorNameBuilder
    {
        ComponentPropertyDescriptor build();
    }
}
