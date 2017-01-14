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
package org.jayware.e2.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableCollection;


public class Parameter
{
    private final Class<?> myType;
    private final Map<Class<? extends Annotation>, Annotation> myAnnotations;

    Parameter(Class<?> type)
    {
        this(type, new Annotation[0]);
    }

    Parameter(Class<?> type, Annotation[] annotations)
    {
        myType = type;
        myAnnotations = new HashMap<Class<? extends Annotation>, Annotation>(annotations.length);

        for (Annotation annotation : annotations)
        {
            myAnnotations.put(annotation.annotationType(), annotation);
        }
    }

    public Class<?> getType()
    {
        return myType;
    }

    public <T extends Annotation> Collection getAnnotations()
    {
        return unmodifiableCollection(myAnnotations.values());
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
    {
        return (T) myAnnotations.get(annotationClass);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof Parameter))
        {
            return false;
        }

        final Parameter other = (Parameter) obj;
        return myType.equals(other.myType) && myAnnotations.equals(other.myAnnotations);
    }

    @Override
    public int hashCode()
    {
        int result = myType.hashCode();
        result = 31 * result + myAnnotations.hashCode();
        return result;
    }

    public static List<Parameter> parametersFrom(Method method)
    {
        final List<Parameter> result = new ArrayList<Parameter>();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Annotation[][] parametersAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterTypes.length; i++)
        {
            result.add(new Parameter(parameterTypes[i], parametersAnnotations[i]));
        }

        return result;
    }
}
