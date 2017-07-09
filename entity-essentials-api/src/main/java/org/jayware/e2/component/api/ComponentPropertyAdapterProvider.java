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
package org.jayware.e2.component.api;


/**
 * A <code>ComponentPropertyAdapterProvider</code> provides {@link ComponentPropertyAdapter}.
 *
 * @see ComponentPropertyAdapter
 *
 * @since 1.0
 */
public interface ComponentPropertyAdapterProvider
{
    /**
     * Registers the {@link ComponentPropertyAdapter} specified by the passed {@link Class} at this
     * <code>ComponentPropertyAdapterProvider</code>.
     *
     * @param adapterClass the {@link Class} of the {@link ComponentPropertyAdapter} to register.
     */
    void registerPropertyAdapter(Class<? extends ComponentPropertyAdapter> adapterClass) throws ComponentPropertyAdapterInstantiationException;

    /**
     * Unregisters the {@link ComponentPropertyAdapter} specified by the passed {@link Class} from this
     * <code>ComponentPropertyAdapterProvider</code>.
     *
     * @param adapterClass the {@link Class} of the {@link ComponentPropertyAdapter} to unregister.
     */
    void unregisterPropertyAdapter(Class<? extends ComponentPropertyAdapter> adapterClass);

    /**
     * Returns an appropriate {@link ComponentPropertyAdapter} according to the specified type.
     * <p>
     * <code>Null</code> is returned if no suitable {@link ComponentPropertyAdapter} was registered before.
     *
     * @param type the {@link Class} of the property.
     * @param <T> the property's type.
     *
     * @return an {@link ComponentPropertyAdapter} instance or <code>null</code>.
     */
    <T> ComponentPropertyAdapter<T> getAdapterFor(Class<T> type);
}
