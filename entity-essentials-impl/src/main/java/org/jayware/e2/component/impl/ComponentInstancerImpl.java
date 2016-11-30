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

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentInstancer;
import org.jayware.e2.component.api.ComponentInstantiationException;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.ComponentProperty;
import org.jayware.e2.component.api.ComponentPropertyAdapter;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.jayware.e2.context.api.Context;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;


public class ComponentInstancerImpl<C extends Component, T extends C>
implements ComponentInstancer<C>
{
    private final ComponentGenerationPlan myComponentGenerationPlan;
    private final Class<T> myComponentClass;
    private final Set<Class<? extends ComponentPropertyAdapter>> myRequiredAdaptersSet;

    public ComponentInstancerImpl(ComponentGenerationPlan componentGenerationPlan, Class<? extends Component> componentClass)
    {
        myComponentGenerationPlan = componentGenerationPlan;
        myComponentClass = (Class<T>) componentClass;
        myRequiredAdaptersSet = new HashSet<Class<? extends ComponentPropertyAdapter>>();

        for (ComponentPropertyGenerationPlan propertyPlan : componentGenerationPlan.getComponentPropertyGenerationPlans())
        {
            ComponentProperty property = propertyPlan.getPropertyAnnotation();
            if (property != null && !property.adapter().equals(ComponentProperty.DefaultAdapter.class))
            {
                myRequiredAdaptersSet.add(property.adapter());
            }
        }
    }

    @Override
    public C newInstance(Context context)
    {
        try
        {
            final Constructor<C> constructor = (Constructor<C>) myComponentClass.getConstructor(Context.class);
            final C instance = constructor.newInstance(context);

            final ComponentManager componentManager = context.getService(ComponentManager.class);
            for (Class<? extends ComponentPropertyAdapter> adapter : myRequiredAdaptersSet)
            {
                componentManager.registerPropertyAdapter(context, adapter);
            }

            return instance;
        }
        catch (Exception e)
        {
            throw new ComponentInstantiationException(e);
        }
    }
}
