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
package org.jayware.e2.storage.impl;

import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.impl.ComponentManagerImpl;
import org.jayware.e2.component.impl.ComponentStore;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityEvent.CreateEntityEvent;
import org.jayware.e2.entity.api.EntityEvent.DeleteEntityEvent;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.util.Filter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.UUID.fromString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Query.State.Success;


public class StorageImplIntegrationTest
{
    private Context context;
    private EventManager eventManager;
    private EntityManager entityManager;
    private ComponentManager componentManager;

    private final UUID testId = fromString("6a8bcaf4-82de-4ac1-b367-8b09d73fdf1c");

    @BeforeMethod
    public void setUp()
    {
        context = ContextProvider.getInstance().createContext();
        eventManager = context.getService(EventManager.class);
        entityManager = context.getService(EntityManager.class);
        componentManager = context.getService(ComponentManager.class);

        // Disable the current implementations!
        final ComponentStore componentStore = context.get(ComponentManagerImpl.COMPONENT_STORE);
        eventManager.unsubscribe(context, componentStore);
    }

    @AfterMethod
    public void tearDown()
    {
        context.dispose();
    }

    @Test
    public void test_create_a_specific_entity_by_CreateEntityEvent()
    {
        final EntityRef ref;

        final ResultSet result = eventManager.query(CreateEntityEvent.class,
            param(ContextParam, context),
            param(EntityIdParam, testId)
        );

        if (result.await(Success, 5, SECONDS))
        {
            ref = result.find(EntityRefParam);

            assertThat(ref)
                .withFailMessage("Result does not contain expected EntityRefParam!")
                .isNotNull();

            assertThat(ref.getId())
                .withFailMessage("Returned EntityRef does not reference the entity with id {%s}!", testId)
                .isEqualTo(testId);
        }
    }

    @Test
    public void test_create_an_entity_by_CreateEntityEvent()
    {
        final EntityRef ref;

        final ResultSet result = eventManager.query(CreateEntityEvent.class,
            param(ContextParam, context)
        );

        if (result.await(Success, 5, SECONDS))
        {
            ref = result.find(EntityRefParam);

            assertThat(ref)
                .withFailMessage("Result does not contain expected EntityRefParam!")
                .isNotNull();
        }
    }

    @Test
    public void test_delete_an_entity_by_DeleteEntityEvent()
    {
        final EntityRef ref = entityManager.createEntity(context);

        eventManager.send(DeleteEntityEvent.class,
            param(ContextParam, context),
            param(EntityRefParam, ref),
            param(EntityIdParam, ref.getId())
        );

        assertThat(ref.isInvalid())
            .withFailMessage("Expected an EntityRef to be invalid when the corresponding entity has been delete!")
            .isTrue();
    }

    @Test
    public void test_create_an_entity_by_EntityManager()
    {
        assertThat(entityManager.createEntity(context)).isNotNull();
    }

    @Test
    public void test_delete_an_entity_by_EntityManager()
    {
        final EntityRef ref = entityManager.createEntity(context);

        entityManager.deleteEntity(ref);

        assertThat(ref.isInvalid())
            .withFailMessage("Expected an EntityRef to be invalid when the corresponding entity has been delete!")
            .isTrue();
    }

    @Test
    public void test_find_all_entities_by_EntityManager()
    {
        final EntityRef a = entityManager.createEntity(context);
        final EntityRef b = entityManager.createEntity(context);

        final List<EntityRef> result = entityManager.findEntities(context);

        assertThat(result)
            .withFailMessage("Expected that the result contains the previously created entities: %s", asList(a, b))
            .containsExactlyInAnyOrder(a, b);
    }

    @Test
    public void test_find_entities_with_aspect_and_filter_by_EntityManager()
    {
        final EntityRef a = entityManager.createEntity(context);
        final EntityRef b = entityManager.createEntity(context);

        List<EntityRef> result = entityManager.findEntities(context, Aspect.ANY, new Filter<EntityRef>()
        {
            @Override
            public boolean accepts(final Context context, final EntityRef ref)
            {
                return false;
            }
        });

        assertThat(result)
            .withFailMessage("Expected that the result is empty because the filter rejected it all!")
            .isEmpty();

        result = entityManager.findEntities(context, Aspect.ANY, new Filter<EntityRef>()
        {
            @Override
            public boolean accepts(final Context context, final EntityRef ref)
            {
                return ref.equals(b);
            }
        });

        assertThat(result)
            .withFailMessage("Expected that the result contains only %s, because the filter accepts only this entity, but it contains: %s!", b, result)
            .containsExactlyInAnyOrder(b);
    }
}