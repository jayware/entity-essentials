package org.jayware.e2.context.api;


/**
 * A ServiceProvider is used by a {@link Context} to lookup services.
 */
public interface ServiceProvider
{
    /**
     * Returns the service which offers the interface denoted by the specified {@link Class}.
     * <p>
     * <b>Note:</b> In contrast to {@link Context#findService(Class)}, this operation throws
     * a {@link ServiceUnavailableException} if a suitable service could not be found.
     *
     * @param service a {@link Class} representing the service's interface.
     * @param <S> the type of the service.
     *
     * @return a service instance, never <code>null</code>.
     *
     * @throws ServiceUnavailableException if no suitable service could be found.
     */
    <S> S getService(Class<? extends S> service) throws ServiceUnavailableException;

    /**
     * Returns the service which offers the interface denoted by the specified {@link Class}.
     * <p>
     * <b>Note:</b> In contrast to {@link Context#getService(Class)}, this operation returns
     * <code>null</code> if a suitable service could not be found.
     *
     * @param service a {@link Class} representing the service's interface.
     * @param <S> the type of the service.
     *
     * @return a service instance or <code>null</code> if a suitable could not be found.
     */
    <S> S findService(Class<? extends S> service);
}
