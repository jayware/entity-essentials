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
package org.jayware.e2.util;

import java.lang.reflect.Type;


public class TypeUtil
{
    public static String getTypeName(Type type)
    {
        return getTypeName((Class) type);
    }

    public static String getTypeName(Class clazz)
    {
        if (clazz.isArray())
        {
            try
            {
                Class<?> cl = clazz;
                int dimensions = 0;

                while (cl.isArray())
                {
                    dimensions++;
                    cl = cl.getComponentType();
                }

                StringBuilder sb = new StringBuilder();
                sb.append(cl.getName());

                for (int i = 0; i < dimensions; i++)
                {
                    sb.append("[]");
                }

                return sb.toString();
            }
            catch (Throwable ignored)
            {

            }
        }

        return clazz.getName();
    }
}

