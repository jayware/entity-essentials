/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2015 Elmar Schug <elmar.schug@jayware.org>,
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
package org.jayware.e2.component.api;


import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.Contextual;
import org.jayware.e2.entity.api.EntityRef;

import java.util.List;


public abstract class AbstractComponent
implements Component, Contextual
{
    protected final Context myContext;
    protected final ComponentManager myComponentManager;

    public AbstractComponent(Context context)
    {
        myContext = context;
        myComponentManager = context.getService(ComponentManager.class);
    }

    public abstract List<String> properties();

    public abstract Object get(String name);

    public abstract boolean set(String name, Object value);

    public abstract boolean has(String name);

    public abstract <T extends Component> T copy();

    public abstract <T extends Component> T copy(T src);

    @Override
    public void pullFrom(EntityRef ref)
    {
        myComponentManager.pullComponent(ref, this);
    }

    @Override
    public void pushTo(EntityRef ref)
    {
        myComponentManager.pushComponent(ref, this);
    }

    @Override
    public void addTo(EntityRef ref)
    {
        myComponentManager.addComponent(ref, this);
    }

    @Override
    public Context getContext()
    {
        return myContext;
    }

    @Override
    public boolean belongsTo(Context context)
    {
        return context != null && myContext.equals(context);
    }

    @Override
    public boolean belongsTo(Contextual contextual)
    {
        return contextual != null && myContext.equals(contextual.getContext());
    }
}
