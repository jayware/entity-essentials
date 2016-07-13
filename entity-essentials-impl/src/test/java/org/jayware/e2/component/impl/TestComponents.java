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

import org.jayware.e2.component.api.Component;


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

    public interface TestComponentAB
    extends TestComponentA, TestComponentB
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

    public interface TestComponentC
    extends Component
    {
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
    }
}
