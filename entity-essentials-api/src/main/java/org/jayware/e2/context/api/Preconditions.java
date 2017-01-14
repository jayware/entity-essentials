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


public class Preconditions
{
    /**
     * Ensures that a {@link Context} passed as a parameter to the calling method is not null and not disposed.
     *
     * @param context a {@link Context}
     *
     * @return the {@link Context} that was validated
     *
     * @throws IllegalArgumentException if {@link Context} is null.
     * @throws IllegalStateException if {@link Context} is disposed.
     */
    public static Context checkContextNotNullAndNotDisposed(Context context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("Context mustn't be null!");
        }

        if (context.isDisposed())
        {
            throw new IllegalStateException("Context mustn't be disposed!");
        }

        return context;
    }

    /**
     * Ensures that the passed {@link Contextual}s <code>first</code> and <code>second</code> are not <code>null</code> and they
     * belong to the same valid {@link Context}.
     *
     * @param first first {@link Contextual}
     * @param second first {@link Contextual}
     *
     * @throws IllegalArgumentException if <code>first</code> or <code>second</code> are <code>null</code>.
     * @throws IllegalStateException if either the {@link Context} of <code>first</code> or the {@link Context} of <code>second</code> is disposed.
     * @throws IllegalContextException if <code>first</code> and <code>second</code> do not belong to the same {@link Context}.
     */
    public static void checkContextualsNotNullAndSameContext(Contextual first, Contextual second)
    {
        if (first == null)
        {
            throw new IllegalArgumentException("First contextual mustn't be null!");
        }

        if (second == null)
        {
            throw new IllegalArgumentException("Second contextual mustn't be null!");
        }

        if (first.getContext().isDisposed())
        {
            throw new IllegalStateException("Context mustn't be disposed!");
        }

        if (!first.belongsTo(second))
        {
            throw new IllegalContextException("Contextuals do not belong to the same context!");
        }
    }

    /**
     * Ensures that the passed {@link Contextual} and the passed {@link Context} are not <code>null</code> and
     * the {@link Contextual} belongs to the specified {@link Context}.
     *
     * @param contextual a {@link Contextual}
     * @param context a {@link Context}
     * @param <C> the type of the {@link Contextual}
     *
     * @return the {@link Contextual} that was validated
     *
     * @throws IllegalArgumentException if either the {@link Contextual} or the {@link Context} are <code>null</code>.
     * @throws IllegalContextException if the {@link Contextual} does not belong to the specified {@link Context}.
     */
    public static <C extends Contextual> C checkContextualNotNullAndBelongsToContext(C contextual, Context context)
    {
        if (contextual == null)
        {
            throw new IllegalArgumentException("Contextual mustn't be null!");
        }

        if (context == null)
        {
            throw new IllegalArgumentException("Context mustn't be null!");
        }

        if (!contextual.belongsTo(context))
        {
            throw new IllegalContextException("The contextual does not belong to the context!");
        }

        return contextual;
    }
}
