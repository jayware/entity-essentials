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
package org.jayware.e2.event.api;


import org.jayware.e2.util.ObjectUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;


/**
 * This class represents the parameters carried by an {@link Event}. An implementation of the event dispatch mechanism
 * may only offer an read-only view on the parameters of an event by the {@link ReadOnlyParameters} interface.
 *
 * @see Event
 * @see Parameter
 * @see ReadOnlyParameters
 *
 * @since 1.0
 */
public class Parameters
implements ReadOnlyParameters
{
    private final Map<String, Parameter> myParameters;
    private final Map<String, Parameter> myReadOnlyParameters;

    /**
     * Constructs an empty instance.
     */
    public Parameters()
    {
        myParameters = new HashMap<String, Parameter>();
        myReadOnlyParameters = unmodifiableMap(myParameters);
    }

    /**
     * Constructs an instance with copies of the specified {@link Parameters}.
     *
     * @param other the parameters for the created instance
     */
    public Parameters(Parameters other)
    {
        this();
        set(other);
    }

    /**
     * Constructs an instance with the passed {@link Parameter Parameters}.
     *
     * @param parameters the parameters for the created instance
     */
    public Parameters(Parameter[] parameters)
    {
        this();
        set(parameters);
    }

    /**
     * Creates an new {@link Parameter Parameter} with the given name and value.
     *
     * @param name the name of the {@link Parameter Parameter}.
     * @param value the value of the {@link Parameter Parameter}.
     *
     * @return the created {@link Parameter Parameter}.
     */
    public static Parameter param(String name, Object value)
    {
        return new Parameter(name, value);
    }

    /**
     * Removes all {@link Parameter Parameters}.
     */
    public void clear()
    {
        myParameters.clear();
    }

    /**
     * Sets the {@link Parameter Parameter} with the specified name to the specified value. If a parameter with the
     * specified name already exists the old value is replaced by the specified value.
     *
     * @param name the {@link Parameter Parameter's} name.
     * @param value the value to set.
     */
    public void set(String name, Object value)
    {
        if (name != null)
        {
            set(param(name, value));
        }
    }

    /**
     * Sets the specified {@link Parameter Parameter}. If a parameter with the same name already exists the parameter's
     * old value is replaced by the specified value.
     *
     * @param parameter the {@link Parameter Parameter}.
     */
    public void set(Parameter parameter)
    {
        if (parameter != null)
        {
            myParameters.put(parameter.name, parameter);
        }
    }

    /**
     * Sets the specified {@link Parameter Parameters}. This operation is equivalent to that of
     * {@link Parameters#set(Parameter)} called once for every parameter. Therefor if a parameter with the same name
     * already exists the parameter's old value is replaced by the specified value.
     *
     * @param parameters the {@link Parameter Parameters}.
     */
    public void set(Parameter[] parameters)
    {
        if (parameters != null)
        {
            for (Parameter parameter : parameters)
            {
                set(parameter);
            }
        }
    }

    /**
     * Copies all {@link Parameter Parameters} from the specified {@link Parameters}. This operation is equivalent to
     * that of {@link Parameters#set(Parameter)} called once for every parameter of the passed {@link Parameters}.
     * Therefor if a parameter with the same name already exists the parameter's old value is replaced by the specified
     * value.
     *
     * @param parameters the {@link Parameter Parameters}.
     */
    public void set(ReadOnlyParameters parameters)
    {
        if (parameters != null)
        {
            for (Parameter parameter : parameters)
            {
                set(parameter);
            }
        }
    }

    @Override
    public Object get(String name)
    {
        final Parameter param = myParameters.get(name);
        return param != null ? param.getValue() : null;
    }

    @Override
    public boolean contains(String parameter)
    {
        return myParameters.containsKey(parameter);
    }

    @Override
    public Iterator<Parameter> iterator()
    {
        return myReadOnlyParameters.values().iterator();
    }

    /**
     * This class represents a single Parameter defined as name-value-pair.
     */
    public static class Parameter
    {
        private final String name;
        private final Object value;

        private Parameter(String name, Object value)
        {
            this.name = name;
            this.value = value;
        }

        /**
         * Returns the name of this {@link Parameter Parameter}.
         *
         * @return this {@link Parameter Parameter}'s name.
         */
        public String getName()
        {
            return name;
        }

        /**
         * Returns the value of this {@link Parameter Parameter}.
         *
         * @return this {@link Parameter Parameter}'s value.
         */
        public Object getValue()
        {
            return value;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }

            if (!(obj instanceof Parameter))
            {
                return false;
            }

            final Parameter parameter = (Parameter) obj;
            return ObjectUtil.equals(name, parameter.name) &&
                   ObjectUtil.equals(value, parameter.value);
        }

        @Override
        public int hashCode()
        {
            return ObjectUtil.hashCode(name);
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder("Parameter{");
            sb.append("name='").append(name).append('\'');
            sb.append(", value=").append(value);
            sb.append('}');
            return sb.toString();
        }
    }
}
