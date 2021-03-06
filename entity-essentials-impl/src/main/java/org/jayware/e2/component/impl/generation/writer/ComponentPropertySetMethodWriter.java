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
import org.jayware.e2.component.api.ComponentProperty;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.jayware.e2.component.impl.ComponentFactoryImpl.ComponentGenerationContext;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentPropertySetMethodWriter
{
    public void writePropertySetMethod(ComponentGenerationContext generationContext, ComponentDescriptor descriptor)
    {
        final ClassWriter classWriter = generationContext.getClassWriter();

        final String propertyClassDescriptor = Type.getDescriptor(ComponentProperty.class);
        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_PUBLIC, "set", "("+ propertyClassDescriptor + "Ljava/lang/Object;)Z");

        methodBuilder.beginMethod();

        for (ComponentPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
        {
            final ComponentProperty property = propertyDescriptor.getProperty();
            final String propertyName = propertyDescriptor.getPropertyName();
            final Label endIfPropertyEquals = new Label();

            if (property != null)
            {
                methodBuilder.loadStaticField(property.component, propertyName, ComponentProperty.class);
                methodBuilder.loadReferenceVariable(1);
                methodBuilder.invokeVirtualMethod(Object.class, "equals", boolean.class, Object.class);
                methodBuilder.jumpIfEquals(endIfPropertyEquals);
                methodBuilder.loadThis();
                methodBuilder.loadConstant(propertyName);
                methodBuilder.loadReferenceVariable(2);
                methodBuilder.invokeVirtualMethod(AbstractComponent.class, "set", boolean.class, String.class, Object.class);
                methodBuilder.pushTrue();
                methodBuilder.returnValue(boolean.class);
                methodBuilder.label(endIfPropertyEquals);
            }
        }

        methodBuilder.pushFalse();
        methodBuilder.returnValue(boolean.class);
        methodBuilder.endMethod();
    }
}
