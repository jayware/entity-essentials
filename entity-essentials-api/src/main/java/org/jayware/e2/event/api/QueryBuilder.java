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


import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.util.Consumer;


public interface QueryBuilder
{
    QueryBuilderTo set(String parameter);

    QueryBuilder set(Parameter parameter);

    QueryBuilder setAll(ReadOnlyParameters parameters);

    QueryBuilder on(State state, Consumer<ResultSet> consumer);

    QueryBuilder reset();

    Query build();

    interface QueryBuilderTo
    {
        QueryBuilder to(Object value);
    }
}
