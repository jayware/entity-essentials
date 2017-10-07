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
package org.jayware.e2.component.api;

public class PropertyDeclarationException
extends RuntimeException
{
    public PropertyDeclarationException(final String message)
    {
        super(message);
    }

    public PropertyDeclarationException(StackTraceElement invocation)
    {
        super(createMessage(invocation));
    }

    private static String createMessage(StackTraceElement invocation)
    {
        String message = "Illegal invocation of Property.property(<type>)";

        if (invocation != null)
        {
            message += " at " + invocation.getClassName() + "." + invocation.getMethodName() + "(" + invocation.getFileName() + ":" + invocation.getLineNumber() + ")";
        }

        return message + ". This operation is intended to declare properties within component classes!";
    }
}
