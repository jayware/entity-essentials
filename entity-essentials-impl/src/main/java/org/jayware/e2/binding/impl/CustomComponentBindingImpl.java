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
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;

import static org.jayware.e2.component.api.ComponentEvent.ComponentChangeEvent.ComponentParam;
import static org.jayware.e2.component.api.ComponentEvent.ComponentTypeParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent.EntityRefParam;


public class CustomComponentBindingImpl<S extends Component, T>
extends BaseBindingImpl
implements ComponentBinding<S, T>
{
    private final EntityRef mySourceRef;
    private final Object myTarget;

    private final Class<? extends Component> mySourceComponent;

    private final BindingRule myRule;

    public CustomComponentBindingImpl(EntityRef sourceRef, Class<? extends Component> sourceComponent, Object target, BindingRule<S, T> rule)
    {
        mySourceRef = sourceRef;
        mySourceComponent = sourceComponent;
        myTarget = target;
        myRule = rule;
    }

    @Handle(ComponentPushedEvent.class)
    public void handle(@Param(EntityRefParam) EntityRef ref,
                       @Param(ComponentTypeParam) Class<? extends Component> componentType,
                       @Param(ComponentParam) Component source)
    {
        if (!ref.equals(mySourceRef) ||
            !componentType.equals(mySourceComponent) ||
            isDisabled())
        {
            return;
        }

        myRule.apply(source, myTarget);
    }

    @Override
    public String toString()
    {
        return "ComponentBindingImpl{" +
        "sourceRef=" + mySourceRef +
        ", sourceComponent=" + mySourceComponent +
        ", target=" + myTarget +
        ", rule=" + myRule +
        '}';
    }
}
