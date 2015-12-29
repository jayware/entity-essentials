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
package org.jayware.e2.util;


/**
 * Static convenience methods that help a method or constructor check whether it was invoked correctly.
 * <p>
 * <b>Note:</b> These methods are based on methods of the {@link com.google.common.base.Preconditions Google Guava Preconditions}
 * class.
 */
public class Preconditions
{
    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     * <p>
     * <b>Note:</b> This operation is based on the {@link com.google.common.base.Preconditions#checkNotNull(Object)
     * Google Guavas Preconditions},
     * but instead of throwing a {@link NullPointerException} when the passed reference is null, this operation
     * throws an {@link IllegalArgumentException}.
     *
     * @param reference an object reference
     * @param <T> the object's type.
     *
     * @return the non-null reference that was validated
     *
     * @throws IllegalArgumentException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference)
    {
        if (reference == null)
        {
            throw new IllegalArgumentException();
        }
        return reference;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     * <p>
     * <b>Note:</b> This operation is based on the {@link com.google.common.base.Preconditions#checkNotNull(Object, Object)
     * Google Guavas Preconditions},
     * but instead of throwing a {@link NullPointerException} when the passed reference is null, this operation
     * throws an {@link IllegalArgumentException}.
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @param <T> the object's type.
     * @return the non-null reference that was validated
     *
     * @throws IllegalArgumentException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, Object errorMessage)
    {
        if (reference == null)
        {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
