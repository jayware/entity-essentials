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
