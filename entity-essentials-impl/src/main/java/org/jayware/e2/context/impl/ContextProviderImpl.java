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
package org.jayware.e2.context.impl;


import org.jayware.e2.context.api.Context;
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
            throw e;
        }
    }
}
