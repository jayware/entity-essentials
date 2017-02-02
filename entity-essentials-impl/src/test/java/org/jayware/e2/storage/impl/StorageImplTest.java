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
import org.jayware.e2.storage.api.StorageException;
import org.jayware.e2.util.Filter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.jayware.e2.component.api.Aspect.ANY;
import static org.jayware.e2.component.api.Aspect.aspect;
import static org.jayware.e2.entity.api.EntityEvent.EntityIdParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefListParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefParam;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;


public class StorageImplTest
{
    private @Mocked Context testContext;
    private @Mocked EventManager testEventManager;
    private @Mocked Map<UUID, EntityRef> testMapOfEntities;
    private @Mocked ComponentDatabase testComponentDatabase;
    private @Mocked Query testQuery;
    private @Mocked EntityRef testRefA, testRefB, testRefC;

    private final UUID testId = fromString("6a8bcaf4-82de-4ac1-b367-8b09d73fdf1c");

    private StorageImpl testee;

    @BeforeMethod
    public void setUp()
    {
        new Expectations()
        {{
            testContext.getService(EventManager.class); result = testEventManager; minTimes = 0;
        }};

        testee = new StorageImpl(testContext, testMapOfEntities, testComponentDatabase);
    }

    @Test
    public void test_that_the_Storage_creates_an_entity_on_CreateEntityEvent_and_puts_the_related_EntityRef_into_the_ResultSet_of_the_Query()
    {
        new Expectations()
        {{
            testQuery.isQuery(); result = true;
            testMapOfEntities.get(testId); result = null;
        }};

        testee.handleCreateEntityEvent(testQuery, testId);

        new Verifications()
        {{
            final EntityRef resultRef, internalRef;
            final UUID id;

            testQuery.result(EntityRefParam, resultRef = withCapture());
            testMapOfEntities.put(id = withCapture(), internalRef = withCapture());

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
            testMapOfEntities.get(testId); result = null;
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
            testMapOfEntities.get(testId); result = testRefA;
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
            testMapOfEntities.get(testId); result = testRefA;
        }};

        testee.handleDeleteEntityEvent(testQuery, testId);

        new Verifications()
        {{
            testMapOfEntities.remove(testId); times = 1;
        }};
    }

    @Test
    public void test_that_the_Storage_fires_a_synchronous_EntityDeletingEvent_with_expected_parameters_before_an_entity_has_been_deleted()
    {
        final List<Class<? extends RootEvent>> capturedEventTypes = new ArrayList<Class<? extends RootEvent>>();
        final List<Parameter> capturedParameters = new ArrayList<Parameter>();

        new Expectations()
        {{
            testRefA.getId(); result = testId;
        }};

        new StrictExpectations()
        {{
            testMapOfEntities.get(testId); result = testRefA;
            testEventManager.send(withCapture(capturedEventTypes), withCapture(capturedParameters), withCapture(capturedParameters), withCapture(capturedParameters));
            testMapOfEntities.remove(any);
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
            testRefA.getId(); result = testId;
        }};

        new StrictExpectations()
        {{
            testMapOfEntities.get(testId); result = testRefA;
            testEventManager.send((Class<? extends RootEvent>) any, (Parameter[]) any);
            testMapOfEntities.remove(any);
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

    @Test
    public void test_that_findEntities_Returns_the_expected_EntityRefs()
    {
        final List<String> capturedResultKeys = new ArrayList<String>();
        final List<Object> capturedResultValues = new ArrayList<Object>();
        final Object capturedParameterObject;
        final int indexOfEntityRefListParam;

        new Expectations() {{
            testMapOfEntities.values(); result = Arrays.<EntityRef>asList(testRefA, testRefB, testRefC);
            testQuery.result(withCapture(capturedResultKeys), withCapture(capturedResultValues));
        }};

        testee.handleFindEntityEvent(testQuery, aspect(), Collections.<Filter<EntityRef>>emptyList());

        indexOfEntityRefListParam = capturedResultKeys.indexOf(EntityRefListParam);

        assertThat(indexOfEntityRefListParam)
            .withFailMessage("Query does not contain the expected EntityRefListParam!")
            .isGreaterThanOrEqualTo(0);

        capturedParameterObject = capturedResultValues.get(indexOfEntityRefListParam);

        assertThat(capturedParameterObject)
            .withFailMessage("Expected EntityRefListParam: '%s' to be of type %s, but was %s", capturedParameterObject, List.class.getName(), capturedParameterObject.getClass().getName())
            .isInstanceOf(List.class);

        assertThat((List<EntityRef>) capturedParameterObject)
            .containsExactlyInAnyOrder(testRefA, testRefB, testRefC);
    }

    @Test
    public void testName()
    {
        final List<String> capturedResultKeys = new ArrayList<String>();
        final List<Object> capturedResultValues = new ArrayList<Object>();

        new Expectations() {{
            testMapOfEntities.values(); result = Arrays.<EntityRef>asList(testRefA, testRefB, testRefC);
            testQuery.result(withCapture(capturedResultKeys), withCapture(capturedResultValues));
        }};

        try
        {
            testee.handleFindEntityEvent(testQuery, ANY, Arrays.<Filter<EntityRef>>asList(new Filter<EntityRef>()
            {
                @Override
                public boolean accepts(final Context context, final EntityRef element)
                {
                    throw new RuntimeException("Got it!");
                }
            }));

            fail("Expected a RuntimeException!");
        }
        catch (StorageException ignored) {}
    }
}