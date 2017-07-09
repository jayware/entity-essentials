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
package org.jayware.e2.component.impl.generation.writer;


public class ComponentWriterFactory
{
    public ComponentPropertyFieldWriter createComponentPropertyFieldWriter()
    {
        return new ComponentPropertyFieldWriter();
    }

    public ComponentStaticInitializerWriter createComponentStaticInitializerWriter()
    {
        return new ComponentStaticInitializerWriter();
    }

    public ComponentDefaultConstructorWriter createComponentDefaultConstructorWriter()
    {
        return new ComponentDefaultConstructorWriter();
    }

    public ComponentCopyConstructorWriter createComponentCopyConstructorWriter()
    {
        return new ComponentCopyConstructorWriter();
    }

    public ComponentPropertyGetterMethodWriter createComponentPropertyGetterWriter()
    {
        return new ComponentPropertyGetterMethodWriter();
    }

    public ComponentPropertySetterMethodWriter createComponentPropertySetterWriter()
    {
        return new ComponentPropertySetterMethodWriter();
    }

    public ComponentGetPropertyNamesMethodWriter createGetPropertyNamesMethodWriter()
    {
        return new ComponentGetPropertyNamesMethodWriter();
    }

    public ComponentGetPropertyTypesMethodWriter createGetPropertyTypeNamesMethodWriter()
    {
        return new ComponentGetPropertyTypesMethodWriter();
    }

    public ComponentGetMethodWriter createComponentGetMethodWriter()
    {
        return new ComponentGetMethodWriter();
    }

    public ComponentSetMethodWriter createComponentSetMethodWriter()
    {
        return new ComponentSetMethodWriter();
    }

    public ComponentHasMethodWriter createComponentHasMethodWriter()
    {
        return new ComponentHasMethodWriter();
    }

    public ComponentTypeMethodWriter createComponentTypeMethodWriter()
    {
        return new ComponentTypeMethodWriter();
    }

    public ComponentCopyOtherMethodWriter createComponentCopyOtherMethodWriter()
    {
        return new ComponentCopyOtherMethodWriter();
    }

    public ComponentCopyThisMethodWriter createComponentCopyThisMethodWriter()
    {
        return new ComponentCopyThisMethodWriter();
    }

    public ComponentEqualsMethodWriter createComponentEqualsMethodWriter()
    {
        return new ComponentEqualsMethodWriter();
    }

    public ComponentHashCodeMethodWriter createComponentHashcodeMethodWriter()
    {
        return new ComponentHashCodeMethodWriter();
    }

    public ComponentToStringMethodWriter createComponentToStringMethodWriter()
    {
        return new ComponentToStringMethodWriter();
    }
}
