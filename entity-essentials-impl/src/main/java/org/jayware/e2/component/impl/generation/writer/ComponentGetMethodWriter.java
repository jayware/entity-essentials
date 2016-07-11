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
import org.jayware.e2.component.api.ComponentMarshalException;
import org.jayware.e2.component.api.ComponentPropertyAdapter;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.jayware.e2.context.api.Context;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Arrays;

import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isStringType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;
import static org.objectweb.asm.Type.getType;


public class ComponentGetMethodWriter
{
    public void writeGetMethodFor(ComponentGenerationPlan componentPlan)
    {
        final String classInternalName = componentPlan.getGeneratedClassInternalName();
        final ClassWriter classWriter = componentPlan.getClassWriter();
        final MethodVisitor visitor = classWriter.visitMethod(ACC_PUBLIC, "get", "(Ljava/lang/String;)Ljava/lang/String;", null, null);

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

            if (isPrimitiveType(propertyType) || isPrimitiveArrayType(propertyType))
            {
                if (propertyType.isArray())
                {
                    visitor.visitVarInsn(ALOAD, 0);
                    visitor.visitFieldInsn(GETFIELD, classInternalName, propertyName, propertyTypeDescriptor);
                    visitor.visitMethodInsn(INVOKESTATIC, getInternalName(Arrays.class), "toString", "(" + propertyTypeDescriptor + ")" + getDescriptor(String.class), false);
                }
                else
                {
                    visitor.visitVarInsn(ALOAD, 0);
                    visitor.visitFieldInsn(GETFIELD, classInternalName, propertyName, propertyTypeDescriptor);
                    visitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + propertyTypeDescriptor + ")Ljava/lang/String;", false);
                }
            }
            else if (propertyType.isEnum())
            {
                final Label ifEnumValueNonNull = new Label();
                final Label endIf = new Label();
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, classInternalName, propertyName, propertyTypeDescriptor);
                visitor.visitVarInsn(ASTORE, 2);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitJumpInsn(IFNONNULL, ifEnumValueNonNull);
                visitor.visitInsn(ACONST_NULL);
                visitor.visitJumpInsn(GOTO, endIf);
                visitor.visitLabel(ifEnumValueNonNull);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitMethodInsn(INVOKEVIRTUAL, getInternalName(Enum.class), "name", "()Ljava/lang/String;",false);
                visitor.visitLabel(endIf);
            }
            else if (isStringType(propertyType))
            {
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, classInternalName, propertyName, propertyTypeDescriptor);
            }
            else if (isObjectType(propertyType) || isObjectArrayType(propertyType))
            {
                final Label endIfAdapterNull = new Label();
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, getInternalName(AbstractComponent.class), "myComponentManager", getDescriptor(ComponentManager.class));
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, getInternalName(AbstractComponent.class), "myContext", getDescriptor(Context.class));
                visitor.visitLdcInsn(getType(propertyType));
                visitor.visitMethodInsn(INVOKEINTERFACE, getInternalName(ComponentManager.class), "getPropertyAdapter", "(" + getDescriptor(Context.class) + getDescriptor(Class.class) + ")" + getDescriptor(ComponentPropertyAdapter.class), true);
                visitor.visitVarInsn(ASTORE, 2);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitJumpInsn(IFNONNULL, endIfAdapterNull);
                visitor.visitTypeInsn(NEW, getInternalName(ComponentMarshalException.class));
                visitor.visitInsn(DUP);
                visitor.visitLdcInsn("No property adapter found for property '" + propertyName + "' of type '" + propertyType.getName() + "'");
                visitor.visitMethodInsn(INVOKESPECIAL, getInternalName(ComponentMarshalException.class), "<init>", "(" + getDescriptor(String.class) + ")V", false);
                visitor.visitInsn(ATHROW);
                visitor.visitLabel(endIfAdapterNull);
                visitor.visitVarInsn(ALOAD, 2);
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, getInternalName(AbstractComponent.class), "myContext", getDescriptor(Context.class));
                visitor.visitVarInsn(ALOAD, 0);
                visitor.visitFieldInsn(GETFIELD, classInternalName, propertyName, propertyTypeDescriptor);
                visitor.visitMethodInsn(INVOKEINTERFACE, getInternalName(ComponentPropertyAdapter.class), "marshal", "(" + getDescriptor(Context.class) + getDescriptor(Object.class) + ")" + getDescriptor(String.class), true);
            }
            else
            {
                throw new ComponentFactoryException();
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
