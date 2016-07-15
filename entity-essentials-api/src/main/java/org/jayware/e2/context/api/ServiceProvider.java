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
     * @param loader a {@link ClassLoader} used to load the {@link Class}.
     * @param <S> the type of the service.
     *
     * @return a service instance, never <code>null</code>.
     *
     * @throws ServiceUnavailableException if no suitable service could be found.
     */
    <S> S getService(Class<? extends S> service, ClassLoader loader) throws ServiceUnavailableException;

    /**
     * Returns the service which offers the interface denoted by the specified {@link Class}.
     * <p>
     * <b>Note:</b> In contrast to {@link Context#getService(Class)}, this operation returns
     * <code>null</code> if a suitable service could not be found.
     *
     * @param service a {@link Class} representing the service's interface.
     * @param loader a {@link ClassLoader} used to load the {@link Class}.
     * @param <S> the type of the service.
     *
     * @return a service instance or <code>null</code> if a suitable could not be found.
     */
    <S> S findService(Class<? extends S> service, ClassLoader loader);
}
