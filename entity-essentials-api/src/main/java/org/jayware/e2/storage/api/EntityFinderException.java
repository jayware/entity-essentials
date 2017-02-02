package org.jayware.e2.storage.api;

import static java.lang.String.format;


public class EntityFinderException
extends RuntimeException
{
    public EntityFinderException(String message, Throwable cause, Object... args)
    {
        super(format(message, args), cause);
    }
}
