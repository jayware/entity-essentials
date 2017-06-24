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
package org.jayware.e2.component.impl.generation.plan;


import org.jayware.e2.util.ObjectUtil;

import java.lang.reflect.Method;

import static org.objectweb.asm.Type.getMethodDescriptor;


public class ComponentPropertyGenerationPlan
{
    private final ComponentGenerationPlan myOwner;

    private final String myName;

    private Class myType;

    private Method myGetterMethod;
    private String myGetterMethodDescriptor;

    private Method mySetterMethod;
    private String mySetterMethodDescriptor;

    public ComponentPropertyGenerationPlan(ComponentGenerationPlan owner, String propertyName)
    {
        myOwner = owner;
        myName = propertyName;
    }

    public ComponentGenerationPlan getComponentGenerationPlan()
    {
        return myOwner;
    }

    public String getPropertyName()
    {
        return myName;
    }

    public Class getPropertyType()
    {
        return myType;
    }

    public void setPropertyType(Class type)
    {
        myType = type;
    }

    public boolean hasGetter()
    {
        return myGetterMethod != null;
    }

    public Method getPropertyGetterMethod()
    {
        return myGetterMethod;
    }

    public void setPropertyGetterMethod(Method getterMethod)
    {
        myGetterMethod = getterMethod;
        myGetterMethodDescriptor = getMethodDescriptor(getterMethod);
    }

    public String getPropertyGetterMethodName()
    {
        return myGetterMethod.getName();
    }

    public String getPropertyGetterMethodDescriptor()
    {
        return myGetterMethodDescriptor;
    }

    public boolean hasSetter()
    {
        return mySetterMethod != null;
    }

    public Method getPropertySetterMethod()
    {
        return mySetterMethod;
    }

    public void setPropertySetterMethod(Method getterMethod)
    {
        mySetterMethod = getterMethod;
        mySetterMethodDescriptor = getMethodDescriptor(getterMethod);
    }

    public String getPropertySetterMethodName()
    {
        return mySetterMethod.getName();
    }

    public String getPropertySetterMethodDescriptor()
    {
        return mySetterMethodDescriptor;
    }

    public boolean isComplete()
    {
        return hasGetter() && hasSetter() && myType != null;
    }

    public boolean isIncomplete()
    {
        return !isComplete();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        final ComponentPropertyGenerationPlan that = (ComponentPropertyGenerationPlan) o;
        return ObjectUtil.equals(myOwner, that.myOwner) &&
               ObjectUtil.equals(myName, that.myName);
    }

    @Override
    public int hashCode()
    {
        return ObjectUtil.hashCode(myOwner, myName);
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("ComponentPropertyGenerationPlan{");
        sb.append("myName=").append(myName);
        sb.append(", myType=").append(myType);
        sb.append(", myOwner='").append(myOwner).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
