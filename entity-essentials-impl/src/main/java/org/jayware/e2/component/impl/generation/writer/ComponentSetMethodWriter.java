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


import org.jayware.e2.component.api.ComponentFactoryException;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;

import static org.jayware.e2.component.impl.generation.asm.MethodBuilder.createMethodBuilder;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.boxed;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBooleanPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBytePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isDoublePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isFloatPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isIntegerPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isLongPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isShortPrimitiveType;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


public class ComponentSetMethodWriter
{
    public void writeSetMethodFor(ComponentGenerationPlan componentPlan)
    {
        final String classInternalName = componentPlan.getGeneratedClassInternalName();
        final ClassWriter classWriter = componentPlan.getClassWriter();

        final MethodBuilder builder = createMethodBuilder(classWriter, ACC_PUBLIC, "set", "(Ljava/lang/String;Ljava/lang/Object;)Z");
        builder.beginMethod();

        for (ComponentPropertyGenerationPlan propertyPlan : componentPlan.getComponentPropertyGenerationPlans())
        {
            final String propertyName = propertyPlan.getPropertyName();
            final Class<?> propertyType = propertyPlan.getPropertyType();

            final Label fail = new Label();

            builder.loadConstant(propertyName);
            builder.loadReferenceVariable(1);
            builder.invokeVirtualMethod(String.class, "equals", boolean.class, Object.class);
            builder.jumpIfEquals(fail);

            if (isObjectType(propertyType) || isObjectArrayType(propertyType))
            {
                final Label endIfNull = new Label();
                final Label store = new Label();

                builder.loadThis();

                builder.loadVariable(2, Object.class);
                builder.jumpIfNull(endIfNull);

                builder.loadVariable(2, Object.class);
                builder.castTo(propertyType);
                builder.jumpTo(store);

                builder.label(endIfNull);
                builder.pushNull();

                builder.label(store);
                builder.storeField(classInternalName, propertyName, propertyType);
                builder.push_1i();
                builder.returnValue(boolean.class);
            }
            else if (isPrimitiveType(propertyType))
            {
                final Label notNull = new Label();

                builder.loadVariable(2, Object.class);
                builder.jumpIfNotNull(notNull);

                builder.loadThis();
                builder.push_0(propertyType);
                builder.storeField(classInternalName, propertyName, propertyType);
                builder.push_1i();
                builder.returnValue(boolean.class);

                builder.label(notNull);

                builder.loadConstant(boxed(propertyType));
                builder.loadVariable(2, Object.class);
                builder.invokeVirtualMethod(Object.class, "getClass", Class.class);
                builder.invokeVirtualMethod(Class.class, "equals", boolean.class, Object.class);
                builder.jumpIfEquals(fail);

                builder.loadThis();

                builder.loadVariable(2, Object.class);
                builder.castTo(boxed(propertyType));

                if (isBooleanPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Boolean.class, "booleanValue", boolean.class);
                }
                else if (isBytePrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Byte.class, "byteValue", byte.class);
                }
                else if (isShortPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Short.class, "shortValue", short.class);
                }
                else if (isIntegerPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Integer.class, "intValue", int.class);
                }
                else if (isLongPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Long.class, "longValue", long.class);
                }
                else if (isFloatPrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Float.class, "floatValue", float.class);
                }
                else if (isDoublePrimitiveType(propertyType))
                {
                    builder.invokeVirtualMethod(Double.class, "doubleValue", double.class);
                }
                else
                {
                    throw new ComponentFactoryException();
                }

                builder.storeField(classInternalName, propertyName, propertyType);
                builder.push_1i();
                builder.returnValue(boolean.class);
            }
            else if (isPrimitiveArrayType(propertyType))
            {
                final Label endifNull = new Label();
                final Label endIfTypeNotMatch = new Label();
                final Class<?> arrayComponentType = propertyType.getComponentType();

                builder.loadVariable(2, Object.class);
                builder.jumpIfNull(endifNull);

                builder.loadVariable(2, Object.class);
                builder.invokeVirtualMethod(Object.class, "getClass", Class.class);
                builder.storeVariable(3, Class.class);
                builder.loadVariable(3, Class.class);
                builder.invokeVirtualMethod(Class.class, "isArray", boolean.class);
                builder.jumpIfEquals(endIfTypeNotMatch);

                builder.loadPrimitiveTypeConstant(arrayComponentType);
                builder.loadVariable(3, Class.class);
                builder.invokeVirtualMethod(Class.class, "getComponentType", Class.class);
                builder.invokeVirtualMethod(Class.class, "equals", boolean.class, Object.class);
                builder.jumpIfEquals(endIfTypeNotMatch);

                builder.loadThis();
                builder.loadVariable(2, Object.class);
                builder.castTo(propertyType);
                builder.storeField(classInternalName, propertyName, propertyType);
                builder.push_1i();
                builder.returnValue(boolean.class);

                builder.label(endIfTypeNotMatch);
                builder.push_0i();
                builder.returnValue(boolean.class);

                builder.label(endifNull);
                builder.loadThis();
                builder.pushNull();
                builder.storeField(classInternalName, propertyName, propertyType);
                builder.push_1i();
                builder.returnValue(boolean.class);
            }
            else
            {
                throw new ComponentFactoryException();
            }

            builder.label(fail);
        }

        builder.push_0i();
        builder.returnValue(boolean.class);
        builder.endMethod();
    }
}
