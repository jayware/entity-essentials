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

    public abstract List<String> getPropertyNames();

    public abstract List<Class> getPropertyTypes();

    public abstract Object get(String name);

    public abstract <T> T get(ComponentProperty<T> property);

    public abstract boolean set(String name, Object value);

    public abstract <T> boolean set(ComponentProperty<T> property, T value);

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
