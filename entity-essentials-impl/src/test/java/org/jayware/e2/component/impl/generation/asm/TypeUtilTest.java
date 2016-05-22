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
package org.jayware.e2.component.impl.generation.asm;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.resolveOpcodePrimitiveType;
import static org.objectweb.asm.Opcodes.T_BOOLEAN;
import static org.objectweb.asm.Opcodes.T_BYTE;
import static org.objectweb.asm.Opcodes.T_CHAR;
import static org.objectweb.asm.Opcodes.T_DOUBLE;
import static org.objectweb.asm.Opcodes.T_FLOAT;
import static org.objectweb.asm.Opcodes.T_INT;
import static org.objectweb.asm.Opcodes.T_LONG;
import static org.objectweb.asm.Opcodes.T_SHORT;


public class TypeUtilTest
{
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_resolveOpcodePrimitiveType_Throws_IllegalArgumentException_if_null_is_passed()
    {
        resolveOpcodePrimitiveType(null);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_Returns_0_when_unknown_class_is_passed()
    {
        assertThat(resolveOpcodePrimitiveType(TypeUtilTest.class)).isEqualTo(0);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_With_boolean()
    {
        assertThat(resolveOpcodePrimitiveType(boolean.class)).isEqualTo(T_BOOLEAN);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_With_byte()
    {
        assertThat(resolveOpcodePrimitiveType(byte.class)).isEqualTo(T_BYTE);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_With_char()
    {
        assertThat(resolveOpcodePrimitiveType(char.class)).isEqualTo(T_CHAR);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_With_short()
    {
        assertThat(resolveOpcodePrimitiveType(short.class)).isEqualTo(T_SHORT);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_With_int()
    {
        assertThat(resolveOpcodePrimitiveType(int.class)).isEqualTo(T_INT);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_With_long()
    {
        assertThat(resolveOpcodePrimitiveType(long.class)).isEqualTo(T_LONG);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_With_float()
    {
        assertThat(resolveOpcodePrimitiveType(float.class)).isEqualTo(T_FLOAT);
    }

    @Test
    public void test_resolveOpcodePrimitiveType_With_double()
    {
        assertThat(resolveOpcodePrimitiveType(double.class)).isEqualTo(T_DOUBLE);
    }
}
