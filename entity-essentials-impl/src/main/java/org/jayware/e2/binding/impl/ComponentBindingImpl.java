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
package org.jayware.e2.binding.impl;

import org.jayware.e2.binding.api.BindingRule;
import org.jayware.e2.binding.api.ComponentBinding;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentEvent.ComponentPushedEvent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;

import static org.jayware.e2.component.api.ComponentEvent.ComponentChangeEvent.ComponentParam;
import static org.jayware.e2.component.api.ComponentEvent.ComponentTypeParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent.EntityRefParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;


public class ComponentBindingImpl<S extends Component, T extends Component>
extends BaseBindingImpl
implements ComponentBinding<S, T>
{
    private final EntityRef mySourceRef;
    private final EntityRef myTargetRef;

    private final Class<? extends Component> mySourceComponent;
    private final Class<? extends Component> myTargetComponent;

    private final BindingRule myRule;

    public ComponentBindingImpl(EntityRef sourceRef, Class<? extends Component> sourceComponent, EntityRef targetRef, Class<? extends Component> targetComponent, BindingRule<S, T> rule)
    {
        mySourceRef = sourceRef;
        mySourceComponent = sourceComponent;
        myTargetRef = targetRef;
        myTargetComponent = targetComponent;
        myRule = rule;
    }

    @Handle(ComponentPushedEvent.class)
    public void handle(@Param(ContextParam) Context context,
                       @Param(EntityRefParam) EntityRef ref,
                       @Param(ComponentTypeParam) Class<? extends Component> componentType,
                       @Param(ComponentParam) Component source)
    {
        if (!ref.equals(mySourceRef) ||
            !componentType.equals(mySourceComponent) ||
            isDisabled())
        {
            return;
        }

        final ComponentManager componentManager = context.getComponentManager();
        final Component target = componentManager.findComponent(myTargetRef, myTargetComponent);

        if (source != null)
        {
            myRule.apply(source, target);
        }
    }

    @Override
    public String toString()
    {
        return "ComponentBindingImpl{" +
        "sourceRef=" + mySourceRef +
        ", sourceComponent=" + mySourceComponent +
        ", targetRef=" + myTargetRef +
        ", targetComponent=" + myTargetComponent +
        ", rule=" + myRule +
        '}';
    }
}
