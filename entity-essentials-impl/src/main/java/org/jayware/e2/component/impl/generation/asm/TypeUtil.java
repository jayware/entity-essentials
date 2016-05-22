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
}
