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
package org.jayware.e2.event.impl;

import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventBuilder;
import org.jayware.e2.event.api.EventBuilder.EventBuilderTo;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.ReadOnlyParameters;

import static java.util.UUID.randomUUID;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.Preconditions.checkStringNotEmpty;


class EventBuilderImpl
implements EventBuilder, EventBuilderTo
{
    private final Class<? extends EventType> myEventType;
    private final Parameters myEventParameters;

    private String myLastParameter;

    private EventBuilderImpl(Class<? extends EventType> type)
    {
        myEventType = type;
        myEventParameters = new Parameters();
    }

    static EventBuilder createEventBuilder(Class<? extends EventType> type)
    {
        checkNotNull(type);

        return new EventBuilderImpl(type);
    }

    @Override
    public EventBuilder reset()
    {
        myEventParameters.clear();
        return this;
    }

    @Override
    public EventBuilderTo set(String parameter)
    {
        checkStringNotEmpty(parameter);
        myLastParameter = parameter;
        return this;
    }

    @Override
    public EventBuilder set(Parameter parameter)
    {
        checkNotNull(parameter);
        myEventParameters.set(parameter);
        return this;
    }

    @Override
    public EventBuilder setAll(ReadOnlyParameters parameters)
    {
        checkNotNull(parameters);
        myEventParameters.set(parameters);
        return this;
    }

    @Override
    public EventBuilder to(Object value)
    {
        myEventParameters.set(myLastParameter, value);
        return this;
    }

    @Override
    public Event build()
    {
        return new EventImpl(randomUUID(), myEventType, myEventParameters);
    }
}
