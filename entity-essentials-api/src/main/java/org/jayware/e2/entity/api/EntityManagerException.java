package org.jayware.e2.entity.api;

public class EntityManagerException
extends RuntimeException
{
    public EntityManagerException(String message)
    {
        super(message);
    }

    public EntityManagerException(Throwable cause)
    {
        super(cause);
    }
}
