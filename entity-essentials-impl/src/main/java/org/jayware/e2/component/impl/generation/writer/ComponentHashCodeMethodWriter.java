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
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.jayware.e2.component.impl.ComponentFactoryImpl.ComponentGenerationContext;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.util.ObjectUtil;

import java.util.Collection;

import static org.jayware.e2.component.impl.generation.asm.MethodBuilder.createMethodBuilder;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBooleanPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBytePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isDoublePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isFloatPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isIntegerPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isLongPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isShortPrimitiveType;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentHashCodeMethodWriter
{

    public static final String VALUEOF_METHOD_NAME = "valueOf";

    public void writeHashCodeMethodFor(ComponentGenerationContext generationContext, ComponentDescriptor descriptor)
    {
        final MethodBuilder methodBuilder = createMethodBuilder(generationContext.getClassWriter(),
            ACC_PUBLIC, "hashCode", "()I"
        );

        methodBuilder.beginMethod();

        final Collection<ComponentPropertyDescriptor> propertyGenerationPlans = descriptor.getPropertyDescriptors();

        methodBuilder.pushConstantValue(propertyGenerationPlans.size());
        methodBuilder.newArray(Object.class);

        int index = 0;
        for (ComponentPropertyDescriptor propertyDescriptor : propertyGenerationPlans)
        {
            final String propertyName = propertyDescriptor.getPropertyName();
            final Class propertyType = propertyDescriptor.getPropertyType();

            methodBuilder.dup();
            methodBuilder.pushConstantValue(index++);

            methodBuilder.loadThis();
            methodBuilder.loadField(generationContext.getGeneratedClassInternalName(), propertyName, propertyType);

            if (isPrimitiveType(propertyType))
            {
                if (isBooleanPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Boolean.class, VALUEOF_METHOD_NAME, Boolean.class, boolean.class);
                }
                else if (isBytePrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Byte.class, VALUEOF_METHOD_NAME, Byte.class, byte.class);
                }
                else if (isShortPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Short.class, VALUEOF_METHOD_NAME, Short.class, short.class);
                }
                else if (isIntegerPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Integer.class, VALUEOF_METHOD_NAME, Integer.class, int.class);
                }
                else if (isLongPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Long.class, VALUEOF_METHOD_NAME, Long.class, long.class);
                }
                else if (isFloatPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Float.class, VALUEOF_METHOD_NAME, Float.class, float.class);
                }
                else if (isDoublePrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Double.class, VALUEOF_METHOD_NAME, Double.class, double.class);
                }
            }

            methodBuilder.custom().visitInsn(AASTORE);
        }

        methodBuilder.invokeStaticMethod(ObjectUtil.class, "hashCode", int.class, Object[].class);
        methodBuilder.returnValue(int.class);
        methodBuilder.endMethod();
    }
}
