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
package org.jayware.e2.component.impl;

import org.jayware.e2.component.api.ComponentFactory;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextInitializer;

import static org.jayware.e2.component.impl.ComponentManagerImpl.COMPONENT_FACTORY;
import static org.jayware.e2.component.impl.ComponentManagerImpl.COMPONENT_STORE;
import static org.jayware.e2.component.impl.ComponentManagerImpl.PROPERTY_ADAPTER_PROVIDER;


public class ContextInitializerImpl
implements ContextInitializer
{
    @Override
    public void initialize(Context context)
    {
        final ComponentFactoryImpl componentFactory = new ComponentFactoryImpl();
        context.put(COMPONENT_FACTORY, componentFactory);
        context.put(ComponentFactory.class, componentFactory);
        context.put(COMPONENT_STORE, new ComponentStore(context));
        context.put(PROPERTY_ADAPTER_PROVIDER, new ComponentPropertyAdapterProviderImpl());
    }
}
