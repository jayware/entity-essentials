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


import java.util.Objects;


/**
 * A <code>Key</code>
 *
 * @param <V> the corresponding value type.
 *
 * @since 1.0
 */
public class Key<V>
{
    private final String myKey;

    private Key(String key)
    {
        myKey = key;
    }

    public static <V> Key<V> createKey(String key)
    {
        return new Key(key);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final Key<?> key = (Key<?>) o;
        return Objects.equals(myKey, key.myKey);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(myKey);
    }

    @Override
    public String toString()
    {
        return "Key{ " + myKey + " }";
    }
}
