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

import java.util.UUID;


public class ContextAssertions
extends AbstractAssert<ContextAssertions, Context>
{
    public ContextAssertions(Context actual)
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
            failWithMessage("Expected Context { %s } to be disposed", actual.getId());
        }

        return this;
    }

    public ContextAssertions isNotDisposed()
    {
        isNotNull();

        if (actual.isDisposed())
        {
            failWithMessage("Expected Context { %s } not to be disposed", actual.getId());
        }

        return this;
    }

    public ContextAssertions hasId(String expectedId)
    {
        isNotNull();

        if (!actual.getId().toString().equals(expectedId))
        {
            failWithMessage("Expected Context { %s } has id: %s", actual.getId(), expectedId);
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
