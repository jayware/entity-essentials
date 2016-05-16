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
package org.jayware.e2.component.impl;

import org.jayware.e2.component.api.AbstractComponentWrapper;
import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.ComponentNotFoundException;
import org.jayware.e2.component.api.ComponentPropertyAdapter;
import org.jayware.e2.component.api.ComponentPropertyAdapterProvider;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.util.Key;

import java.util.Collection;
import java.util.Set;

import static org.jayware.e2.util.Key.createKey;
import static org.jayware.e2.util.Preconditions.checkNotNull;


public class ComponentManagerImpl
implements ComponentManager
{
    private static final Key<ComponentStore> COMPONENT_STORE = createKey("org.jayware.e2.ComponentStore");
    private static final Key<ComponentPropertyAdapterProvider> PROPERTY_ADAPTER_PROVIDER = createKey("org.jayware.e2.PropertyAdapterProvider");

    private static final Context.ValueProvider<ComponentStore> COMPONENT_STORE_PROVIDER_VALUE_PROVIDER = new Context.ValueProvider<ComponentStore>()
    {
        @Override
        public ComponentStore provide(Context context)
        {
            return new ComponentStore(context);
        }
    };

    private static final Context.ValueProvider<ComponentPropertyAdapterProvider> PROPERTY_ADAPTER_PROVIDER_VALUE_PROVIDER = new Context.ValueProvider<ComponentPropertyAdapterProvider>()
    {
        @Override
        public ComponentPropertyAdapterProvider provide(Context context)
        {
            return new ComponentPropertyAdapterProviderImpl();
        }
    };

    @Override
    public <T extends Component> void prepareComponent(Context context, Class<T> component)
    {
        checkNotNull(context);
        checkNotNull(component);

        final ComponentStore componentStore = getOrCreateComponentStore(context);
        componentStore.prepareComponent(component);
    }

    @Override
    public Class<? extends Component> resolveComponent(Context context, String name)
    {
        checkNotNull(context);
        checkNotNull(name);

        final ComponentStore componentStore = getOrCreateComponentStore(context);
        return componentStore.getComponentClassByName(name);
    }

    @Override
    public <T extends Component> T addComponent(EntityRef ref, Class<T> component)
    {
        checkNotNull(ref);
        checkNotNull(component);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.addComponent(ref, component);
    }

    @Override
    public <T extends Component> void removeComponent(EntityRef ref, Class<T> component)
    {
        checkNotNull(ref);
        checkNotNull(component);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        componentStore.removeComponent(ref, component);
    }

    @Override
    public <T extends Component> T getComponent(EntityRef ref, Class<T> component)
    {
        checkNotNull(ref);
        checkNotNull(component);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.getComponent(ref, component);
    }

    @Override
    public <T extends Component, W extends AbstractComponentWrapper<W, T>> W getComponent(EntityRef ref, W wrapper)
    throws ComponentNotFoundException
    {
        checkNotNull(ref);
        checkNotNull(wrapper);

        return wrapper.wrap(ref, getComponent(ref, wrapper.type()));
    }

    @Override
    public Collection<Component> getComponents(EntityRef ref)
    {
        checkNotNull(ref);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.getComponents(ref);
    }

    @Override
    public <T extends Component> T findComponent(EntityRef ref, Class<T> component)
    {
        checkNotNull(ref);
        checkNotNull(component);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.findComponent(ref, component);
    }

    @Override
    public <T extends Component> void pullComponent(EntityRef ref, T component)
    throws ComponentNotFoundException
    {
        checkNotNull(ref);
        checkNotNull(component);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        componentStore.pullComponent(ref, component);
    }

    @Override
    public <T extends Component> void pushComponent(EntityRef ref, T component)
    {
        checkNotNull(ref);
        checkNotNull(component);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        componentStore.pushComponent(ref, component);
    }

    @Override
    public boolean hasComponent(EntityRef ref, Class<? extends Component> component)
    {
        checkNotNull(ref);
        checkNotNull(component);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.hasComponent(ref, component);
    }

    @Override
    public boolean hasComponents(EntityRef ref, Collection<Class<? extends Component>> components)
    {
        checkNotNull(ref);
        checkNotNull(components);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.hasComponents(ref, components);
    }

    @Override
    public int numberOfComponents(EntityRef ref)
    {
        checkNotNull(ref);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.numberOfComponents(ref);
    }

    @Override
    public Aspect getAspect(EntityRef ref)
    {
        checkNotNull(ref);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.getAspect(ref);
    }

    @Override
    public Set<Class<? extends Component>> getComponentClasses(Context context)
    {
        checkNotNull(context);

        final ComponentStore componentStore = getOrCreateComponentStore(context);
        return componentStore.getComponentClasses();
    }

    @Override
    public void registerPropertyAdapter(Context context, Class<? extends ComponentPropertyAdapter> adapterClass)
    {
        checkNotNull(context);
        checkNotNull(adapterClass);

        final ComponentPropertyAdapterProvider adapterProvider = getOrCreatePropertyAdapterProvider(context);
        adapterProvider.registerPropertyAdapter(adapterClass);
    }

    @Override
    public void unregisterPropertyAdapter(Context context, Class<? extends ComponentPropertyAdapter> adapterClass)
    {
        checkNotNull(context);
        checkNotNull(adapterClass);

        final ComponentPropertyAdapterProvider adapterProvider = getOrCreatePropertyAdapterProvider(context);
        adapterProvider.unregisterPropertyAdapter(adapterClass);
    }

    @Override
    public <T> ComponentPropertyAdapter<T> getPropertyAdapter(Context context, Class<T> type)
    {
        checkNotNull(context);
        checkNotNull(type);

        final ComponentPropertyAdapterProvider adapterProvider = getOrCreatePropertyAdapterProvider(context);
        return adapterProvider.getAdapterFor(type);
    }

    private ComponentStore getOrCreateComponentStore(Context context)
    {
        context.putIfAbsent(COMPONENT_STORE, COMPONENT_STORE_PROVIDER_VALUE_PROVIDER);
        return context.get(COMPONENT_STORE);
    }

    private ComponentStore getOrCreateComponentStore(EntityRef ref)
    {
        return getOrCreateComponentStore(ref.getContext());
    }

    private ComponentPropertyAdapterProvider getOrCreatePropertyAdapterProvider(Context context)
    {
        context.putIfAbsent(PROPERTY_ADAPTER_PROVIDER, PROPERTY_ADAPTER_PROVIDER_VALUE_PROVIDER);
        return context.get(PROPERTY_ADAPTER_PROVIDER);
    }
}
