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

import org.jayware.e2.component.api.ComponentPropertyAdapter;
import org.jayware.e2.component.api.ComponentPropertyAdapterInstantiationException;
import org.jayware.e2.component.api.ComponentPropertyAdapterProvider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.TypeUtil.getTypeName;


public class ComponentPropertyAdapterProviderImpl
implements ComponentPropertyAdapterProvider
{
    private final Map<Class<?>, ComponentPropertyAdapter<?>> myPropertyAdapterMap;

    public ComponentPropertyAdapterProviderImpl()
    {
        myPropertyAdapterMap = new ConcurrentHashMap<Class<?>, ComponentPropertyAdapter<?>>();
    }

    @Override
    public void registerPropertyAdapter(Class<? extends ComponentPropertyAdapter> adapterClass)
    throws ComponentPropertyAdapterInstantiationException
    {
        checkNotNull(adapterClass);

        try
        {
            final Class adaptedType = resolveAdaptedType(adapterClass);
            final ComponentPropertyAdapter adapterInstance = adapterClass.newInstance();

            if (!myPropertyAdapterMap.containsKey(adaptedType))
            {
                myPropertyAdapterMap.put(adaptedType, adapterInstance);
            }
        }
        catch (Exception e)
        {
            throw new ComponentPropertyAdapterInstantiationException(e);
        }
    }

    @Override
    public void unregisterPropertyAdapter(Class<? extends ComponentPropertyAdapter> adapterClass)
    {
        checkNotNull(adapterClass);
        myPropertyAdapterMap.remove(resolveAdaptedType(adapterClass));
    }

    @Override
    public <T> ComponentPropertyAdapter<T> getAdapterFor(Class<T> type)
    {
        return (ComponentPropertyAdapter<T>) myPropertyAdapterMap.get(type);
    }

    private Class resolveAdaptedType(Class<? extends ComponentPropertyAdapter> adapterClass)
    {
        for (Type type : adapterClass.getGenericInterfaces())
        {
            if (type instanceof ParameterizedType)
            {
                ParameterizedType parameterizedType = (ParameterizedType) type;

                if (getTypeName(parameterizedType.getRawType()).equals(getTypeName(ComponentPropertyAdapter.class)))
                {
                    return (Class) parameterizedType.getActualTypeArguments()[0];
                }
            }
        }

        throw new IllegalArgumentException();
    }
}
