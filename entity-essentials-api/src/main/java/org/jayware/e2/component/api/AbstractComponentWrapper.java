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
import org.jayware.e2.entity.api.EntityRef;

import java.lang.reflect.ParameterizedType;


/**
 * The <code>AbstractComponentWrapper</code> class forms a base for component wrappers and provides a couple of
 * pre-implemented operations.
 * <p>
 * <b>Note: </b> The operations provided by this class are not thread-safe! Therefore <u>instances of this class should
 * not be shared among multiple threads</u> to avoid race conditions! A thread-safe implementation has to created from
 * scratch by implementing the {@link ComponentWrapper} interface directly.
 *
 * @param <W> the type of the wrapper-class.
 * @param <C> the type of the wrapped component.
 *
 * @see Component
 * @see ComponentManager
 *
 * @since 1.0
 */
public abstract class AbstractComponentWrapper<W extends AbstractComponentWrapper, C extends Component>
implements ComponentWrapper<W, C>
{
    protected Context myContext;
    protected EntityRef myEntity;
    protected C myComponent;

    @Override
    public W wrap(EntityRef ref, C component)
    {
        invalidate();

        myContext = ref.getContext();
        myEntity = ref;
        myComponent = component;

        initialize();

        return (W) this;
    }

    @Override
    public EntityRef getEntity()
    {
        return myEntity;
    }

    @Override
    public C getComponent()
    {
        return myComponent;
    }

    @Override
    public Class<C> type()
    {
        Class<AbstractComponentWrapper<W, C>> wrapperClass = (Class<AbstractComponentWrapper<W, C>>) getClass();

        ParameterizedType parameterizedType = (ParameterizedType) wrapperClass.getGenericSuperclass();

        if (parameterizedType.getRawType().getTypeName().equals(AbstractComponentWrapper.class.getTypeName()))
        {
            return (Class) parameterizedType.getActualTypeArguments()[1];
        }

        throw new IllegalArgumentException("Failed to resolve component class from component wrapper: " + wrapperClass.getName());
    }

    @Override
    public boolean isValid()
    {
        return myContext != null && myEntity != null && myComponent != null;
    }

    @Override
    public boolean isInvalid()
    {
        return !isValid();
    }

    @Override
    public W pull()
    {
        pullFrom(myEntity);
        return (W) this;
    }

    @Override
    public W push()
    {
        pushTo(myEntity);
        return (W) this;
    }

    public void pullFrom(EntityRef ref)
    {
        myComponent.pullFrom(ref);
        postPull();
    }

    public void pushTo(EntityRef ref)
    {
        prePush();
        myComponent.pushTo(myEntity);
    }

    /**
     * Every time called <u>after</u> <code>this</code> {@link AbstractComponentWrapper} wraps up a new {@link Component}.
     * <p>
     * <b>Note:</b> This operation is intended to be overwritten to react on the certain event.
     */
    protected void initialize() {}

    /**
     * Every time called <u>before</u> <code>this</code> {@link AbstractComponentWrapper} wraps up a new {@link Component}.
     * <p>
     * <b>Note:</b> This operation is intended to be overwritten to react on the certain event.
     */
    protected void invalidate() {}

    /**
     * Every time called <u>after</u> <code>this</code> {@link AbstractComponentWrapper omponentWrapper's} {@link Component}
     * has been updated.
     * <p>
     * <b>Note:</b> This operation is intended to be overwritten to react on the certain event.
     */
    protected void postPull() {}

    /**
     * Every time called <u>before</u> <code>this</code> {@link AbstractComponentWrapper ComponentWrapper's} {@link Component}
     * is delivered.
     * <p>
     * <b>Note:</b> This operation is intended to be overwritten to react on the certain event.
     */
    protected void prePush() {}
}
