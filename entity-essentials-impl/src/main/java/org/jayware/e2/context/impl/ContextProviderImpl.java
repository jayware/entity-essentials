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
package org.jayware.e2.context.impl;


import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextInitializationException;
import org.jayware.e2.context.api.ContextInitializer;
import org.jayware.e2.context.api.ContextProvider;

import java.util.Iterator;
import java.util.ServiceLoader;


public class ContextProviderImpl
extends ContextProvider
{
    @Override
    public Context createContext()
    {
        return createContext(getClass().getClassLoader());
    }

    @Override
    public Context createContext(ClassLoader classLoader)
    {
        final Context context = new ContextImpl(new DefaultServiceProviderImpl(classLoader));

        initialize(context, classLoader);

        return context;
    }

    private static void initialize(Context context, ClassLoader classLoader)
    {
        final Iterator<ContextInitializer> services = ServiceLoader.load(ContextInitializer.class, classLoader).iterator();
        try
        {
            while (services.hasNext())
            {
                final ContextInitializer initializer = services.next();
                initializer.initialize(context);
            }
        }
        catch (Exception e)
        {
            context.dispose();
            throw new ContextInitializationException("Failed to initialize context!", e);
        }
    }
}
