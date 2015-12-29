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


public class ComponentPropertyAdapterInstantiationException
extends RuntimeException
{
    public ComponentPropertyAdapterInstantiationException()
    {
    }

    public ComponentPropertyAdapterInstantiationException(String message)
    {
        super(message);
    }

    public ComponentPropertyAdapterInstantiationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ComponentPropertyAdapterInstantiationException(Throwable cause)
    {
        super(cause);
    }

    public ComponentPropertyAdapterInstantiationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
