/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2016 Elmar Schug <elmar.schug@jayware.org>,
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

import com.google.common.base.Objects;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
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
        methodBuilder.custom().visitTypeInsn(INSTANCEOF, generatedClassInternalName);
        methodBuilder.jumpIfNotEquals(endIfInstanceOf);
        methodBuilder.push_0i();
        methodBuilder.returnValue(boolean.class);

        methodBuilder.label(endIfInstanceOf);
        methodBuilder.loadReferenceVariable(1);
        methodBuilder.castTo(generatedClassInternalName);
        methodBuilder.storeVariable(2, componentPlan.getComponentType());

        for (ComponentPropertyGenerationPlan propertyGenerationPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            final Class propertyType = propertyGenerationPlan.getPropertyType();

            methodBuilder.loadThis();
            methodBuilder.loadField(generatedClassInternalName, propertyGenerationPlan.getPropertyName(), propertyType);

            methodBuilder.loadReferenceVariable(2);
            methodBuilder.loadField(generatedClassInternalName, propertyGenerationPlan.getPropertyName(), propertyType);

            if (isObjectType(propertyType))
            {
                methodBuilder.invokeStaticMethod(Objects.class, "equal", boolean.class, Object.class, Object.class);
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
