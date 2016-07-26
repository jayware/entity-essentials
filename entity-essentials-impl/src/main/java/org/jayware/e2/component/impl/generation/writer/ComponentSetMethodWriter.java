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

//                builder.loadVariable(2, Object.class);
//                builder.castTo(boxedArray(propertyType));
//                builder.storeVariable(4, boxedArray(propertyType));
//                builder.loadVariable(4, boxedArray(propertyType));
//                builder.arrayLength();
//                builder.newPrimitiveArray(propertyType.getComponentType());
//                builder.storeVariable(5, propertyType);
//                builder.push_0i();
//                builder.storeVariable(6, int.class);
//
//                final Label headForLoop = new Label();
//                final Label endForLoop = new Label();
//                builder.label(headForLoop);
//                builder.loadVariable(6, int.class);
//                builder.loadVariable(4, boxedArray(propertyType));
//                builder.arrayLength();
//                builder.jumpIfIntIsEqualsOrGreater(endForLoop);
//
//                builder.loadVariable(4, boxedArray(propertyType));
//                builder.loadVariable(6, int.class);
//                builder.custom().visitInsn(AALOAD);
//
//                final Label ifNotNull = new Label();
//                final Label endIf = new Label();
//                builder.jumpIfNotNull(ifNotNull);
//
//                builder.loadVariable(5, propertyType);
//                builder.loadVariable(6, int.class);
//                builder.push_0(arrayComponentType);
//                builder.custom().visitInsn(Type.getType(arrayComponentType).getOpcode(IASTORE));
//
//                builder.jumpTo(endIf);
//                builder.label(ifNotNull);
//
//                builder.loadVariable(5, propertyType);
//                builder.loadVariable(6, int.class);
//
//                builder.loadVariable(4, boxedArray(propertyType));
//                builder.loadVariable(6, int.class);
//                builder.custom().visitInsn(AALOAD);
//
//                builder.castTo(boxed(arrayComponentType));
//
//                if (isBooleanPrimitiveType(arrayComponentType))
//                {
//                    builder.invokeVirtualMethod(Boolean.class, "booleanValue", boolean.class);
//                }
//                else if (isBytePrimitiveType(arrayComponentType))
//                {
//                    builder.invokeVirtualMethod(Byte.class, "byteValue", byte.class);
//                }
//                else if (isShortPrimitiveType(arrayComponentType))
//                {
//                    builder.invokeVirtualMethod(Short.class, "shortValue", short.class);
//                }
//                else if (isIntegerPrimitiveType(arrayComponentType))
//                {
//                    builder.invokeVirtualMethod(Integer.class, "intValue", int.class);
//                }
//                else if (isLongPrimitiveType(arrayComponentType))
//                {
//                    builder.invokeVirtualMethod(Long.class, "longValue", long.class);
//                }
//                else if (isFloatPrimitiveType(arrayComponentType))
//                {
//                    builder.invokeVirtualMethod(Float.class, "floatValue", float.class);
//                }
//                else if (isDoublePrimitiveType(arrayComponentType))
//                {
//                    builder.invokeVirtualMethod(Double.class, "doubleValue", double.class);
//                }
//                else
//                {
//                    throw new ComponentFactoryException();
//                }
//
//                builder.custom().visitInsn(Type.getType(arrayComponentType).getOpcode(IASTORE));
//
//                builder.label(endIf);
//
//                builder.incrementInt(6, 1);
//                builder.jumpTo(headForLoop);
//                builder.label(endForLoop);

//                builder.push_0(propertyType);
//                builder.storeVariable(5, propertyType);

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
