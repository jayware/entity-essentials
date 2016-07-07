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

import com.google.common.base.Objects;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.Entity;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;


/**
 * An <code>Aspect</code> is a set of {@link Component Components} describing together a specific characteristic.
 * <p>
 * An {@link Entity} fulfills an certain <code>Aspect</code> if it has at least the <code>Aspect's</code>
 * {@link Component Components}.
 * <p>
 *
 * @see Entity
 * @see EntityManager
 * @see Component
 *
 * @since 1.0
 */
public class Aspect
{
    /**
     * Matches always any {@link Entity}.
     */
    public static final Aspect ANY = new Aspect()
    {
        public boolean matches(EntityRef ref)
        {
            return true;
        }

        @Override
        public boolean satisfies(Aspect other)
        {
            return true;
        }

        @Override
        public boolean contains(Class<? extends Component> component)
        {
            return true;
        }
    };

    /**
     * Matches {@link Entity Entities} which doesn't have any {@link Component}.
     */
    public static final Aspect EMPTY = new Aspect()
    {
        public boolean matches(EntityRef ref)
        {
            final ComponentManager componentManager = ref.getContext().getService(ComponentManager.class);
            return componentManager.numberOfComponents(ref) == 0;
        }

        @Override
        public boolean satisfies(Aspect other)
        {
            return false;
        }

        @Override
        public boolean contains(Class<? extends Component> component)
        {
            return false;
        }
    };

    /**
     * Matches never.
     */
    public static final Aspect NONE = new Aspect()
    {
        public boolean matches(EntityRef ref)
        {
            return false;
        }

        @Override
        public boolean satisfies(Aspect other)
        {
            return false;
        }

        @Override
        public boolean contains(Class<? extends Component> component)
        {
            return false;
        }
    };

    private final Set<Class<? extends Component>> myComponentSet;

    private Aspect()
    {
        this(Collections.<Class<? extends Component>>emptySet());
    }

    private Aspect(Set<Class<? extends Component>> set)
    {
        myComponentSet = unmodifiableSet(set);
    }

    /**
     * Creates an {@link Aspect} from the passed {@link Component} types.
     *
     * @param components the {@link Component Components}.
     *
     * @return a new {@link Aspect}.
     */
    public static Aspect aspect(Class<? extends Component>... components)
    {
        return new Aspect(new HashSet<>(asList(components)));
    }

    /**
     * Creates an {@link Aspect} from the specified {@link Set} of {@link Component} types.
     *
     * @param components the {@link Component Components}.
     *
     * @return a new {@link Aspect}.
     */
    public static Aspect aspect(Set<Class<? extends Component>> components)
    {
        return new Aspect(new HashSet<>(components));
    }

    public static Aspect combine(Aspect a, Aspect b)
    {
        final Set<Class<? extends Component>> resultSet = new HashSet<>();
        resultSet.addAll(a.myComponentSet);
        resultSet.addAll(b.myComponentSet);

        return new Aspect(resultSet);
    }

    public Aspect intersect(Aspect a, Aspect b)
    {
        final Set<Class<? extends Component>> resultSet = new HashSet<>();

        for (Class<? extends Component> component : a.myComponentSet)
        {
            if (b.myComponentSet.contains(component))
            {
                resultSet.add(component);
            }
        }

        if (resultSet.isEmpty())
        {
            return EMPTY;
        }

        return new Aspect(resultSet);
    }

    public Aspect add(Aspect aspect)
    {
        return combine(this, aspect);
    }

    /**
     * Adds the specified type of {@link Component}.
     *
     * @param component the type of {@link Component}.
     *
     * @return a new {@link Aspect}.
     */
    public Aspect add(Class<? extends Component> component)
    {
        final Set<Class<? extends Component>> resultSet = new HashSet<>(myComponentSet);
        resultSet.add(component);

        return new Aspect(resultSet);
    }

    public Aspect remove(Aspect aspect)
    {
        final Set<Class<? extends Component>> resultSet = new HashSet<>(myComponentSet);
        resultSet.removeAll(aspect.myComponentSet);

        if (resultSet.isEmpty())
        {
            return EMPTY;
        }

        return new Aspect(resultSet);
    }

    /**
     * Removes the specified type of {@link Component}.
     *
     * @param component the type of {@link Component}.
     *
     * @return a new {@link Aspect}.
     */
    public Aspect remove(Class<? extends Component> component)
    {
        if (myComponentSet.contains(component))
        {
            final Set<Class<? extends Component>> resultSet = new HashSet<>(myComponentSet);
            resultSet.remove(component);

            if (resultSet.isEmpty())
            {
                return EMPTY;
            }

            return new Aspect(resultSet);
        }

        return this;
    }

    public Aspect intersect(Aspect aspect)
    {
        return intersect(this, aspect);
    }

    /**
     * Returns whether the {@link Entity} referenced by the specified {@link EntityRef} matches this {@link Aspect}.
     *
     * @param ref an {@link EntityRef}
     *
     * @return true if the {@link Entity} matches this {@link Aspect}, otherwise false.
     */
    public boolean matches(EntityRef ref)
    {
        final Context context = ref.getContext();
        final ComponentManager componentManager = context.getComponentManager();

        return componentManager.hasComponents(ref, myComponentSet);
    }

    /**
     * Returns whether this {@link Aspect} satisfies the specified {@link Aspect}.
     * <p>
     * An {@link Aspect} A satisfies an {@link Aspect} B if and only if A encompasses at least all components of B.
     * <p>
     * Example: <b>A</b> {c1}, <b>B</b> {c1, c2, c3, c4}, <b>C</b> {c1, c3}<br>
     * <table>
     *     <tr><th></th><th>A</th><th>B</th><th>C</th><th></th><th>Description</th></tr>
     *     <tr><th>A</th><td>x</td><td></td><td></td><td></td><td><b>A</b> doesn't satisfy <b>B</b> nor <b>C</b>, because <b>A</b> doesn't contain c2, c3 and c4.<br></td></tr>
     *     <tr><th>B</th><td>x</td><td>x</td><td>x</td><td></td><td><b>B</b> satisfies <b>A</b> and <b>C</b> because <b>B</b> contains c1 and c3.</td></tr>
     *     <tr><th>C</th><td>x</td><td></td><td>x</td><td></td><td><b>C</b> satisfies <b>A</b> but not <b>B</b>, because <b>C</b> contains c1 but not c2 and c4.</td></tr>
     *     <caption>resulting table</caption>
     * </table>
     *
     * @param other an {@link Aspect}.
     *
     * @return <code>true</code> if <code>this</code> {@link Aspect} satisfies the other {@link Aspect},
     *         otherwise <code>false</code>.
     */
    public boolean satisfies(Aspect other)
    {
        if (other.equals(ANY) || other.equals(EMPTY))
        {
            return true;
        }
        else if (other.equals(NONE))
        {
            return false;
        }

        return myComponentSet.containsAll(other.myComponentSet);
    }

    public boolean contains(Class<? extends Component> component)
    {
        return myComponentSet.contains(component);
    }

    public Set<Class<? extends Component>> components()
    {
        return myComponentSet;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Aspect))
        {
            return false;
        }

        final Aspect other = (Aspect) obj;

        return myComponentSet.containsAll(other.myComponentSet) &&
               other.myComponentSet.containsAll(myComponentSet);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(myComponentSet);
    }

    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder("Aspect { ");
        for (Class<? extends Component> component : myComponentSet)
        {
            result.append(component.getSimpleName()).append(" ");
        }
        result.append("}");
        return result.toString();
    }
}