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


import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.objectweb.asm.ClassWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveType;
import static org.objectweb.asm.Opcodes.ACC_STATIC;


public class ComponentStaticInitializerWriter
{
    public void writeStaticInitializerFor(ComponentGenerationPlan componentPlan)
    {
        final ClassWriter classWriter = componentPlan.getClassWriter();

        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_STATIC, "<clinit>", "()V");
        methodBuilder.beginMethod();

        final int namesList = 1, typesList = 2;
        final String namesListField = "ourPropertyNames", typesListField = "ourPropertyTypes";

        writeNewArrayListInstance(methodBuilder, namesList);
        writeNewArrayListInstance(methodBuilder, typesList);

        for (ComponentPropertyGenerationPlan propertyPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            methodBuilder.loadReferenceVariable(namesList);
            methodBuilder.loadConstant(propertyPlan.getPropertyName());
            methodBuilder.invokeInterfaceMethod(List.class, "add", boolean.class, Object.class);
            methodBuilder.loadReferenceVariable(typesList);

            if (isPrimitiveType(propertyPlan.getPropertyType()))
            {
                methodBuilder.loadPrimitiveTypeConstant(propertyPlan.getPropertyType());
            }
            else
            {
                methodBuilder.loadConstant(propertyPlan.getPropertyType());
            }

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
