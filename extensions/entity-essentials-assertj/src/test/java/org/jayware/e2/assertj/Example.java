/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2017 Elmar Schug <elmar.schug@jayware.org>,
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
package org.jayware.e2.assertj;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.jayware.e2.component.api.Aspect.aspect;


public class Example
{
    private Context context;
    private EntityManager entityManager;
    private ComponentManager componentManager;
    private EntityRef ref;

    public interface ComponentA extends Component {}
    public interface ComponentB extends Component {}
    public interface ComponentC extends Component {}

    @BeforeMethod
    public void setup()
    {
        context = ContextProvider.getInstance().createContext();
        entityManager = context.getService(EntityManager.class);
        componentManager = context.getService(ComponentManager.class);

        ref = entityManager.createEntity(context);
        componentManager.addComponent(ref, ComponentA.class);
        componentManager.addComponent(ref, ComponentB.class);
    }

    @Test
    public void example()
    {
        ContextAssertions.assertThat(context).isNotDisposed(); // <1>

        EntityAssertions.assertThat(ref).isValid(); // <2>

        EntityAssertions.assertThat(ref).hasAtLeast(ComponentA.class); // <3>

        EntityAssertions.assertThat(ref).hasExactly(ComponentA.class, ComponentB.class); // <4>

        EntityAssertions.assertThat(ref).doesNotHave(ComponentC.class); // <5>

        EntityAssertions.assertThat(ref).matches(aspect(ComponentA.class, ComponentB.class)); // <6>
    }
}