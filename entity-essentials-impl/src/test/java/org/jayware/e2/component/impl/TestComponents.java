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
package org.jayware.e2.component.impl;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentProperty;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.util.ObjectUtil;

import java.util.Arrays;

import static org.jayware.e2.component.api.ComponentProperty.property;


public class TestComponents
{
    public enum TestEnum
    {
        A,
        B,
        C
    }

    public interface TestComponentA
    extends Component
    {
        TestEnum getTestEnum();

        void setTestEnum(TestEnum testEnum);

        double[] getArray();

        void setArray(double[] array);
    }

    public interface TestComponentB
    extends Component
    {
        int getInt();

        void setInt(int value);

        Integer getInteger();

        void setInteger(Integer value);

        String getString();

        void setString(String value);
    }

    public interface TestComponentCB
    extends TestComponentB
    {

    }

    public interface TestComponentAB
    extends TestComponentA, TestComponentB
    {
        int getValue();

        void setValue(int value);
    }

    public interface TestComponentACB
    extends TestComponentA, TestComponentCB
    {
        int getValue();

        void setValue(int value);
    }

    public interface MalformedCombinedTestComponent
    extends TestComponentA, Comparable
    {
        int getValue();

        void setValue(int value);
    }

    public interface TestComponentWithParameterTypeMismatch
    extends Component
    {
        String getString();

        void setString(int value);
    }

    public interface TestComponentWhichExtendsARenegade
    extends Component, Renegade
    {
        void setNumber(int number);

        String getText();
    }

    public interface Renegade
    {
        void setText(String text);

        int getNumber();
    }

    public interface TestComponentC
    extends Component
    {
        ComponentProperty<Boolean> primitiveBoolean = property(boolean.class);

        boolean getPrimitiveBoolean();

        void setPrimitiveBoolean(boolean value);

        byte getPrimitiveByte();

        void setPrimitiveByte(byte value);

        short getPrimitiveShort();

        void setPrimitiveShort(short value);

        int getPrimitiveInteger();

        void setPrimitiveInteger(int value);

        long getPrimitiveLong();

        void setPrimitiveLong(long value);

        float getPrimitiveFloat();

        void setPrimitiveFloat(float value);

        double getPrimitiveDouble();

        void setPrimitiveDouble(double value);

        Boolean getBooleanObject();

        void setBooleanObject(Boolean value);

        Byte getByteObject();

        void setByteObject(Byte value);

        Short getShortObject();

        void setShortObject(Short value);

        Integer getIntegerObject();

        void setIntegerObject(Integer value);

        Long getLongObject();

        void setLongObject(Long value);

        Float getFloatObject();

        void setFloatObject(Float value);

        Double getDoubleObject();

        void setDoubleObject(Double value);

        String getString();

        void setString(String value);

        boolean[] getPrimitiveBooleanArray();

        void setPrimitiveBooleanArray(boolean[] value);

        byte[] getPrimitiveByteArray();

        void setPrimitiveByteArray(byte[] value);

        short[] getPrimitiveShortArray();

        void setPrimitiveShortArray(short[] value);

        int[] getPrimitiveIntegerArray();

        void setPrimitiveIntegerArray(int[] value);

        long[] getPrimitiveLongArray();

        void setPrimitiveLongArray(long[] value);

        float[] getPrimitiveFloatArray();

        void setPrimitiveFloatArray(float[] value);

        double[] getPrimitiveDoubleArray();

        void setPrimitiveDoubleArray(double[] value);

        Boolean[] getBooleanObjectArray();

        void setBooleanObjectArray(Boolean[] value);

        Byte[] getByteObjectArray();

        void setByteObjectArray(Byte[] value);

        Short[] getShortObjectArray();

        void setShortObjectArray(Short[] value);

        Integer[] getIntegerObjectArray();

        void setIntegerObjectArray(Integer[] value);

        Long[] getLongObjectArray();

        void setLongObjectArray(Long[] value);

        Float[] getFloatObjectArray();

        void setFloatObjectArray(Float[] value);

        Double[] getDoubleObjectArray();

        void setDoubleObjectArray(Double[] value);

        Object[] getObjectArray();

        void setObjectArray(Object[] value);

        String[] getStringArray();

        void setStringArray(String[] value);

        TestEnum getEnum();

        void setEnum(TestEnum value);

        TestEnum[] getEnumArray();

        void setEnumArray(TestEnum[] value);
    }

    public static class CustomComponentASubtype
    implements TestComponentA
    {
        private TestEnum myEnum;
        private double[] myArray;

        @Override
        public TestEnum getTestEnum()
        {
            return myEnum;
        }

        @Override
        public void setTestEnum(final TestEnum testEnum)
        {
            myEnum = testEnum;
        }

        @Override
        public double[] getArray()
        {
            return myArray;
        }

        @Override
        public void setArray(final double[] array)
        {
            myArray = array;
        }

        @Override
        public void pullFrom(final EntityRef ref)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void pushTo(final EntityRef ref)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addTo(final EntityRef ref)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class<? extends Component> type()
        {
            return TestComponentA.class;
        }

        public boolean equals(Object var1) {
            if (this == var1)
            {
                return true;
            }
            else
            {
                if (!(var1 instanceof TestComponentA))
                {
                    return false;
                }
                else
                {
                    final TestComponentA var2 = (TestComponentA) var1;
                    return Arrays.equals(myArray, var2.getArray()) && ObjectUtil.equals(myEnum, var2.getTestEnum());
                }
            }
        }

        public int hashCode() {
            return ObjectUtil.hashCode(new Object[]{myArray, myEnum});
        }
    }
}
