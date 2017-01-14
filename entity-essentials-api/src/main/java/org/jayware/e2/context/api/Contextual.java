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
 * A <code>Contextual</code> is an object which belongs to an <code>Context</code>.
 *
 * @see Context
 */
public interface Contextual
{
    /**
     * Returns the {@link Context} this object belongs to.
     *
     * @return this object's {@link Context}.
     */
    Context getContext();

    /**
     * Returns whether this object belongs to the specified {@link Context}.
     *
     * @param context a {@link Context}.
     *
     * @return <code>true</code> if this object belongs to the specified {@link Context}, otherwise <code>false</code>.
     */
    boolean belongsTo(Context context);

    /**
     * Returns whether this object belongs to the same {@link Context} as the specified one.
     *
     * @param contextual a {@link Contextual}.
     *
     * @return <code>true</code> if this object belongs to the smae {@link Context} as the specified one, otherwise <code>false</code>.
     */
    boolean belongsTo(Contextual contextual);
}
