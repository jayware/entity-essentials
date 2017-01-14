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
import org.jayware.e2.context.api.Contextual;


public class ContextualAssertions
extends AbstractAssert<ContextualAssertions, Contextual>
{
    public ContextualAssertions(Contextual actual)
    {
        super(actual, ContextualAssertions.class);
    }

    public static ContextualAssertions assertThat(Contextual context)
    {
        return new ContextualAssertions(context);
    }

    public ContextualAssertions belongsTo(Context context)
    {
        isNotNull();

        if (!actual.belongsTo(context))
        {
            failWithMessage("Expected Contextual '%s' belongs to Context { %s }", actual, context.getId());
        }

        return this;
    }

    public ContextualAssertions belongsTo(Contextual contextual)
    {
        isNotNull();

        if (!actual.belongsTo(contextual))
        {
            failWithMessage("Expected Contextual '%s' belongs to the same Context as '%s'", actual, contextual);
        }

        return this;
    }
}
