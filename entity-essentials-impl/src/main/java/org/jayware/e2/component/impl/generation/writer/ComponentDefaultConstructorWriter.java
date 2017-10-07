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
import org.jayware.e2.component.impl.ComponentFactoryImpl.ComponentGenerationContext;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.context.api.Context;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentDefaultConstructorWriter
{
    public void writeDefaultConstructorFor(ComponentGenerationContext generationContext)
    {
        final ClassWriter classWriter = generationContext.getClassWriter();

        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_PUBLIC, "<init>", "(" + Type.getDescriptor(Context.class) + ")V");
        methodBuilder.beginMethod();
        methodBuilder.loadThis();
        methodBuilder.loadReferenceVariable(1);
        methodBuilder.invokeConstructor(AbstractComponent.class, Context.class);
        methodBuilder.returnVoid();
        methodBuilder.endMethod();
    }
}
