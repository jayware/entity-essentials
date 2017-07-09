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
package org.jayware.e2.component.impl.generation.asm;


import org.objectweb.asm.Type;

import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.objectweb.asm.Opcodes.T_BOOLEAN;
import static org.objectweb.asm.Opcodes.T_BYTE;
import static org.objectweb.asm.Opcodes.T_CHAR;
import static org.objectweb.asm.Opcodes.T_DOUBLE;
import static org.objectweb.asm.Opcodes.T_FLOAT;
import static org.objectweb.asm.Opcodes.T_INT;
import static org.objectweb.asm.Opcodes.T_LONG;
import static org.objectweb.asm.Opcodes.T_SHORT;
import static org.objectweb.asm.Type.getType;


public class TypeUtil
{
    public static int resolveOpcodePrimitiveType(Class clazz)
    {
        checkNotNull(clazz);

        switch (getType(clazz).getSort())
        {
            case Type.BOOLEAN: return T_BOOLEAN;
            case Type.BYTE: return T_BYTE;
            case Type.CHAR: return T_CHAR;
            case Type.SHORT: return T_SHORT;
            case Type.INT: return T_INT;
            case Type.LONG: return T_LONG;
            case Type.FLOAT: return T_FLOAT;
            case Type.DOUBLE: return T_DOUBLE;
        }

        return 0;
    }

    public static boolean isPrimitiveArrayType(Class<?> type)
    {
        return type.isArray() && type.getComponentType().isPrimitive();
    }

    public static boolean isObjectArrayType(Class<?> type)
    {
        return type.isArray() && isObjectType(type.getComponentType());
    }

    public static boolean isDoublePrimitiveArrayType(Class<?> type)
    {
        return type.isArray() && double.class.equals(type.getComponentType());
    }

    public static boolean isFloatPrimitiveArrayType(Class<?> type)
    {
        return type.isArray() && float.class.equals(type.getComponentType());
    }

    public static boolean isLongPrimitiveArrayType(Class<?> type)
    {
        return type.isArray() && long.class.equals(type.getComponentType());
    }

    public static boolean isIntegerPrimitiveArrayType(Class<?> type)
    {
        return type.isArray() && int.class.equals(type.getComponentType());
    }

    public static boolean isShortPrimitiveArrayType(Class<?> type)
    {
        return type.isArray() && short.class.equals(type.getComponentType());
    }

    public static boolean isBytePrimitiveArrayType(Class<?> type)
    {
        return type.isArray() && byte.class.equals(type.getComponentType());
    }

    public static boolean isBooleanPrimitiveArrayType(Class<?> type)
    {
        return type.isArray() && boolean.class.equals(type.getComponentType());
    }

    public static boolean isArrayType(Class<?> type)
    {
        return type.isArray();
    }

    public static boolean isObjectType(Class<?> type)
    {
        return !type.isPrimitive() && !type.isArray();
    }

    public static boolean isStringType(Class<?> type)
    {
        return String.class.equals(type);
    }

    public static boolean isDoublePrimitiveType(Class<?> type)
    {
        return double.class.equals(type);
    }

    public static boolean isFloatPrimitiveType(Class<?> type)
    {
        return float.class.equals(type);
    }

    public static boolean isIntegerPrimitiveType(Class<?> type)
    {
        return int.class.equals(type);
    }

    public static boolean isLongPrimitiveType(Class<?> type)
    {
        return long.class.equals(type);
    }

    public static boolean isShortPrimitiveType(Class<?> type)
    {
        return short.class.equals(type);
    }

    public static boolean isBytePrimitiveType(Class<?> type)
    {
        return byte.class.equals(type);
    }

    public static boolean isBooleanPrimitiveType(Class<?> type)
    {
        return boolean.class.equals(type);
    }

    public static boolean isPrimitiveType(Class<?> type)
    {
        return type.isPrimitive();
    }

    public static Class boxed(Class type)
    {
        if (isBooleanPrimitiveType(type))
        {
            return Boolean.class;
        }
        else if (isBytePrimitiveType(type))
        {
            return Byte.class;
        }
        else if (isShortPrimitiveType(type))
        {
            return Short.class;
        }
        else if (isIntegerPrimitiveType(type))
        {
            return Integer.class;
        }
        else if (isLongPrimitiveType(type))
        {
            return Long.class;
        }
        else if (isFloatPrimitiveType(type))
        {
            return Float.class;
        }
        else if (isDoublePrimitiveType(type))
        {
            return Double.class;
        }
        else
        {
            throw new RuntimeException();
        }
    }

    public static Class boxedArray(Class type)
    {
        if (isBooleanPrimitiveArrayType(type))
        {
            return Boolean[].class;
        }
        else if (isBytePrimitiveArrayType(type))
        {
            return Byte[].class;
        }
        else if (isShortPrimitiveArrayType(type))
        {
            return Short[].class;
        }
        else if (isIntegerPrimitiveArrayType(type))
        {
            return Integer[].class;
        }
        else if (isLongPrimitiveArrayType(type))
        {
            return Long[].class;
        }
        else if (isFloatPrimitiveArrayType(type))
        {
            return Float[].class;
        }
        else if (isDoublePrimitiveArrayType(type))
        {
            return Double[].class;
        }
        else
        {
            throw new RuntimeException();
        }
    }
}
