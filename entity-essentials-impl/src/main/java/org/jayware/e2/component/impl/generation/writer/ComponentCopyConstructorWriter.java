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


import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.impl.ComponentFactoryImpl;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.context.api.Context;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentCopyConstructorWriter
{
    public void writeCopyConstructorFor(ComponentFactoryImpl.ComponentGenerationContext generationContext)
    {
        final ClassWriter classWriter = generationContext.getClassWriter();

        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_PUBLIC, "<init>", "(L" + generationContext.getGeneratedClassInternalName() + ";)V");
        methodBuilder.beginMethod();
        methodBuilder.loadThis();
        methodBuilder.duplicateTopStackElement();
        methodBuilder.loadReferenceVariable(1);
        methodBuilder.loadField(AbstractComponent.class, "myContext", Context.class);
        methodBuilder.invokeConstructor(AbstractComponent.class, Context.class);
        methodBuilder.loadReferenceVariable(1);
        methodBuilder.invokeVirtualMethod(generationContext.getGeneratedClassInternalName(), "copy", Component.class, Component.class);
        methodBuilder.returnVoid();
        methodBuilder.endMethod();
    }
}
