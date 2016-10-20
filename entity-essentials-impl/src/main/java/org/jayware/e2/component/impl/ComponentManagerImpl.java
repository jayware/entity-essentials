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

import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.AbstractComponentWrapper;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentEvent.AddComponentEvent;
import org.jayware.e2.component.api.ComponentEvent.ComponentTypesQuery;
import org.jayware.e2.component.api.ComponentEvent.CreateComponentEvent;
import org.jayware.e2.component.api.ComponentEvent.PrepareComponentEvent;
import org.jayware.e2.component.api.ComponentEvent.RemoveComponentEvent;
import org.jayware.e2.component.api.ComponentFactory;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.ComponentManagerException;
import org.jayware.e2.component.api.ComponentNotFoundException;
import org.jayware.e2.component.api.ComponentPropertyAdapter;
import org.jayware.e2.component.api.ComponentPropertyAdapterProvider;
import org.jayware.e2.component.api.ContextualComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.util.Key;
import org.jayware.e2.util.TimeoutException;

import java.util.Collection;
import java.util.Set;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jayware.e2.component.api.ComponentEvent.ComponentParam;
import static org.jayware.e2.component.api.ComponentEvent.ComponentTypeCollectionParam;
import static org.jayware.e2.component.api.ComponentEvent.ComponentTypeParam;
import static org.jayware.e2.context.api.Preconditions.checkContextNotNullAndNotDisposed;
import static org.jayware.e2.context.api.Preconditions.checkContextualsNotNullAndSameContext;
import static org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent.EntityRefParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.Preconditions.checkRefNotNullAndValid;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Query.State.Success;
import static org.jayware.e2.util.Key.createKey;
import static org.jayware.e2.util.Preconditions.checkNotNull;


