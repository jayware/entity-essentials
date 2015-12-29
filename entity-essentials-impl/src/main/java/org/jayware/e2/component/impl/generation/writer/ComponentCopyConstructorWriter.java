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


import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.context.api.Context;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentCopyConstructorWriter
{
    public void writeCopyConstructorFor(ComponentGenerationPlan componentPlan)
    {
        final ClassWriter classWriter = componentPlan.getClassWriter();

        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_PUBLIC, "<init>", "(L" + componentPlan.getGeneratedClassInternalName() + ";)V");
        methodBuilder.beginMethod();
        methodBuilder.loadThis();
        methodBuilder.duplicateTopStackElement();
        methodBuilder.loadReferenceVariable(1);
        methodBuilder.loadField(AbstractComponent.class, "myContext", Context.class);
        methodBuilder.invokeConstructor(AbstractComponent.class, Context.class);
        methodBuilder.loadReferenceVariable(1);
        methodBuilder.invokeVirtualMethod(componentPlan.getGeneratedClassInternalName(), "copy", Component.class, Component.class);
        methodBuilder.returnVoid();
        methodBuilder.endMethod();
    }
}
