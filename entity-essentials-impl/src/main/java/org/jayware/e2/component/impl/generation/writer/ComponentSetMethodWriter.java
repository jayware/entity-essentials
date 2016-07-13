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


import org.jayware.e2.component.api.ComponentFactoryException;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

import static org.jayware.e2.component.impl.generation.asm.MethodBuilder.createMethodBuilder;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.boxed;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBooleanPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBytePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isDoublePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isFloatPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isIntegerPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isLongPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isShortPrimitiveType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentSetMethodWriter
{
    public void writeSetMethodFor(ComponentGenerationPlan componentPlan)
    {
        final String classInternalName = componentPlan.getGeneratedClassInternalName();
        final ClassWriter classWriter = componentPlan.getClassWriter();

        final MethodBuilder builder = createMethodBuilder(classWriter, ACC_PUBLIC, "set", "(Ljava/lang/String;Ljava/lang/Object;)Z");
        builder.beginMethod();

        for (ComponentPropertyGenerationPlan propertyPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            final String propertyName = propertyPlan.getPropertyName();
            final Class<?> propertyType = propertyPlan.getPropertyType();

            final Label fail = new Label();

            builder.loadConstant(propertyName);
            builder.loadReferenceVariable(1);
            builder.invokeVirtualMethod(String.class, "equals", boolean.class, Object.class);
            builder.jumpIfEquals(fail);

            if (isObjectType(propertyType))
            {
                final Label endIfNull = new Label();
                final Label store = new Label();

                builder.loadThis();

                builder.loadVariable(2, Object.class);
                builder.jumpIfNull(endIfNull);

                builder.loadVariable(2, Object.class);
                builder.castTo(propertyType);
                builder.jumpTo(store);

                builder.label(endIfNull);
                builder.pushNull();

                builder.label(store);
                builder.storeField(classInternalName, propertyName, propertyType);
                builder.push_1i();
                builder.returnValue(boolean.class);
            }
            else if (isPrimitiveType(propertyType))
            {
                final Label notNull = new Label();

                builder.loadVariable(2, Object.class);
                builder.jumpIfNotNull(notNull);

                builder.loadThis();
                builder.push_0(propertyType);
                builder.storeField(classInternalName, propertyName, propertyType);
                builder.push_1i();
                builder.returnValue(boolean.class);

                builder.label(notNull);

                builder.loadConstant(boxed(propertyType));
                builder.loadVariable(2, Object.class);
                builder.invokeVirtualMethod(Object.class, "getClass", Class.class);
                builder.invokeVirtualMethod(Class.class, "equals", boolean.class, Object.class);
                builder.jumpIfEquals(fail);

                builder.loadThis();

                builder.loadVariable(2, Object.class);
                builder.castTo(boxed(propertyType));

                if (isBooleanPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Boolean.class, "booleanValue", boolean.class);
                }
                else if (isBytePrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Byte.class, "byteValue", byte.class);
                }
                else if (isShortPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Short.class, "shortValue", short.class);
                }
                else if (isIntegerPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Integer.class, "intValue", int.class);
                }
                else if (isLongPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Long.class, "longValue", long.class);
                }
                else if (isFloatPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Float.class, "floatValue", float.class);
                }
                else if (isDoublePrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Double.class, "doubleValue", double.class);
                }
                else
                {
                    throw new ComponentFactoryException();
                }

                builder.storeField(classInternalName, propertyName, propertyType);
                builder.push_1i();
                builder.returnValue(boolean.class);
            }
            else if (isObjectArrayType(propertyType))
            {
//                visitor.visitVarInsn(ALOAD, 2);
//                visitor.visitInsn(ICONST_1);
//                visitor.visitVarInsn(ALOAD, 2);
//                visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(String.class), "length", "()I", false);
//                visitor.visitInsn(ICONST_1);
//                visitor.visitInsn(ISUB);
//                visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(String.class), "substring", "(II)" + getDescriptor(String.class), false);
//                visitor.visitVarInsn(ASTORE, 2);
//                visitor.visitVarInsn(ALOAD, 2);
//                visitor.visitLdcInsn(", ");
//                visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(String.class), "split", "(" + getDescriptor(String.class) + ")[" + getDescriptor(String.class), false);
//                visitor.visitVarInsn(ASTORE, 3);
//                visitor.visitVarInsn(ALOAD, 3);
//                visitor.visitInsn(ARRAYLENGTH);
//                visitor.visitIntInsn(NEWARRAY, resolveOpcodePrimitiveType(propertyType.getComponentType()));
//                visitor.visitVarInsn(ASTORE, 4);
//                final Label endForLoop = new Label();
//                final Label headForLoop = new Label();
//                visitor.visitInsn(ICONST_0);
//                visitor.visitVarInsn(ISTORE, 5);
//                visitor.visitLabel(headForLoop);
//                visitor.visitVarInsn(ILOAD, 5);
//                visitor.visitVarInsn(ALOAD, 3);
//                visitor.visitInsn(ARRAYLENGTH);
//                visitor.visitJumpInsn(IF_ICMPGE, endForLoop);
//                visitor.visitVarInsn(ALOAD, 4);
//                visitor.visitVarInsn(ILOAD, 5);
//                visitor.visitVarInsn(ALOAD, 3);
//                visitor.visitVarInsn(ILOAD, 5);
//                visitor.visitInsn(AALOAD);
//
//                if (isBooleanPrimitiveArrayType(propertyType))
//                {
//                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Boolean.class), "parseBoolean", "(Ljava/lang/String;)Z", false);
//                }
//                else if (isBytePrimitiveArrayType(propertyType))
//                {
//                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Byte.class), "parseByte", "(Ljava/lang/String;)B", false);
//                }
//                else if (isShortPrimitiveArrayType(propertyType))
//                {
//                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Short.class), "parseShort", "(Ljava/lang/String;)S", false);
//                }
//                else if (isIntegerPrimitiveArrayType(propertyType))
//                {
//                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Integer.class), "parseInt", "(Ljava/lang/String;)I", false);
//                }
//                else if (isFloatPrimitiveArrayType(propertyType))
//                {
//                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Float.class), "parseFloat", "(Ljava/lang/String;)F", false);
//                }
//                else if (isDoublePrimitiveArrayType(propertyType))
//                {
//                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Double.class), "parseDouble", "(Ljava/lang/String;)D", false);
//                }
//
//                visitor.visitInsn(getType(propertyType.getComponentType()).getOpcode(IASTORE));
//
//                visitor.visitIincInsn(5, 1);
//                visitor.visitJumpInsn(GOTO, headForLoop);
//                visitor.visitLabel(endForLoop);
//                visitor.visitVarInsn(ALOAD, 4);
//                throw new ComponentFactoryException();
            }
            else if (isPrimitiveArrayType(propertyType))
            {
//                throw new ComponentFactoryException();
            }
            else
            {
                throw new ComponentFactoryException();
            }

            builder.label(fail);
        }

        builder.push_0i();
        builder.returnValue(boolean.class);
        builder.endMethod();
    }
}
