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
package org.jayware.e2.event.api;


import java.util.Arrays;

import static org.jayware.e2.util.Preconditions.checkNotNull;


public class Preconditions
{
    public static Event checkEventNotNullAndHasOneOfTypes(Event event, Class<? extends EventType>... types)
    {
        checkNotNull(event, "Event must'n be null!");

        if (types != null)
        {
            for (Class<? extends EventType> type : types)
            {
                if (type.isAssignableFrom(event.getType()))
                {
                    return event;
                }
            }

            throw new IllegalArgumentException("Event has to have one of the types: " + Arrays.toString(types));
        }

        return event;
    }

    public static Event checkEventNotNull(Event event)
    {
        return checkEventNotNullAndHasOneOfTypes(event, (Class<? extends EventType>[]) null);
    }
}
