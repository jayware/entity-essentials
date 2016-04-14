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
            throw new IllegalArgumentException();
        }

        if (context.isDisposed())
        {
            throw new IllegalStateException();
        }

        return context;
    }

    /**
     * Ensures that the passed {@link Contextual}s <code>a</code> and <code>b</code> are not <code>null</code> and they
     * belong to the same valid {@link Context}.
     *
     * @param a a {@link Contextual}
     * @param b a {@link Contextual}
     *
     * @throws IllegalArgumentException if <code>a</code> or <code>b</code> are <code>null</code>.
     * @throws IllegalStateException if either the {@link Context} of <code>a</code> or the {@link Context} of <code>b</code> is disposed.
     * @throws IllegalContextException if <code>a</code> and <code>b</code> do not belong to the same {@link Context}.
     */
    public static void checkContextualsNotNullAndSameContext(Contextual a, Contextual b)
    {
        if (a == null || b == null)
        {
            throw new IllegalArgumentException();
        }

        if (!a.belongsTo(b))
        {
            throw new IllegalContextException();
        }

        if (a.getContext().isDisposed())
        {
            throw new IllegalStateException();
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
        if (contextual == null || context == null)
        {
            throw new IllegalArgumentException();
        }

        if (!contextual.belongsTo(context))
        {
            throw new IllegalContextException();
        }

        return contextual;
    }
}
