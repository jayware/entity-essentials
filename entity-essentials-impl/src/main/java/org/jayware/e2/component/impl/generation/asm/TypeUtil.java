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
        return type.isArray() && !type.getComponentType().isPrimitive();
    }

    public static boolean isDoublePrimitiveArrayType(Class<?> type)
    {
        return double.class.equals(type.getComponentType());
    }

    public static boolean isFloatPrimitiveArrayType(Class<?> type)
    {
        return float.class.equals(type.getComponentType());
    }

    public static boolean isIntegerPrimitiveArrayType(Class<?> type)
    {
        return int.class.equals(type.getComponentType());
    }

    public static boolean isShortPrimitiveArrayType(Class<?> type)
    {
        return short.class.equals(type.getComponentType());
    }

    public static boolean isBytePrimitiveArrayType(Class<?> type)
    {
        return byte.class.equals(type.getComponentType());
    }

    public static boolean isBooleanPrimitiveArrayType(Class<?> type)
    {
        return boolean.class.equals(type.getComponentType());
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
}
