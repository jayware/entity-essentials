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
package org.jayware.e2.context.impl;


import org.jayware.e2.context.api.ServiceProvider;
import org.jayware.e2.context.api.ServiceUnavailableException;

import java.util.Iterator;
import java.util.ServiceLoader;


public class DefaultServiceProviderImpl
implements ServiceProvider
{
    @Override
    public <S> S getService(Class<? extends S> service)
    {
        final S result = findService(service);

        if (result == null)
        {
            throw new ServiceUnavailableException(service);
        }

        return result;
    }

    @Override
    public <S> S findService(Class<? extends S> service)
    {
        final Iterator<? extends S> iterator = ServiceLoader.load(service).iterator();

        if (iterator.hasNext())
        {
            return iterator.next();
        }

        return null;
    }
}
