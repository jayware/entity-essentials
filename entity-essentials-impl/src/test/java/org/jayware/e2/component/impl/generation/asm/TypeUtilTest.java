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


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.resolveOpcodePrimitiveType;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Test
    public void test_resolveOpcodePrimitiveType_Throws_IllegalArgumentException_if_null_is_passed()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                resolveOpcodePrimitiveType(null);
            }
        });
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
