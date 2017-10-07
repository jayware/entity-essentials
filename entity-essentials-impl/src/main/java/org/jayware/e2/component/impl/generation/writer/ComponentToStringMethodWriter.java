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


import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.jayware.e2.component.impl.ComponentFactoryImpl.ComponentGenerationContext;
import org.jayware.e2.component.impl.generation.asm.TypeUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBytePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isShortPrimitiveType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;


public class ComponentToStringMethodWriter
{
    private static final String APPEND_METHOD_NAME = "append";
    private static final String STRING_BUILDER_INTERNAL_NAME = "java/lang/StringBuilder";
    private static final String STRING_BUILDER_APPEND_METHOD_DESCRIPTOR = "(Ljava/lang/String;)Ljava/lang/StringBuilder;";

    public void writeToStringMethodFor(ComponentGenerationContext generationContext, ComponentDescriptor descriptor)
    {
        final Class<? extends Component> componentClass = descriptor.getDeclaringComponent();
        final String classInternalName = generationContext.getGeneratedClassInternalName();
        final ClassWriter classWriter = generationContext.getClassWriter();

        final MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, STRING_BUILDER_INTERNAL_NAME);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, STRING_BUILDER_INTERNAL_NAME, "<init>", "()V", false);
        mv.visitLdcInsn(componentClass.getSimpleName() + "{");
        mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME, APPEND_METHOD_NAME, STRING_BUILDER_APPEND_METHOD_DESCRIPTOR, false);

        int progress = 0;
        final int numberOfProperties = descriptor.getPropertyDescriptors().size();
        for (ComponentPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
        {
            final String propertyName = propertyDescriptor.getPropertyName();
            final Class<?> propertyType = propertyDescriptor.getPropertyType();
            final String propertyTypeDescriptor = Type.getDescriptor(propertyDescriptor.getPropertyType());

            if (progress == 0)
            {
                mv.visitLdcInsn(propertyName + "='");
            }
            else
            {
                mv.visitLdcInsn("', " + propertyName + "='");
            }

            mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME, APPEND_METHOD_NAME, STRING_BUILDER_APPEND_METHOD_DESCRIPTOR, false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, classInternalName, propertyName, propertyTypeDescriptor);

            if (TypeUtil.isPrimitiveType(propertyType))
            {
                // StringBuilder does not support Short and Byte primitives.
                if (isShortPrimitiveType(propertyType) || isBytePrimitiveType(propertyType))
                {
                    mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME, APPEND_METHOD_NAME, "(I)Ljava/lang/StringBuilder;", false);
                }
                else
                {
                    mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME, APPEND_METHOD_NAME, "(" + propertyTypeDescriptor + ")Ljava/lang/StringBuilder;", false);
                }
            }
            else
            {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME, APPEND_METHOD_NAME, STRING_BUILDER_APPEND_METHOD_DESCRIPTOR, false);
            }

            if (progress == numberOfProperties - 1)
            {
                mv.visitIntInsn(BIPUSH, '\'');
                mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME, APPEND_METHOD_NAME, "(C)Ljava/lang/StringBuilder;", false);
            }

            ++progress;
        }

        mv.visitLdcInsn("}");
        mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME, APPEND_METHOD_NAME, STRING_BUILDER_APPEND_METHOD_DESCRIPTOR, false);
        mv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_INTERNAL_NAME, "toString", "()Ljava/lang/String;", false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
