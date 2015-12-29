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

import java.util.Dictionary;
import java.util.Properties;


public class ConfigurationUtil
{
    public static <T> T getPropertyOrDefault(Dictionary<String, ?> properties, String property, T defaultValue) {
        if (properties != null) {
            Object propertyObject = properties.get(property);

            if (propertyObject != null) {
                return (T) propertyObject;
            }
        }

        return defaultValue;
    }

    public static <T> T getPropertyOrDefault(Properties properties, String property, T defaultValue) {
        if (properties != null) {
            Object propertyObject = properties.get(property);

            if (propertyObject != null) {
                return (T) propertyObject;
            }
        }

        return defaultValue;
    }
}
