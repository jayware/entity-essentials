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
package org.jayware.e2.storage.impl;

import mockit.Expectations;
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.Verifications;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityEvent.EntityCreatedEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityDeletedEvent;
import org.jayware.e2.entity.api.EntityEvent.EntityDeletingEvent;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.storage.api.ComponentDatabase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class StorageImplTest
{
    private @Mocked Context testContext;
    private @Mocked EventManager testEventManager;
    private @Mocked Map<UUID, EntityRef> testSetOfEntities;
    private @Mocked ComponentDatabase testComponentDatabase;
    private @Mocked Query testQuery;
    private @Mocked EntityRef testRef;

    private final UUID testId = fromString("6a8bcaf4-82de-4ac1-b367-8b09d73fdf1c");

    private StorageImpl testee;

    @BeforeMethod
    public void setUp()
    {
        new Expectations()
        {{
            testContext.getService(EventManager.class); result = testEventManager; minTimes = 0;
        }};

        testee = new StorageImpl(testContext, testSetOfEntities, testComponentDatabase);
    }

    @Test
    public void test_that_the_Storage_creates_an_entity_on_CreateEntityEvent_and_puts_the_related_EntityRef_into_the_ResultSet_of_the_Query()
    {
        new Expectations()
        {{
            testQuery.isQuery(); result = true;
            testSetOfEntities.get(testId); result = null;
        }};

        testee.handleCreateEntityEvent(testQuery, testId);

        new Verifications()
        {{
            final EntityRef resultRef, internalRef;
            final UUID id;

            testQuery.result(EntityRefParam, resultRef = withCapture());
            testSetOfEntities.put(id = withCapture(), internalRef = withCapture());

            assertThat(resultRef)
                .withFailMessage("Result of Query to create an entity does not contain expected EntityRef referencing the newly created entity!")
                .isNotNull();

            assertThat(resultRef.getId())
                .withFailMessage("Expected EntityRef with id {%s} but got {%s}", testId, resultRef.getId())
                .isEqualTo(testId);

            assertThat(id)
                .withFailMessage("The added entity has id {%s} but {%s} was expected!", testId, id)
                .isEqualTo(testId);

            assertThat(internalRef)
                .withFailMessage("The internal stored EntityRef is not the same as the ref return within the ResultSet!")
                .isEqualTo(resultRef);
        }};
    }

    @Test
    public void test_that_the_Storage_posts_an_EntityCreatedEvent_when_an_entity_has_been_created()
    {
        new Expectations()
        {{
            testSetOfEntities.get(testId); result = null;
        }};

        testee.handleCreateEntityEvent(testQuery, testId);

        new Verifications()
        {{
            final Class<? extends RootEvent> type;
            final Parameter[] parameters;

            testEventManager.post(type = withCapture(), parameters = withCapture()); maxTimes = 1;

            assertThat(type)
                .withFailMessage("Expected %s but %s was fired!", EntityCreatedEvent.class.getSimpleName(), type.getSimpleName())
                .isEqualTo(EntityCreatedEvent.class);

            assertThat(parameters)
                .withFailMessage("Event does not carry expected ContextParam!")
                .contains(param(ContextParam, testContext));

            assertThat(parameters)
                .withFailMessage("Event does not carry expected EntityIdParam = %s", testId)
                .contains(param(EntityIdParam, testId));

            assertThat(new Parameters(parameters).contains(EntityRefParam))
                .withFailMessage("Event does not carry expected EntityRefParam!")
                .isTrue();
        }};
    }

    @Test
    public void test_that_the_Storage_does_not_post_an_EntityCreatedEvent_when_an_entity_with_the_specified_id_already_exists()
    {
        new Expectations()
        {{
            testSetOfEntities.get(testId); result = testRef;
        }};

        testee.handleCreateEntityEvent(testQuery, testId);

        new Verifications()
        {{
            testEventManager.post(EntityCreatedEvent.class, (Parameter[]) any); times = 0;
        }};
    }

    @Test
    public void test_that_the_Storage_deletes_an_entity_on_a_DeleteEntityEvent()
    {
        new Expectations()
        {{
            testSetOfEntities.get(testId); result = testRef;
        }};

        testee.handleDeleteEntityEvent(testQuery, testId);

        new Verifications()
        {{
            testSetOfEntities.remove(testId); times = 1;
        }};
    }

    @Test
    public void test_that_the_Storage_fires_a_synchronous_EntityDeletingEvent_with_expected_parameters_before_an_entity_has_been_deleted()
    {
        final List<Class<? extends RootEvent>> capturedEventTypes = new ArrayList<Class<? extends RootEvent>>();
        final List<Parameter> capturedParameters = new ArrayList<Parameter>();

        new Expectations()
        {{
            testRef.getId(); result = testId;
        }};

        new StrictExpectations()
        {{
            testSetOfEntities.get(testId); result = testRef;
            testEventManager.send(withCapture(capturedEventTypes), withCapture(capturedParameters), withCapture(capturedParameters), withCapture(capturedParameters));
            testSetOfEntities.remove(any);
            testEventManager.post((Class<? extends RootEvent>) any, (Parameter[]) any);
        }};

        testee.handleDeleteEntityEvent(testQuery, testId);

        final Parameters parameters = new Parameters(capturedParameters.toArray(new Parameter[3]));

        assertThat(capturedEventTypes.get(0))
            .withFailMessage("Expected %s but %s was fired!", EntityDeletingEvent.class.getSimpleName(), capturedEventTypes.get(0).getSimpleName())
            .isEqualTo(EntityDeletingEvent.class);

        assertThat(parameters)
            .withFailMessage("Event does not carry expected ContextParam!")
            .contains(param(ContextParam, testContext));

        assertThat(parameters)
            .withFailMessage("Event does not carry expected EntityIdParam = %s", testId)
            .contains(param(EntityIdParam, testId));

        assertThat(parameters.contains(EntityRefParam))
            .withFailMessage("Event does not carry expected EntityRefParam!")
            .isTrue();
    }

    @Test
    public void test_that_the_Storage_fires_a_asynchronous_EntityDeletedEvent_with_expected_parameters_after_an_entity_has_been_deleted()
    {
        final List<Class<? extends RootEvent>> capturedEventTypes = new ArrayList<Class<? extends RootEvent>>();
        final List<Parameter> capturedParameters = new ArrayList<Parameter>();

        new Expectations()
        {{
            testRef.getId(); result = testId;
        }};

        new StrictExpectations()
        {{
            testSetOfEntities.get(testId); result = testRef;
            testEventManager.send((Class<? extends RootEvent>) any, (Parameter[]) any);
            testSetOfEntities.remove(any);
            testEventManager.post(withCapture(capturedEventTypes), withCapture(capturedParameters), withCapture(capturedParameters), withCapture(capturedParameters));
        }};

        testee.handleDeleteEntityEvent(testQuery, testId);

        final Parameters parameters = new Parameters(capturedParameters.toArray(new Parameter[3]));

        assertThat(capturedEventTypes.get(0))
            .withFailMessage("Expected %s but %s was fired!", EntityDeletedEvent.class.getSimpleName(), capturedEventTypes.get(0).getSimpleName())
            .isEqualTo(EntityDeletedEvent.class);

        assertThat(parameters)
            .withFailMessage("Event does not carry expected ContextParam!")
            .contains(param(ContextParam, testContext));

        assertThat(parameters)
            .withFailMessage("Event does not carry expected EntityIdParam = %s", testId)
            .contains(param(EntityIdParam, testId));

        assertThat(parameters.contains(EntityRefParam))
            .withFailMessage("Event does not carry expected EntityRefParam!")
            .isTrue();
    }


}