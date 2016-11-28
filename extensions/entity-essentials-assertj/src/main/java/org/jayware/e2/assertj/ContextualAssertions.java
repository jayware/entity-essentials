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
