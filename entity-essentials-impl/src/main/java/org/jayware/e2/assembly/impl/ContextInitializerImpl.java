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
package org.jayware.e2.assembly.impl;

import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextInitializer;

import static org.jayware.e2.assembly.impl.TreeManagerImpl.TREE_HUB;


public class ContextInitializerImpl
implements ContextInitializer
{
    private static final Context.ValueProvider<TreeHub> TREE_HUB_VALUE_PROVIDER = new Context.ValueProvider<TreeHub>()
    {
        @Override
        public TreeHub provide(Context context)
        {
            return new TreeHub(context);
        }
    };

    @Override
    public void initialize(Context context)
    {
        context.putIfAbsent(TREE_HUB, TREE_HUB_VALUE_PROVIDER);
    }
}
