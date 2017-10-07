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
import org.jayware.e2.component.impl.ComponentFactoryImpl.ComponentGenerationContext;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Type.getType;


public class ComponentTypeMethodWriter
{
    public void writeTypeMethodFor(ComponentGenerationContext generationContext, ComponentDescriptor descriptor)
    {
        final ClassWriter classWriter = generationContext.getClassWriter();
        final MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "type", "()Ljava/lang/Class;", null, null);

        mv.visitCode();
        mv.visitLdcInsn(getType(descriptor.getDeclaringComponent()));
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