public class ComponentManagerImpl
implements ComponentManager
{
    private static final long TIMEOUT_IN_MILLIS = 5000;

    public static final Key<ComponentStore> COMPONENT_STORE = createKey("org.jayware.e2.ComponentStore");
    public static final Key<ComponentFactory> COMPONENT_FACTORY = createKey("org.jayware.e2.ComponentFactory");
    public static final Key<ComponentPropertyAdapterProvider> PROPERTY_ADAPTER_PROVIDER = createKey("org.jayware.e2.PropertyAdapterProvider");

    @Override
    public <T extends Component> T createComponent(Context context, Class<T> type)
    {
        checkNotNull(type);
        checkContextNotNullAndNotDisposed(context);

        getOrCreateComponentStore(context);

        try
        {
            final EventManager eventManager = context.getService(EventManager.class);
            final ResultSet resultSet = eventManager.query(CreateComponentEvent.class,
                param(ContextParam, context),
                param(ComponentTypeParam, type)
            );

            if (!resultSet.await(Success, TIMEOUT_IN_MILLIS, MILLISECONDS))
            {
                throw new TimeoutException("Query did not succeed within " + TIMEOUT_IN_MILLIS + "ms");
            }

            return resultSet.get(ComponentParam);
        }
        catch (Exception e)
        {
            throw new ComponentManagerException("Failed to create Component '" + type.getSimpleName() + "'", e);
        }
    }

    @Override
    public <T extends Component> void prepareComponent(Context context, Class<T> component)
    {
        checkNotNull(context);
        checkNotNull(component);

        final EventManager eventManager = context.getService(EventManager.class);
        eventManager.send(PrepareComponentEvent.class,
            param(ContextParam, context),
            param(ComponentTypeParam, component)
        );
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
        checkRefNotNullAndValid(ref);
        checkNotNull(component);

        try
        {
            final Context context = checkContextNotNullAndNotDisposed(ref.getContext());
            final EventManager eventManager = context.getService(EventManager.class);
            final ResultSet resultSet = eventManager.query(AddComponentEvent.class,
                param(ContextParam, context),
                param(EntityRefParam, ref),
                param(EntityIdParam, ref.getId()),
                param(ComponentTypeParam, component)
            );

            if (!resultSet.await(Success, TIMEOUT_IN_MILLIS, MILLISECONDS))
            {
                throw new TimeoutException("Query did not succeed within " + TIMEOUT_IN_MILLIS + "ms");
            }

            return resultSet.get(ComponentParam);
        }
        catch (Exception e)
        {
            throw new ComponentManagerException("Failed to add Component '" + component.getSimpleName() + "' to entity {" + ref.getId() + "}", e);
        }
    }

    @Override
    public <T extends Component> T addComponent(EntityRef ref, T component)
    {
        checkRefNotNullAndValid(ref);
        checkContextualsNotNullAndSameContext(ref, (AbstractComponent) component);

        final Context context = checkContextNotNullAndNotDisposed(ref.getContext());
        final EventManager eventManager = context.getService(EventManager.class);

        try
        {
            final ResultSet resultSet = eventManager.query(AddComponentEvent.class,
                param(ContextParam, context),
                param(EntityRefParam, ref),
                param(EntityIdParam, ref.getId()),
                param(ComponentTypeParam, component.type()),
                param(ComponentParam, component)
            );

            if (!resultSet.await(Success, TIMEOUT_IN_MILLIS, MILLISECONDS))
            {
                throw new TimeoutException("Query did not succeed within " + TIMEOUT_IN_MILLIS + "ms");
            }

            return resultSet.get(ComponentParam);
        }
        catch (Exception e)
        {
            throw new ComponentManagerException("Failed to add Component '" + component.type().getSimpleName() + "' to entity {" + ref.getId() + "}", e);
        }
    }

    @Override
    public <T extends Component> T removeComponent(EntityRef ref, Class<T> component)
    {
        checkRefNotNullAndValid(ref);
        checkNotNull(component);

        final Context context = ref.getContext();
        final EventManager eventManager = context.getService(EventManager.class);

        try
        {
            final ResultSet result = eventManager.query(RemoveComponentEvent.class,
                param(ContextParam, context),
                param(EntityRefParam, ref),
                param(EntityIdParam, ref.getId()),
                param(ComponentTypeParam, component)
            );

            if (!result.await(Success, TIMEOUT_IN_MILLIS, MILLISECONDS))
            {
                throw new TimeoutException("Query did not succeed within " + TIMEOUT_IN_MILLIS + "ms");
            }

            return result.find(ComponentParam);
        }
        catch (Exception e)
        {
            throw new ComponentManagerException("Failed to remove Component '" + component.getSimpleName() + "' from entity {" + ref.getId() + "}", e);
        }
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
    public Collection<Class<? extends Component>> getComponentTypes(EntityRef ref)
    {
        checkRefNotNullAndValid(ref);

        final Context context = ref.getContext();
        final EventManager eventManager = context.getService(EventManager.class);

        try
        {
            final ResultSet resultSet = eventManager.query(ComponentTypesQuery.class,
                param(ContextParam, context),
                param(EntityRefParam, ref)
            );

            resultSet.timeout(Success, TIMEOUT_IN_MILLIS, "Failed to query all types of components associated to %s within %sms", ref, TIMEOUT_IN_MILLIS);

            return resultSet.get(ComponentTypeCollectionParam);
        }
        catch (org.jayware.e2.util.TimeoutException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ComponentManagerException(e, "Failed to query all types of components associated to %s", ref);
        }
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
    public int getNumberOfComponents(EntityRef ref)
    {
        checkNotNull(ref);

        final ComponentStore componentStore = getOrCreateComponentStore(ref);
        return componentStore.numberOfComponents(ref);
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

    @Override
    public ContextualComponentManager asContextual(Context context)
    {
        checkContextNotNullAndNotDisposed(context);
        return new ContextualComponentManagerImpl(context, this);
    }

    private ComponentStore getOrCreateComponentStore(Context context)
    {
        return context.get(COMPONENT_STORE);
    }

    private ComponentStore getOrCreateComponentStore(EntityRef ref)
    {
        return getOrCreateComponentStore(ref.getContext());
    }

    private ComponentPropertyAdapterProvider getOrCreatePropertyAdapterProvider(Context context)
    {
        return context.get(PROPERTY_ADAPTER_PROVIDER);
    }
}
