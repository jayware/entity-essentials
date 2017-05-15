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
package org.jayware.e2.component.impl;

import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.impl.TestComponents.TestComponentA;
import org.jayware.e2.component.impl.TestComponents.TestComponentB;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ComponentManagerIntegrationTest
{
    private final String testRefId = "e2b94185-38e2-4a0f-abfc-f8ef4fb4e92b";

    private Context context;
    private EntityManager entityManager;
    private ComponentManager componentManager;

    @BeforeEach
    public void setUp()
    {
        context = ContextProvider.getInstance().createContext();
        entityManager = context.getService(EntityManager.class);
        componentManager = context.getService(ComponentManager.class);
    }

    @AfterEach
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