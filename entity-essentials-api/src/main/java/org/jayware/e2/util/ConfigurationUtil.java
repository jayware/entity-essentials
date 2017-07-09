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
