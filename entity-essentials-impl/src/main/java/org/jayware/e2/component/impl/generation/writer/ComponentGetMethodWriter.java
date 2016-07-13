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


import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
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
    public void writeGetMethodFor(ComponentGenerationPlan componentPlan)
    {
        final String classInternalName = componentPlan.getGeneratedClassInternalName();
        final ClassWriter classWriter = componentPlan.getClassWriter();
        final MethodVisitor visitor = classWriter.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);

        visitor.visitCode();

        for (ComponentPropertyGenerationPlan propertyPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            final String propertyName = propertyPlan.getPropertyName();
            final Class<?> propertyType = propertyPlan.getPropertyType();
            final String propertyTypeDescriptor = Type.getDescriptor(propertyPlan.getPropertyType());

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
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Boolean.class), "valueOf", "(Z)" + getDescriptor(Boolean.class), false);
                }
                else if (isBytePrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Byte.class), "valueOf", "(B)" + getDescriptor(Byte.class), false);
                }
                else if (isShortPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Short.class), "valueOf", "(S)" + getDescriptor(Short.class), false);
                }
                else if (isIntegerPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Integer.class), "valueOf", "(I)" + getDescriptor(Integer.class), false);
                }
                else if (isLongPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Long.class), "valueOf", "(J)" + getDescriptor(Long.class), false);
                }
                else if (isFloatPrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Float.class), "valueOf", "(F)" + getDescriptor(Float.class), false);
                }
                else if (isDoublePrimitiveType(propertyType))
                {
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Double.class), "valueOf", "(D)" + getDescriptor(Double.class), false);
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
