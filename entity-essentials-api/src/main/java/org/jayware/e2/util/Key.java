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
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof Key))
        {
            return false;
        }

        final Key<?> other = (Key<?>) obj;
        return ObjectUtil.equals(myKey, other.myKey);
    }

    @Override
    public int hashCode()
    {
        return ObjectUtil.hashCode(myKey);
    }

    @Override
    public String toString()
    {
        return "Key{ " + myKey + " }";
    }
}
