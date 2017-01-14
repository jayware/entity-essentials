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
package org.jayware.e2.entity.api;


import org.jayware.e2.context.api.Context;


public class Preconditions
{
    /**
     * Ensures that an {@link EntityRef} passed as a parameter to the calling method is not null and valid.
     *
     * @param ref an {@link EntityRef}
     *
     * @return the non-null reference that was validated
     *
     * @throws IllegalArgumentException if {@link EntityRef} is null.
     * @throws IllegalStateException if the {@link Context} to which the passed {@link EntityRef} belongs to, has been disposed.
     * @throws InvalidEntityRefException if the {@link EntityRef} is invalid.
     */
    public static EntityRef checkRefNotNullAndValid(EntityRef ref)
    {
        if (ref == null)
        {
            throw new IllegalArgumentException("EntityRef must'n be null!");
        }

        if (ref.isInvalid())
        {
            final Context context = ref.getContext();

            if (context.isDisposed())
            {
                throw new IllegalStateException("Context {" + context.getId() + "} has been disposed!");
            }
            else
            {
                throw new InvalidEntityRefException(ref);
            }
        }

        return ref;
    }
}
