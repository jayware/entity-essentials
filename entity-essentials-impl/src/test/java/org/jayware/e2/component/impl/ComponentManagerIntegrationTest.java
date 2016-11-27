/**
 * Entity Essentials -- A Component-based Entity System
 *
 * Copyright (C) 2016 Elmar Schug <elmar.schug@jayware.org>,
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

import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.component.impl.TestComponents.TestComponentB;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ComponentManagerIntegrationTest
{
    private final String testRefId = "e2b94185-38e2-4a0f-abfc-f8ef4fb4e92b";

    private Context context;
    private EntityManager entityManager;
    private ComponentManager componentManager;

    @BeforeMethod
    public void setUp()
    {
        context = ContextProvider.getInstance().createContext();
        entityManager = context.getService(EntityManager.class);
        componentManager = context.getService(ComponentManager.class);
    }

    @AfterMethod
    public void tearDown()
    {
        context.dispose();
    }

    @Test
    public void test()
    {
        final EntityRef entity = entityManager.createEntity(context);

        componentManager.addComponent(entity, TestComponentA.class).pushTo(entity);
        componentManager.createComponent(context, TestComponentB.class).addTo(entity);

        assertThat(componentManager.hasComponent(entity, TestComponentA.class))
            .withFailMessage("Entity does not have a TestComponentA")
            .isTrue();

        assertThat(componentManager.hasComponent(entity, TestComponentB.class))
            .withFailMessage("Entity does not have a TestComponentB")
            .isTrue();
    }
}