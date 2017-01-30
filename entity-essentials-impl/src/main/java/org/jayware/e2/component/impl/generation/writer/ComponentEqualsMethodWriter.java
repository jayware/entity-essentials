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
import org.jayware.e2.util.ObjectUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

import java.util.Arrays;

import static org.jayware.e2.component.impl.generation.asm.MethodBuilder.createMethodBuilder;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBooleanPrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBytePrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isDoublePrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isDoublePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isFloatPrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isFloatPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isIntegerPrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isLongPrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isLongPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isShortPrimitiveArrayType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.DCMPL;
import static org.objectweb.asm.Opcodes.FCMPL;
import static org.objectweb.asm.Opcodes.IF_ACMPNE;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.LCMP;


public class ComponentEqualsMethodWriter
{
    public void writeEqualsMethodFor(ComponentGenerationPlan componentPlan)
    {
        final ClassWriter classWriter = componentPlan.getClassWriter();
        final MethodBuilder methodBuilder = createMethodBuilder(classWriter, ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z");
        final String componentTypeInternalName = componentPlan.getComponentTypeInternalName();
        final String generatedClassInternalName = componentPlan.getGeneratedClassInternalName();

        methodBuilder.beginMethod();

        methodBuilder.loadThis();
        methodBuilder.loadReferenceVariable(1);

        final Label endIfSameObject = new Label();
        final Label endIfInstanceOf = new Label();
        final Label endIf = new Label();
        methodBuilder.custom().visitJumpInsn(IF_ACMPNE, endIfSameObject);
        methodBuilder.push_1i();
        methodBuilder.returnValue(boolean.class);

        methodBuilder.label(endIfSameObject);
        methodBuilder.loadReferenceVariable(1);
        methodBuilder.custom().visitTypeInsn(INSTANCEOF, componentTypeInternalName);
        methodBuilder.jumpIfNotEquals(endIfInstanceOf);
        methodBuilder.push_0i();
        methodBuilder.returnValue(boolean.class);

        methodBuilder.label(endIfInstanceOf);
        methodBuilder.loadReferenceVariable(1);
        methodBuilder.castTo(componentTypeInternalName);
        methodBuilder.storeVariable(2, componentPlan.getComponentType());

        for (ComponentPropertyGenerationPlan propertyGenerationPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            final Class propertyType = propertyGenerationPlan.getPropertyType();

            methodBuilder.loadThis();
            methodBuilder.loadField(generatedClassInternalName, propertyGenerationPlan.getPropertyName(), propertyType);

            methodBuilder.loadReferenceVariable(2);
            methodBuilder.invokeInterfaceMethod(componentTypeInternalName, propertyGenerationPlan.getPropertyGetterMethodName(), propertyGenerationPlan.getPropertyType());

            if (isObjectType(propertyType))
            {
                methodBuilder.invokeStaticMethod(ObjectUtil.class, "equal", boolean.class, Object.class, Object.class);
                methodBuilder.jumpIfEquals(endIf);
            }
            else if (isArrayType(propertyType))
            {
                if (isObjectArrayType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Arrays.class, "equals", boolean.class, Object[].class, Object[].class);
                }
                else if (isBooleanPrimitiveArrayType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Arrays.class, "equals", boolean.class, boolean[].class, boolean[].class);
                }
                else if (isBytePrimitiveArrayType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Arrays.class, "equals", boolean.class, byte[].class, byte[].class);
                }
                else if (isShortPrimitiveArrayType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Arrays.class, "equals", boolean.class, short[].class, short[].class);
                }
                else if (isIntegerPrimitiveArrayType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Arrays.class, "equals", boolean.class, int[].class, int[].class);
                }
                else if (isLongPrimitiveArrayType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Arrays.class, "equals", boolean.class, long[].class, long[].class);
                }
                else if (isFloatPrimitiveArrayType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Arrays.class, "equals", boolean.class, float[].class, float[].class);
                }
                else if (isDoublePrimitiveArrayType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Arrays.class, "equals", boolean.class, double[].class, double[].class);
                }

                methodBuilder.jumpIfEquals(endIf);
            }
            else
            {
                if (isFloatPrimitiveType(propertyType))
                {
                    methodBuilder.custom().visitInsn(FCMPL);
                    methodBuilder.jumpIfNotEquals(endIf);
                }
                else if (isDoublePrimitiveType(propertyType))
                {
                    methodBuilder.custom().visitInsn(DCMPL);
                    methodBuilder.jumpIfNotEquals(endIf);
                }
                else if (isLongPrimitiveType(propertyType))
                {
                    methodBuilder.custom().visitInsn(LCMP);
                    methodBuilder.jumpIfNotEquals(endIf);
                }
                else
                {
                    methodBuilder.custom().visitJumpInsn(IF_ICMPNE, endIf);
                }
            }
        }

        methodBuilder.push_1i();
        methodBuilder.returnValue(boolean.class);

        methodBuilder.label(endIf);
        methodBuilder.push_0i();
        methodBuilder.returnValue(boolean.class);
        methodBuilder.endMethod();
    }
}
