/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2016 Elmar Schug <elmar.schug@jayware.org>,
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

import com.google.common.base.Objects;
import org.jayware.e2.component.impl.generation.asm.MethodBuilder;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.objectweb.asm.ClassWriter;

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
    public void writeHashCodeMethodFor(ComponentGenerationPlan componentPlan)
    {
        final ClassWriter classWriter = componentPlan.getClassWriter();
        final MethodBuilder methodBuilder = createMethodBuilder(classWriter, ACC_PUBLIC, "hashCode", "()I");

        methodBuilder.beginMethod();

        final Collection<ComponentPropertyGenerationPlan> propertyGenerationPlans = componentPlan.getComponentPropertyGenerationPlans();

        methodBuilder.pushConstantValue(propertyGenerationPlans.size());
        methodBuilder.newArray(Object.class);

        int index = 0;
        for (ComponentPropertyGenerationPlan propertyGenerationPlan : propertyGenerationPlans)
        {
            final String propertyName = propertyGenerationPlan.getPropertyName();
            final Class propertyType = propertyGenerationPlan.getPropertyType();

            methodBuilder.dup();
            methodBuilder.pushConstantValue(index++);

            methodBuilder.loadThis();
            methodBuilder.loadField(componentPlan.getGeneratedClassInternalName(), propertyName, propertyType);

            if (isPrimitiveType(propertyType))
            {
                if (isBooleanPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Boolean.class, "valueOf", Boolean.class, boolean.class);
                }
                else if (isBytePrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Byte.class, "valueOf", Byte.class, byte.class);
                }
                else if (isShortPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Short.class, "valueOf", Short.class, short.class);
                }
                else if (isIntegerPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Integer.class, "valueOf", Integer.class, int.class);
                }
                else if (isLongPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Long.class, "valueOf", Long.class, long.class);
                }
                else if (isFloatPrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Float.class, "valueOf", Float.class, float.class);
                }
                else if (isDoublePrimitiveType(propertyType))
                {
                    methodBuilder.invokeStaticMethod(Double.class, "valueOf", Double.class, double.class);
                }
            }

            methodBuilder.custom().visitInsn(AASTORE);
        }

        methodBuilder.invokeStaticMethod(Objects.class, "hashCode", int.class, Object[].class);
        methodBuilder.returnValue(int.class);
        methodBuilder.endMethod();
    }
}
