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
package org.jayware.e2.event.api;


import static org.jayware.e2.event.api.Inspector.generateReport;


public class MissingResultException
extends RuntimeException
{
    public MissingResultException(ResultSet resultSet)
    {
        this("", resultSet);
    }

    public MissingResultException(String message, ResultSet resultSet)
    {
        super(generateReport(message, resultSet.getQuery()));
    }

    public MissingResultException(String message, ResultSet resultSet, Throwable cause)
    {
        super(generateReport(message, resultSet.getQuery()), cause);
    }
}
