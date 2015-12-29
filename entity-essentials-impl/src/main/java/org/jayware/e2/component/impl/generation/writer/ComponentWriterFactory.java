/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
 *                    Markus Neubauer <markus.neubauer@jayware.org>
 *
 *     This file is part of Entity Essentials.
 *
 *     Entity Essentials is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     Entity Essentials is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    public ComponentPropertiesMethodWriter createPropertiesMethodWriter()
    {
        return new ComponentPropertiesMethodWriter();
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

    public ComponentToStringMethodWriter createComponentToStringMethodWriter()
    {
        return new ComponentToStringMethodWriter();
    }
}
