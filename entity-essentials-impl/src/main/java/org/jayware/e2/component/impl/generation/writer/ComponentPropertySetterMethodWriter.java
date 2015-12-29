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


import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentPropertySetterMethodWriter
{
    public void writePropertySetterFor(ComponentPropertyGenerationPlan propertyPlan)
    {
        final ComponentGenerationPlan componentPlan = propertyPlan.getComponentGenerationPlan();
        final ClassWriter classWriter = componentPlan.getClassWriter();

        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_PUBLIC, propertyPlan.getPropertySetterMethodName(), propertyPlan.getPropertySetterMethodDescriptor());
        methodBuilder.beginMethod();
        methodBuilder.loadThis();
        methodBuilder.loadVariable(1, propertyPlan.getPropertyType());
        methodBuilder.storeField(componentPlan.getGeneratedClassInternalName(), propertyPlan.getPropertyName(), propertyPlan.getPropertyType());
        methodBuilder.returnVoid();
        methodBuilder.endMethod();
    }
}
