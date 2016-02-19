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
