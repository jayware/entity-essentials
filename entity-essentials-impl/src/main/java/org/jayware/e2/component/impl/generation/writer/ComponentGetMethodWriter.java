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


import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.jayware.e2.component.impl.ComponentFactoryImpl.ComponentGenerationContext;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBooleanPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBytePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isDoublePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isFloatPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isIntegerPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isLongPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isShortPrimitiveType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;


public class ComponentGetMethodWriter
{

    private static final String VALUEOF_METHOD_NAME = "valueOf";

    public void writeGetMethodFor(ComponentGenerationContext generationContext, ComponentDescriptor descriptor)
    {
        final String classInternalName = generationContext.getGeneratedClassInternalName();
        final ClassWriter classWriter = generationContext.getClassWriter();
        final MethodVisitor visitor = classWriter.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);

        visitor.visitCode();

        for (ComponentPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
        {
            final String propertyName = propertyDescriptor.getPropertyName();
            final Class<?> propertyType = propertyDescriptor.getPropertyType();
            final String propertyTypeDescriptor = Type.getDescriptor(propertyDescriptor.getPropertyType());

            final Label endIfPropertyNameEqualsLabel = new Label();

            visitor.visitLdcInsn(propertyName);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(String.class), "equals", "(Ljava/lang/Object;)Z", false);
            visitor.visitJumpInsn(IFEQ, endIfPropertyNameEqualsLabel);

            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD, classInternalName, propertyName, propertyTypeDescriptor);

            if (isPrimitiveType(propertyType))
            {
                if (isBooleanPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Boolean.class), VALUEOF_METHOD_NAME, "(Z)" + getDescriptor(Boolean.class), false);
                }
                else if (isBytePrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Byte.class), VALUEOF_METHOD_NAME, "(B)" + getDescriptor(Byte.class), false);
                }
                else if (isShortPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Short.class), VALUEOF_METHOD_NAME, "(S)" + getDescriptor(Short.class), false);
                }
                else if (isIntegerPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Integer.class), VALUEOF_METHOD_NAME, "(I)" + getDescriptor(Integer.class), false);
                }
                else if (isLongPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Long.class), VALUEOF_METHOD_NAME, "(J)" + getDescriptor(Long.class), false);
                }
                else if (isFloatPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Float.class), VALUEOF_METHOD_NAME, "(F)" + getDescriptor(Float.class), false);
                }
                else if (isDoublePrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Double.class), VALUEOF_METHOD_NAME, "(D)" + getDescriptor(Double.class), false);
                }
            }

            visitor.visitInsn(ARETURN);
            visitor.visitLabel(endIfPropertyNameEqualsLabel);
        }

        visitor.visitInsn(ACONST_NULL);
        visitor.visitInsn(ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }
}
