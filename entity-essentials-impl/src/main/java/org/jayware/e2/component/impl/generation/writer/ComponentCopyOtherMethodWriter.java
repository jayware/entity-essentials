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


import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.jayware.e2.component.impl.generation.asm.TypeUtil.resolveOpcodePrimitiveType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IALOAD;
import static org.objectweb.asm.Opcodes.IASTORE;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;
import static org.objectweb.asm.Type.getType;


public class ComponentCopyOtherMethodWriter
{
    public void writeCopyOtherMethodFor(ComponentGenerationPlan componentPlan)
    {
        final Class<? extends Component> componentClass = componentPlan.getComponentType();
        final ClassWriter classWriter = componentPlan.getClassWriter();

        final MethodVisitor visitor = classWriter.visitMethod(ACC_PUBLIC, "copy", "(" + getDescriptor(Component.class) + ")" + getDescriptor(Component.class), null, null);
        final Label endIfNotInstanceOf = new Label();
        visitor.visitCode();
        visitor.visitVarInsn(ALOAD, 1);
        visitor.visitInsn(DUP);
        visitor.visitTypeInsn(INSTANCEOF, getInternalName(componentClass));
        visitor.visitJumpInsn(IFNE, endIfNotInstanceOf);
        visitor.visitTypeInsn(NEW, getInternalName(IllegalArgumentException.class));
        visitor.visitInsn(DUP);
        visitor.visitLdcInsn("Copy source type has to be: " + componentClass);
        visitor.visitMethodInsn(INVOKESPECIAL, getInternalName(IllegalArgumentException.class), "<init>", "(" + getDescriptor(String.class) + ")V", false);
        visitor.visitInsn(ATHROW);
        visitor.visitLabel(endIfNotInstanceOf);
        visitor.visitTypeInsn(CHECKCAST, getInternalName(componentClass));
        visitor.visitVarInsn(ASTORE, 2);
        for (ComponentPropertyGenerationPlan propertyPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            final String propertyName = propertyPlan.getPropertyName();
            final Class<?> propertyType = propertyPlan.getPropertyType();
            final String propertyTypeDescriptor = Type.getDescriptor(propertyPlan.getPropertyType());

            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitVarInsn(ALOAD, 2);
            visitor.visitMethodInsn(INVOKEINTERFACE, getInternalName(componentClass), propertyPlan.getPropertyGetterMethodName(), propertyPlan.getPropertyGetterMethodDescriptor(), true);

            final Label ifNotNull = new Label();
            final Label endIfNotNull = new Label();

            if (propertyType.isArray())
            {
                visitor.visitVarInsn(ASTORE, 3);
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitJumpInsn(IFNONNULL, ifNotNull);
                visitor.visitInsn(ACONST_NULL);
                visitor.visitJumpInsn(GOTO, endIfNotNull);
                visitor.visitLabel(ifNotNull);
                visitor.visitVarInsn(ALOAD, 3);
                visitor.visitInsn(ARRAYLENGTH);

                if (propertyType.getComponentType().isPrimitive())
                {
                    visitor.visitIntInsn(NEWARRAY, resolveOpcodePrimitiveType(propertyType.getComponentType()));
                }
                else
                {
                    visitor.visitTypeInsn(ANEWARRAY, getInternalName(propertyType.getComponentType()));
                }

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
                visitor.visitInsn(getType(propertyType.getComponentType()).getOpcode(IALOAD));
                visitor.visitInsn(getType(propertyType.getComponentType()).getOpcode(IASTORE));
                visitor.visitIincInsn(5, 1);
                visitor.visitJumpInsn(GOTO, headForLoop);
                visitor.visitLabel(endForLoop);
                visitor.visitVarInsn(ALOAD, 4);
            }

            visitor.visitLabel(endIfNotNull);
            visitor.visitFieldInsn(PUTFIELD, componentPlan.getGeneratedClassInternalName(), propertyName, propertyTypeDescriptor);
        }
        visitor.visitVarInsn(ALOAD, 0);
        visitor.visitInsn(ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }
}
