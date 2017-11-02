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
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.jayware.e2.component.impl.ComponentFactoryImpl.ComponentGenerationContext;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

import static org.jayware.e2.component.impl.generation.asm.TypeUtil.boxed;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;


public class ComponentCopyOtherMethodWriter
{
    private static final int COMPONENT_ARG = 1;
    private static final int CASTED_COMPONENT_ARG = 2;
    private static final int PROPERTY_VALUE = 3;

    public void writeCopyOtherMethodFor(ComponentGenerationContext generationContext, ComponentDescriptor descriptor)
    {
        final Class<? extends Component> componentClass = descriptor.getDeclaringComponent();
        final ClassWriter classWriter = generationContext.getClassWriter();

        final MethodBuilder methodBuilder = MethodBuilder.createMethodBuilder(classWriter, ACC_PUBLIC, "copy", "(" + getDescriptor(Component.class) + ")" + getDescriptor(Component.class), null, null);
        final Label endIfNotInstanceOfComponent = new Label();
        final Label endIfNotInstanceOfAbstractComponent = new Label();
        methodBuilder.beginMethod();

        methodBuilder.loadReferenceVariable(COMPONENT_ARG);
        methodBuilder.dup();
        methodBuilder.custom().visitTypeInsn(INSTANCEOF, getInternalName(componentClass));
        methodBuilder.jumpIfNotEquals(endIfNotInstanceOfComponent);
        methodBuilder.newInstanceOf(IllegalArgumentException.class);
        methodBuilder.dup();
        methodBuilder.loadConstant("Copy source type has to implement: " + componentClass.getName());
        methodBuilder.invokeConstructor(IllegalArgumentException.class, String.class);
        methodBuilder.throwException();
        methodBuilder.label(endIfNotInstanceOfComponent);

        methodBuilder.loadReferenceVariable(COMPONENT_ARG);
        methodBuilder.custom().visitTypeInsn(INSTANCEOF, getInternalName(AbstractComponent.class));
        methodBuilder.jumpIfNotEquals(endIfNotInstanceOfAbstractComponent);
        methodBuilder.newInstanceOf(IllegalArgumentException.class);
        methodBuilder.dup();
        methodBuilder.loadConstant("Copy source type has to implement: " + AbstractComponent.class.getName());
        methodBuilder.invokeConstructor(IllegalArgumentException.class, String.class);
        methodBuilder.throwException();
        methodBuilder.label(endIfNotInstanceOfAbstractComponent);

        methodBuilder.castTo(AbstractComponent.class);
        methodBuilder.storeReferenceVariable(CASTED_COMPONENT_ARG);

        for (ComponentPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
        {
            final String propertyName = propertyDescriptor.getPropertyName();
            final Class propertyType = propertyDescriptor.getPropertyType();

            methodBuilder.loadReferenceVariable(CASTED_COMPONENT_ARG);
            methodBuilder.loadConstant(propertyName);
            methodBuilder.invokeVirtualMethod(AbstractComponent.class, "get", Object.class, String.class);

            if (isPrimitiveType(propertyType))
            {
                methodBuilder.castTo(boxed(propertyType));
            }

            methodBuilder.storeVariable(PROPERTY_VALUE, boxed(propertyType));
            methodBuilder.loadThis();
            methodBuilder.loadConstant(propertyName);
            methodBuilder.loadVariable(PROPERTY_VALUE, boxed(propertyType));

            methodBuilder.invokeVirtualMethod(AbstractComponent.class, "set", boolean.class, String.class, Object.class);
        }

        methodBuilder.loadThis();
        methodBuilder.returnReference();
        methodBuilder.endMethod();
    }
}
