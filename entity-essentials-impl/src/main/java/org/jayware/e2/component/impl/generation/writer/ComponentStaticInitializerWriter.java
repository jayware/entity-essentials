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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jayware.e2.util.TypeUtil.getTypeName;
import static org.objectweb.asm.Opcodes.ACC_STATIC;


public class ComponentStaticInitializerWriter
{
    public void writeStaticInitializerFor(ComponentGenerationPlan componentPlan)
    {
        final ClassWriter classWriter = componentPlan.getClassWriter();

        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_STATIC, "<clinit>", "()V");
        methodBuilder.beginMethod();

        final int namesList = 1, typesList = 2;
        final String namesListField = "ourPropertyNames", typesListField = "ourPropertyTypeNames";

        writeNewArrayListInstance(methodBuilder, namesList);
        writeNewArrayListInstance(methodBuilder, typesList);

        for (ComponentPropertyGenerationPlan propertyPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            methodBuilder.loadReferenceVariable(namesList);
            methodBuilder.loadConstant(propertyPlan.getPropertyName());
            methodBuilder.invokeInterfaceMethod(List.class, "add", boolean.class, Object.class);
            methodBuilder.loadReferenceVariable(typesList);
            methodBuilder.loadConstant(getTypeName(propertyPlan.getPropertyType()));
            methodBuilder.invokeInterfaceMethod(List.class, "add", boolean.class, Object.class);
        }

        writeStoreUnmodifiableArrayList(componentPlan.getGeneratedClassInternalName(), methodBuilder, namesList, namesListField);
        writeStoreUnmodifiableArrayList(componentPlan.getGeneratedClassInternalName(), methodBuilder, typesList, typesListField);

        methodBuilder.returnVoid();
        methodBuilder.endMethod();
    }

    private void writeStoreUnmodifiableArrayList(String classInternalName, MethodBuilder methodBuilder, int index, String field)
    {
        methodBuilder.loadReferenceVariable(index);
        methodBuilder.invokeStaticMethod(Collections.class, "unmodifiableList", List.class, List.class);
        methodBuilder.storeStaticField(classInternalName, field, List.class);
    }

    private void writeNewArrayListInstance(MethodBuilder methodBuilder, int index)
    {
        methodBuilder.newInstanceOf(ArrayList.class);
        methodBuilder.duplicateTopStackElement();
        methodBuilder.invokeConstructor(ArrayList.class);
        methodBuilder.storeReferenceVariable(index);
    }
}
