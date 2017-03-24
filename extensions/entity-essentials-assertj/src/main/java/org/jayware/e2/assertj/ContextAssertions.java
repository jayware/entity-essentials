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
package org.jayware.e2.assertj;

import org.assertj.core.api.AbstractAssert;
import org.jayware.e2.context.api.Context;

import java.util.UUID;


public class ContextAssertions
extends AbstractAssert<ContextAssertions, Context>
{
    private ContextAssertions(Context actual)
    {
        super(actual, ContextAssertions.class);
    }

    public static ContextAssertions assertThat(Context context)
    {
        return new ContextAssertions(context);
    }

    public ContextAssertions isDisposed()
    {
        isNotNull();

        if (!actual.isDisposed())
        {
            failWithMessage("Expected Context <%s> to be disposed", actual.getId());
        }

        return this;
    }

    public ContextAssertions isNotDisposed()
    {
        isNotNull();

        if (actual.isDisposed())
        {
            failWithMessage("Expected Context <%s> not to be disposed", actual.getId());
        }

        return this;
    }

    public ContextAssertions hasId(String expectedId)
    {
        isNotNull();

        if (!actual.getId().toString().equals(expectedId))
        {
            failWithMessage("Expected Context <%s> has id: %s", actual.getId(), expectedId);
        }

        return this;
    }

    public ContextAssertions hasId(UUID expectedId)
    {
        isNotNull();

        hasId(expectedId.toString());

        return this;
    }
}
