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


import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentPropertyGetterMethodWriter
{
    public void writePropertyGetterFor(ComponentPropertyGenerationPlan plan)
    {
        final ComponentGenerationPlan componentGenerationPlan = plan.getComponentGenerationPlan();
        final ClassWriter classWriter = componentGenerationPlan.getClassWriter();

        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_PUBLIC, plan.getPropertyGetterMethodName(), plan.getPropertyGetterMethodDescriptor());

        methodBuilder.beginMethod();
        methodBuilder.loadThis();
        methodBuilder.loadField(componentGenerationPlan.getGeneratedClassInternalName(), plan.getPropertyName(), plan.getPropertyType());
        methodBuilder.returnValue(plan.getPropertyType());
        methodBuilder.endMethod();
    }
}
