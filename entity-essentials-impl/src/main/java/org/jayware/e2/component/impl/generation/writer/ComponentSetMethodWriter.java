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
import org.jayware.e2.component.api.ComponentFactoryException;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.ComponentPropertyAdapter;
import org.jayware.e2.component.api.ComponentUnmarshalException;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.jayware.e2.context.api.Context;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.jayware.e2.component.impl.generation.asm.TypeUtil.resolveOpcodePrimitiveType;
import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IASTORE;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;
import static org.objectweb.asm.Type.getType;


public class ComponentSetMethodWriter
{
    public void writeSetMethodFor(ComponentGenerationPlan componentPlan)
    {
        final String classInternalName = componentPlan.getGeneratedClassInternalName();
        final ClassWriter classWriter = componentPlan.getClassWriter();

        final MethodVisitor visitor = classWriter.visitMethod(ACC_PUBLIC, "set", "(Ljava/lang/String;Ljava/lang/String;)Z", null, null);
        visitor.visitCode();

        for (ComponentPropertyGenerationPlan propertyPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            final String propertyName = propertyPlan.getPropertyName();
            final Class<?> propertyType = propertyPlan.getPropertyType();

            final Label endIfPropertyNameEqualsLabel = new Label();

            visitor.visitLdcInsn(propertyName);
            visitor.visitVarInsn(ALOAD, 1);
            visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(String.class), "equals", "(Ljava/lang/Object;)Z", false);
            visitor.visitJumpInsn(IFEQ, endIfPropertyNameEqualsLabel);
            visitor.visitVarInsn(ALOAD, 0);

            if (propertyType.isPrimitive() || propertyType.isArray() && propertyType.getComponentType().isPrimitive())
            {
                if (!propertyType.isArray())
                {
                    visitor.visitVarInsn(ALOAD, 2);

                    if (boolean.class.equals(propertyType))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Boolean.class), "parseBoolean", "(Ljava/lang/String;)Z", false);
                    }
                    else if (byte.class.equals(propertyType))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Byte.class), "parseByte", "(Ljava/lang/String;)B", false);
                    }
                    else if (short.class.equals(propertyType))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Short.class), "parseShort", "(Ljava/lang/String;)S", false);
                    }
                    else if (int.class.equals(propertyType))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Integer.class), "parseInt", "(Ljava/lang/String;)I", false);
                    }
                    else if (float.class.equals(propertyType))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Float.class), "parseFloat", "(Ljava/lang/String;)F", false);
                    }
                    else if (double.class.equals(propertyType))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Double.class), "parseDouble", "(Ljava/lang/String;)D", false);
                    }
                }
                else
                {
                    visitor.visitVarInsn(ALOAD, 2);
                    visitor.visitInsn(ICONST_1);
                    visitor.visitVarInsn(ALOAD, 2);
                    visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(String.class), "length", "()I", false);
                    visitor.visitInsn(ICONST_1);
                    visitor.visitInsn(ISUB);
                    visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(String.class), "substring", "(II)" + getDescriptor(String.class), false);
                    visitor.visitVarInsn(ASTORE, 2);
                    visitor.visitVarInsn(ALOAD, 2);
                    visitor.visitLdcInsn(", ");
                    visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(String.class), "split", "(" + getDescriptor(String.class) + ")[" + getDescriptor(String.class), false);
                    visitor.visitVarInsn(ASTORE, 3);
                    visitor.visitVarInsn(ALOAD, 3);
                    visitor.visitInsn(ARRAYLENGTH);
                    visitor.visitIntInsn(NEWARRAY, resolveOpcodePrimitiveType(propertyType.getComponentType()));
                    visitor.visitVarInsn(ASTORE, 4);
                    final Label endForLoop = new Label();
                    final Label headForLoop = new Label();
                    visitor.visitInsn(ICONST_0);
                    visitor.visitVarInsn(ISTORE, 5);
                    visitor.visitLabel(headForLoop);
                    visitor.visitVarInsn(ILOAD, 5);
                    visitor.visitVarInsn(ALOAD, 3);
                    visitor.visitInsn(ARRAYLENGTH);
                    visitor.visitJumpInsn(IF_ICMPGE, endForLoop);
                    visitor.visitVarInsn(ALOAD, 4);
                    visitor.visitVarInsn(ILOAD, 5);
                    visitor.visitVarInsn(ALOAD, 3);
                    visitor.visitVarInsn(ILOAD, 5);
                    visitor.visitInsn(AALOAD);

                    if (boolean.class.equals(propertyType.getComponentType()))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Boolean.class), "parseBoolean", "(Ljava/lang/String;)Z", false);
                    }
                    else if (byte.class.equals(propertyType.getComponentType()))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Byte.class), "parseByte", "(Ljava/lang/String;)B", false);
                    }
                    else if (short.class.equals(propertyType.getComponentType()))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Short.class), "parseShort", "(Ljava/lang/String;)S", false);
                    }
                    else if (int.class.equals(propertyType.getComponentType()))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Integer.class), "parseInt", "(Ljava/lang/String;)I", false);
                    }
                    else if (float.class.equals(propertyType.getComponentType()))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Float.class), "parseFloat", "(Ljava/lang/String;)F", false);
                    }
                    else if (double.class.equals(propertyType.getComponentType()))
                    {
                        visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Double.class), "parseDouble", "(Ljava/lang/String;)D", false);
                    }

                    visitor.visitInsn(getType(propertyType.getComponentType()).getOpcode(IASTORE));

                    visitor.visitIincInsn(5, 1);
                    visitor.visitJumpInsn(GOTO, headForLoop);
                    visitor.visitLabel(endForLoop);
                    visitor.visitVarInsn(ALOAD, 4);
                }
            }
            else if (propertyType.isEnum())
            {
                final Label ifEnumValueNonNull = new Label();
                final Label endIf = new Label();
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitJumpInsn(IFNONNULL, ifEnumValueNonNull);
                visitor.visitInsn(ACONST_NULL);
                visitor.visitJumpInsn(GOTO, endIf);
                visitor.visitLabel(ifEnumValueNonNull);
                visitor.visitLdcInsn(getType(propertyType));
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Enum.class), "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
                visitor.visitTypeInsn(CHECKCAST, getInternalName(propertyType));
                visitor.visitLabel(endIf);
            }
            else if (String.class.equals(propertyType))
            {
                visitor.visitVarInsn(ALOAD, 2);
            }
            else if (!propertyType.isPrimitive() || propertyType.isArray() && !propertyType.getComponentType().isPrimitive())
            {
                final Label endIfAdapterNull = new Label();
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, getInternalName(AbstractComponent.class), "myComponentManager", getDescriptor(ComponentManager.class));
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, getInternalName(AbstractComponent.class), "myContext", getDescriptor(Context.class));
                visitor.visitLdcInsn(getType(propertyType));
                visitor.visitMethodInsn(INVOKEINTERFACE, getInternalName(ComponentManager.class), "getPropertyAdapter", "(" + getDescriptor(Context.class) + getDescriptor(Class.class) + ")" + getDescriptor(ComponentPropertyAdapter.class), true);
                visitor.visitVarInsn(ASTORE, 3);
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitJumpInsn(IFNONNULL, endIfAdapterNull);
                visitor.visitTypeInsn(NEW, getInternalName(ComponentUnmarshalException.class));
                visitor.visitInsn(DUP);
                visitor.visitLdcInsn("No property adapter found for property '" + propertyName + "' of type '" + propertyType.getName() + "'");
                visitor.visitMethodInsn(INVOKESPECIAL, getInternalName(ComponentUnmarshalException.class), "<init>", "(" + getDescriptor(String.class) + ")V", false);
                visitor.visitInsn(ATHROW);
                visitor.visitLabel(endIfAdapterNull);
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, getInternalName(AbstractComponent.class), "myContext", getDescriptor(Context.class));
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitMethodInsn(INVOKEINTERFACE, getInternalName(ComponentPropertyAdapter.class), "unmarshal", "(" + getDescriptor(Context.class) + getDescriptor(String.class) + ")" + getDescriptor(Object.class), true);
                visitor.visitTypeInsn(CHECKCAST, getType(propertyType).getInternalName());
            }
            else
            {
                throw new ComponentFactoryException();
            }

            visitor.visitFieldInsn(PUTFIELD, classInternalName, propertyName, Type.getDescriptor(propertyPlan.getPropertyType()));
            visitor.visitInsn(ICONST_1);
            visitor.visitInsn(IRETURN);
            visitor.visitLabel(endIfPropertyNameEqualsLabel);
        }

        visitor.visitInsn(ICONST_0);
        visitor.visitInsn(IRETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }
}
