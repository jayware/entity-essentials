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
package org.jayware.e2.component.impl;


import mockit.Mocked;
import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.component.impl.TestComponents.TestComponentC;
import org.jayware.e2.component.impl.TestComponents.TestEnum;
import org.jayware.e2.context.api.Context;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.impl.TestComponents.TestEnum.A;
import static org.jayware.e2.component.impl.TestComponents.TestEnum.B;
import static org.jayware.e2.component.impl.TestComponents.TestEnum.C;


public class GeneratedComponentTest
{
    private static final boolean[] PRIMITIVE_BOOLEAN_ARRAY = {false, true, true, true, false, false};
    private static final Boolean[] BOOLEAN_OBJECT_ARRAY = {false, false, true, true, true, false};

    private static final byte[] PRIMITIVE_BYTE_ARRAY = {-1, 5, 42, 73, -13, 37};
    private static final Byte[] BYTE_OBJECT_ARRAY = {-42, 1, 73, 7, -128, 1};

    private static final short[] PRIMITIVE_SHORT_ARRAY = {-9531, 5, 3, 73, -1, 45};
    private static final Short[] SHORT_OBJECT_ARRAY = {-12, 24, 6314, 7, -5, -512};

    private static final int[] PRIMITIVE_INTEGER_ARRAY = {-1, 123, 1321, -573, -13, 37};
    private static final Integer[] INTEGER_OBJECT_ARRAY = {-42, 73, 1337, 4312, -5, 1};

    private static final long[] PRIMITIVE_LONG_ARRAY = {-16424, -123, 62315, -525373, -1337, 1337};
    private static final Long[] LONG_OBJECT_ARRAY = {-42424242L, 743L, 1337L, 4312L, -5L, 15123L};

    private static final float[] PRIMITIVE_FLOAT_ARRAY = {-1f, 1.23f, 13.21f, -5.73f, -13f, 3.7f};
    private static final Float[] FLOAT_OBJECT_ARRAY = {-42f, 73f, 13.37f, 43.12f, -5f, 1.7f};

    private static final double[] PRIMITIVE_DOUBLE_ARRAY = {-1d, 1.23d, 13.21d, -5.73d, -13d, 3.7d};
    private static final Double[] DOUBLE_OBJECT_ARRAY = {-42d, 73d, 13.37d, 43.12d, -5d, 1.7d};

    private static final Object[] OBJECT_ARRAY = {1.5, "foo", 1337, new Object(), false, "bar"};
    private static final String[] STRING_ARRAY = {"F", "U", "B", "A", "R"};

    private static final TestEnum[] ENUM_ARRAY = {A, B, B, A, C, B};

    private @Mocked Context testContext;
    private ComponentFactoryImpl componentFactory;

    private TestComponentC testee, testeeA, testeeB;

    @BeforeMethod
    public void setup()
    {
        componentFactory = new ComponentFactoryImpl();
        componentFactory.prepareComponent(TestComponentA.class);
        componentFactory.prepareComponent(TestComponentC.class);
        testee = componentFactory.createComponent(TestComponentC.class).newInstance(testContext);

        testeeA = componentFactory.createComponent(TestComponentC.class).newInstance(testContext);
        testeeB = componentFactory.createComponent(TestComponentC.class).newInstance(testContext);
        System.out.println();
    }

