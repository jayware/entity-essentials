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
