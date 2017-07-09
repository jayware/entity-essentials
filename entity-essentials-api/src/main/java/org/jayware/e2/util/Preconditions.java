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
package org.jayware.e2.util;


/**
 * Static convenience methods that help a method or constructor checkArgument whether it was invoked correctly.
 */
public class Preconditions
{
    public interface Precondition
    {
        boolean check();
    }

    /**
     * Ensures that the specified {@link Precondition} is met.
     * <p>
     * If the passed {@link Precondition} returns <code>false</code> an {@link IllegalArgumentException} is thrown.
     *
     * @param precondition a {@link Precondition}
     *
     *
     * @throws IllegalArgumentException if the specified {@link Precondition} returns false.
     */
    public static void checkArgument(Precondition precondition)
    {
        if (!precondition.check())
        {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ensures that the specified precondition is met.
     * <p>
     *
     * @param precondition the precondition
     *
     * @throws IllegalArgumentException if the specified precondition is false.
     */
    public static void checkArgument(boolean precondition)
    {
        if (!precondition)
        {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Ensures that the specified {@link Precondition} is met.
     * <p>
     * If the passed {@link Precondition} returns <code>false</code> an {@link IllegalStateException} is thrown.
     *
     * @param precondition a {@link Precondition}
     *
     * @throws IllegalStateException if the specified {@link Precondition} returns false.
     */
    public static void checkState(Precondition precondition)
    {
        if (!precondition.check())
        {
            throw new IllegalStateException();
        }
    }

    /**
     * Ensures that the specified precondition is met.
     * <p>
     *
     * @param precondition a precondition.
     *
     * @throws IllegalStateException if the specified precondition is false.
     */
    public static void checkState(boolean precondition)
    {
        if (!precondition)
        {
            throw new IllegalStateException();
        }
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param <T> the object's type.
     *
     * @return the non-null reference that was validated
     *
     * @throws IllegalArgumentException if reference is <code>null</code>
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
     *
     * @param reference    an object reference
     * @param errorMessage the exception message to use if the checkArgument fails; will be converted to a
     *                     string using {@link String#valueOf(Object)}
     * @param <T> the object's type.
     * @return the non-null reference that was validated
     *
     * @throws IllegalArgumentException if reference is <code>null</code>
     */
    public static <T> T checkNotNull(T reference, Object errorMessage)
    {
        if (reference == null)
        {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * Ensures that a {@link String} passed as a parameter to the calling method is not empty.
     *
     * @param string a {@link String}
     *
     * @return the {@link String} that was validated
     *
     * @throws IllegalArgumentException if {@code string} is null
     */
    public static String checkStringNotEmpty(String string)
    {
        if (string == null || string.isEmpty())
        {
            throw new IllegalArgumentException();
        }
        return string;
    }
}