    @Test
    public void test_PrimitiveBoolean_Property()
    {
        final boolean value = true;

        assertThat(((AbstractComponent) testee).set("primitiveBoolean", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveBoolean")).isEqualTo(value);

        testeeA.setPrimitiveBoolean(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveBoolean(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveBooleanArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("primitiveBooleanArray", PRIMITIVE_BOOLEAN_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveBooleanArray")).isEqualTo(PRIMITIVE_BOOLEAN_ARRAY);

        testeeA.setPrimitiveBooleanArray(PRIMITIVE_BOOLEAN_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveBooleanArray(PRIMITIVE_BOOLEAN_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_BooleanObject_Property()
    {
        final Boolean value = Boolean.TRUE;

        assertThat(((AbstractComponent) testee).set("booleanObject", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("booleanObject")).isEqualTo(value);

        testeeA.setBooleanObject(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setBooleanObject(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_BooleanObjectArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("booleanObjectArray", BOOLEAN_OBJECT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("booleanObjectArray")).isEqualTo(BOOLEAN_OBJECT_ARRAY);

        testeeA.setBooleanObjectArray(BOOLEAN_OBJECT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setBooleanObjectArray(BOOLEAN_OBJECT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }
    @Test
    public void test_PrimitiveByte_Property()
    {
        final byte value = 42;

        assertThat(((AbstractComponent) testee).set("primitiveByte", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveByte")).isEqualTo(value);

        testeeA.setPrimitiveByte(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveByte(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveByteArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("primitiveByteArray", PRIMITIVE_BYTE_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveByteArray")).isEqualTo(PRIMITIVE_BYTE_ARRAY);

        testeeA.setPrimitiveByteArray(PRIMITIVE_BYTE_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveByteArray(PRIMITIVE_BYTE_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }
    @Test
    public void test_ByteObject_Property()
    {
        final Byte value = 42;

        assertThat(((AbstractComponent) testee).set("byteObject", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("byteObject")).isEqualTo(value);

        testeeA.setByteObject(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setByteObject(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_ByteObjectArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("byteObjectArray", BYTE_OBJECT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("byteObjectArray")).isEqualTo(BYTE_OBJECT_ARRAY);

        testeeA.setByteObjectArray(BYTE_OBJECT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setByteObjectArray(BYTE_OBJECT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveShort_Property()
    {
        final short value = 42;

        assertThat(((AbstractComponent) testee).set("primitiveShort", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveShort")).isEqualTo(value);

        testeeA.setPrimitiveShort(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveShort(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveShortArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("primitiveShortArray", PRIMITIVE_SHORT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveShortArray")).isEqualTo(PRIMITIVE_SHORT_ARRAY);

        testeeA.setPrimitiveShortArray(PRIMITIVE_SHORT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveShortArray(PRIMITIVE_SHORT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_ShortObject_Property()
    {
        final Short value = 42;

        assertThat(((AbstractComponent) testee).set("shortObject", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("shortObject")).isEqualTo(value);

        testeeA.setShortObject(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setShortObject(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_ShortObjectArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("shortObjectArray", SHORT_OBJECT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("shortObjectArray")).isEqualTo(SHORT_OBJECT_ARRAY);

        testeeA.setShortObjectArray(SHORT_OBJECT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setShortObjectArray(SHORT_OBJECT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveInteger_Property()
    {
        final int value = 42;

        assertThat(((AbstractComponent) testee).set("primitiveInteger", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveInteger")).isEqualTo(value);

        testeeA.setPrimitiveInteger(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveInteger(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveIntegerArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("primitiveIntegerArray", PRIMITIVE_INTEGER_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveIntegerArray")).isEqualTo(PRIMITIVE_INTEGER_ARRAY);

        testeeA.setPrimitiveIntegerArray(PRIMITIVE_INTEGER_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveIntegerArray(PRIMITIVE_INTEGER_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_IntegerObject_Property()
    {
        final Integer value = new Integer(42);

        assertThat(((AbstractComponent) testee).set("integerObject", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("integerObject")).isEqualTo(value);

        testeeA.setIntegerObject(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setIntegerObject(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_IntegerObjectArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("integerObjectArray", INTEGER_OBJECT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("integerObjectArray")).isEqualTo(INTEGER_OBJECT_ARRAY);

        testeeA.setIntegerObjectArray(INTEGER_OBJECT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setIntegerObjectArray(INTEGER_OBJECT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveLong_Property()
    {
        final long value = 42L;

        assertThat(((AbstractComponent) testee).set("primitiveLong", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveLong")).isEqualTo(value);

        testeeA.setPrimitiveLong(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveLong(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveLongArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("primitiveLongArray", PRIMITIVE_LONG_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveLongArray")).isEqualTo(PRIMITIVE_LONG_ARRAY);

        testeeA.setPrimitiveLongArray(PRIMITIVE_LONG_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveLongArray(PRIMITIVE_LONG_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_LongObject_Property()
    {
        final Long value = 42L;

        assertThat(((AbstractComponent) testee).set("longObject", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("longObject")).isEqualTo(value);

        testeeA.setLongObject(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setLongObject(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_LongObjectArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("longObjectArray", LONG_OBJECT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("longObjectArray")).isEqualTo(LONG_OBJECT_ARRAY);

        testeeA.setLongObjectArray(LONG_OBJECT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setLongObjectArray(LONG_OBJECT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveFloat_Property()
    {
        final float value = 42f;

        assertThat(((AbstractComponent) testee).set("primitiveFloat", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveFloat")).isEqualTo(value);

        testeeA.setPrimitiveFloat(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveFloat(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveFloatArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("primitiveFloatArray", PRIMITIVE_FLOAT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveFloatArray")).isEqualTo(PRIMITIVE_FLOAT_ARRAY);

        testeeA.setPrimitiveFloatArray(PRIMITIVE_FLOAT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveFloatArray(PRIMITIVE_FLOAT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_FloatObject_Property()
    {
        final Float value = 42f;

        assertThat(((AbstractComponent) testee).set("floatObject", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("floatObject")).isEqualTo(value);

        testeeA.setFloatObject(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setFloatObject(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_FloatObjectArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("floatObjectArray", FLOAT_OBJECT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("floatObjectArray")).isEqualTo(FLOAT_OBJECT_ARRAY);

        testeeA.setFloatObjectArray(FLOAT_OBJECT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setFloatObjectArray(FLOAT_OBJECT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveDouble_Property()
    {
        final double value = 42d;

        assertThat(((AbstractComponent) testee).set("primitiveDouble", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveDouble")).isEqualTo(value);

        testeeA.setPrimitiveDouble(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveDouble(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_PrimitiveDoubleArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("primitiveDoubleArray", PRIMITIVE_DOUBLE_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("primitiveDoubleArray")).isEqualTo(PRIMITIVE_DOUBLE_ARRAY);

        testeeA.setPrimitiveDoubleArray(PRIMITIVE_DOUBLE_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setPrimitiveDoubleArray(PRIMITIVE_DOUBLE_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_DoubleObject_Property()
    {
        final Double value = 42d;

        assertThat(((AbstractComponent) testee).set("doubleObject", value)).isTrue();
        assertThat(((AbstractComponent) testee).get("doubleObject")).isEqualTo(value);

        testeeA.setDoubleObject(value);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setDoubleObject(value);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_DoubleObjectArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("doubleObjectArray", DOUBLE_OBJECT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("doubleObjectArray")).isEqualTo(DOUBLE_OBJECT_ARRAY);

        testeeA.setDoubleObjectArray(DOUBLE_OBJECT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setDoubleObjectArray(DOUBLE_OBJECT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_ObjectArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("objectArray", OBJECT_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("objectArray")).isEqualTo(OBJECT_ARRAY);

        testeeA.setObjectArray(OBJECT_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setObjectArray(OBJECT_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_StringArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("stringArray", STRING_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("stringArray")).isEqualTo(STRING_ARRAY);

        testeeA.setStringArray(STRING_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setStringArray(STRING_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_Enum_Property()
    {
        assertThat(((AbstractComponent) testee).set("enum", A)).isTrue();
        assertThat(((AbstractComponent) testee).get("enum")).isEqualTo(A);

        testeeA.setEnum(A);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setEnum(A);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }

    @Test
    public void test_EnumArray_Property()
    {
        assertThat(((AbstractComponent) testee).set("enumArray", ENUM_ARRAY)).isTrue();
        assertThat(((AbstractComponent) testee).get("enumArray")).isEqualTo(ENUM_ARRAY);

        testeeA.setEnumArray(ENUM_ARRAY);

        assertThat(testeeA).isNotEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isNotEqualTo(testeeB.hashCode());

        testeeB.setEnumArray(ENUM_ARRAY);

        assertThat(testeeA).isEqualTo(testeeB);
        assertThat(testeeA.hashCode()).isEqualTo(testeeB.hashCode());
    }
}
