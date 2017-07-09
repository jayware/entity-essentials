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


import org.jayware.e2.context.api.Context;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Parameters.Parameter;
import org.jayware.e2.util.ReferenceType;

import java.lang.ref.WeakReference;


/**
 * The <code>EventManager</code> is the heart of the framework's event-system.
 *
 * @see Event
 * @see Query
 * @see Handle
 */
public interface EventManager
{
    /**
     * Creates an {@link Event} with the specified {@link EventType}.
     *
     * @param type the event's {@link EventType}.
     *
     * @return an {@link EventBuilder} to set additional {@link Parameters}.
     *
     * @throws IllegalArgumentException if the passed {@link EventType} is null.
     */
    EventBuilder createEvent(Class<? extends RootEvent> type) throws IllegalArgumentException;

    /**
     * Creates an {@link Event} with the specified {@link EventType} and the passed {@link Parameter Parameters}.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameter Parameters} to set to the {@link Event}
     *
     * @return the newly created {@link Event}.
     *
     * @throws IllegalArgumentException if the passed {@link EventType} is null.
     */
    Event createEvent(Class<? extends RootEvent> type, Parameter... parameters) throws IllegalArgumentException;

    /**
     * Creates an {@link Event} with the specified {@link EventType} and the passed {@link Parameters}.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameters} to set to the {@link Event}
     *
     * @return the newly created {@link Event}.
     *
     * @throws IllegalArgumentException if the passed {@link EventType} is null.
     */
    Event createEvent(Class<? extends RootEvent> type, Parameters parameters) throws IllegalArgumentException;

    /**
     * Creates an {@link Query} with the specified {@link EventType}.
     *
     * @param type the event's {@link EventType}.
     *
     * @return an {@link QueryBuilder} to set additional {@link Parameters}.
     *
     * @throws IllegalArgumentException if the passed {@link EventType} is null.
     */
    QueryBuilder createQuery(Class<? extends RootEvent> type) throws IllegalArgumentException;

    /**
     * Creates a {@link Query} with the specified {@link EventType} and the passed {@link Parameters}.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameters} to set to the {@link Event}
     *
     * @return the newly created {@link Event}.
     *
     * @throws IllegalArgumentException if the passed {@link EventType} is null.
     */
    Query createQuery(Class<? extends RootEvent> type, Parameter... parameters) throws IllegalArgumentException;

    /**
     * Creates a {@link Query} with the specified {@link EventType} and the passed {@link Parameters}.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameters} to set to the {@link Event}
     *
     * @return the newly created {@link Event}.
     *
     * @throws IllegalArgumentException if the passed {@link EventType} is null.
     */
    Query createQuery(Class<? extends RootEvent> type, Parameters parameters) throws IllegalArgumentException;

    /**
     * Subscribes the specified {@link Object} for {@link Event Events} occurring in the specified {@link Context}.
     * <p>
     * The {@link Class} of the subscriber has to have at least one method annotated as {@link Handle @Handle}.
     * <p>
     * <b>Note: </b> To avoid memory leaks due to undead subscribers, an <code>EventManager</code> implementation may
     * use {@link WeakReference} for every subscriber. Therefore, if no other object holds an reference to the subscriber
     * object, the subscriber object will be garbage collected. To prevent the object from being garbage collected use
     * one of {@link EventManager#subscribe(Context, Object, ReferenceType)} or {@link EventManager#subscribe(Context, Object, ReferenceType, EventFilter...)}
     * and pass {@link ReferenceType#Strong} as {@link ReferenceType} parameter. This forces an <code>EventManager</code>
     * implementation to use strong references instead of weak ones. But be advised, <u>subscriptions with strong references
     * have to be revoked manually to avoid memory leaks.</u>
     *
     * @param context    a {@link Context}.
     * @param subscriber any {@link Object}.
     */
    void subscribe(Context context, Object subscriber);

    /**
     * Subscribes the specified {@link Object} for {@link Event Events} occurring in the specified {@link Context}
     * in the same way as {@link EventManager#subscribe(Context, Object)} does expect that before an {@link Event} is
     * dispatched to the specified subscriber the passed {@link EventFilter Filters} are applied.
     * <p>
     * The {@link Class} of the subscriber has to have at least one method annotated as {@link Handle @Handle}.
     * <p>
     * <b>Note: </b> To avoid memory leaks due to undead subscribers, an <code>EventManager</code> implementation may
     * use {@link WeakReference} for every subscriber. Therefore, if no other object holds an reference to the subscriber
     * object, the subscriber object will be garbage collected. To prevent the object from being garbage collected use
     * one of {@link EventManager#subscribe(Context, Object, ReferenceType)} or {@link EventManager#subscribe(Context, Object, ReferenceType, EventFilter...)}
     * and pass {@link ReferenceType#Strong} as {@link ReferenceType} parameter. This forces an <code>EventManager</code>
     * implementation to use strong references instead of weak ones. But be advised, <u>subscriptions with strong references
     * have to be revoked manually to avoid memory leaks.</u>
     *
     * @param context    a {@link Context}
     * @param subscriber any {@link Object}.
     * @param filters    the {@link EventFilter Filters} to use.
     */
    void subscribe(Context context, Object subscriber, EventFilter... filters);

    /**
     * Subscribes the specified {@link Object} for {@link Event Events} occurring in the specified {@link Context}.
     * The passed {@link ReferenceType} is used to hold an reference to the subscriber.
     * <p>
     * The {@link Class} of the subscriber has to have at least one method annotated as {@link Handle @Handle}.
     * <p>
     * <b>Note: </b> To avoid memory leaks due to undead subscribers, an <code>EventManager</code> implementation may
     * use {@link WeakReference} for every subscriber. Therefore, if no other object holds an reference to the subscriber
     * object, the subscriber object will be garbage collected. To prevent the object from being garbage collected this
     * operation accepts an {@link ReferenceType} and the {@link ReferenceType#Strong} forces an <code>EventManager</code>
     * implementation to use strong references instead of weak ones. But be advised, <u>subscriptions with strong references
     * have to be revoked manually to avoid memory leaks.</u>
     *
     * @param context       a {@link Context}
     * @param subscriber    any {@link Object}.
     * @param referenceType the {@link ReferenceType} to use.
     */
    void subscribe(Context context, Object subscriber, ReferenceType referenceType);

    /**
     * Subscribes the specified {@link Object} for {@link Event Events} occurring in the specified {@link Context}
     * in the same way as {@link EventManager#subscribe(Context, Object, ReferenceType)} does expect that before an
     * {@link Event} is dispatched to the specified subscriber the passed {@link EventFilter Filters} are applied.
     * <p>
     * The {@link Class} of the subscriber has to have at least one method annotated as {@link Handle @Handle}.
     * <p>
     * <b>Note: </b> To avoid memory leaks due to undead subscribers, an <code>EventManager</code> implementation may
     * use {@link WeakReference} for every subscriber. Therefore, if no other object holds an reference to the subscriber
     * object, the subscriber object will be garbage collected. To prevent the object from being garbage collected this
     * operation accepts an {@link ReferenceType} and the {@link ReferenceType#Strong} forces an <code>EventManager</code>
     * implementation to use strong references instead of weak ones. But be advised, <u>subscriptions with strong references
     * have to be revoked manually to avoid memory leaks.</u>
     *
     * @param context       a {@link Context}
     * @param subscriber    any {@link Object}.
     * @param referenceType the {@link ReferenceType} to use.
     * @param filters       the {@link EventFilter Filters} to use.
     */
    void subscribe(Context context, Object subscriber, ReferenceType referenceType, EventFilter... filters);

    /**
     * Revokes a previous made subscription of the specified subscriber in the passed {@link Context}.
     * <p>
     * <b>Note:</b> If there is no subscription corresponding to the specified subscriber in the passed {@link Context}
     * nothing happens.
     * </p>
     *
     * @param context    a {@link Context}.
     * @param subscriber the {@link Object} to unsubscribe.
     */
    void unsubscribe(Context context, Object subscriber);

    /**
     * Sends an {@link Event} of the specified {@link EventType} with the passed {@link Parameter Parameters}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread will <u>not</u> return until the event has been delivered to all interested subscribers.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameter Parameters} to set to the {@link Event}.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void send(Class<? extends RootEvent> type, Parameter... parameters) throws SanityCheckFailedException;

    /**
     * Sends an {@link Event} of the specified {@link EventType} with the passed {@link Parameters}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread will <u>not</u> return until the event has been delivered to all interested subscribers.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameters} to set to the {@link Event}.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void send(Class<? extends RootEvent> type, Parameters parameters) throws SanityCheckFailedException;

    /**
     * Sends the {@link Event} created from the information provided by the specified {@link EventBuilder}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread will <u>not</u> return until the event has been delivered to all interested subscribers.
     *
     * @param builder an {@link EventBuilder} to use.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void send(EventBuilder builder) throws SanityCheckFailedException;

    /**
     * Sends the specified {@link Event}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread will <u>not</u> return until the event has been delivered to all interested subscribers.
     *
     * @param event an {@link Event} to send.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void send(Event event) throws SanityCheckFailedException;

    /**
     * Posts an {@link Event} of the specified {@link EventType} with the passed {@link Parameter Parameters}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>asynchronous</u> manner.
     * Therefore the calling thread will return <u>before</u> the event has been delivered to all interested subscribers.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameter Parameters} to set to the {@link Event}.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void post(Class<? extends RootEvent> type, Parameter... parameters) throws SanityCheckFailedException;

    /**
     * Posts an {@link Event} of the specified {@link EventType} with the passed {@link Parameters}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>asynchronous</u> manner.
     * Therefore the calling thread will return <u>before</u> the event has been delivered to all interested subscribers.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameters} to set to the {@link Event}.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void post(Class<? extends RootEvent> type, Parameters parameters) throws SanityCheckFailedException;

    /**
     * Posts the {@link Event} created from the information provided by the specified {@link EventBuilder}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>asynchronous</u> manner.
     * Therefore the calling thread will return <u>before</u> the event has been delivered to all interested subscribers.
     *
     * @param builder an {@link EventBuilder} to use.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void post(EventBuilder builder) throws SanityCheckFailedException;

    /**
     * Posts the specified {@link Event}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>asynchronous</u> manner.
     * Therefore the calling thread will return <u>before</u> the event has been delivered to all interested subscribers.
     *
     * @param event an {@link Event} to send.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void post(Event event) throws SanityCheckFailedException;

    /**
     * Executes a {@link Query} with the specified {@link EventType} and the passed {@link Parameter Parameters}.
     *
     * @param type an {@link EventType}.
     * @param parameters an array of {@link Parameter Parameters}.
     *
     * @return the {@link ResultSet} of the {@link Query}.
     */
    ResultSet query(Class<? extends RootEvent> type, Parameter... parameters);

    /**
     * Executes a {@link Query} with the specified {@link EventType} and the passed {@link Parameters}.
     *
     * @param type an {@link EventType}.
     * @param parameters a {@link Parameters}.
     *
     * @return the {@link ResultSet} of the {@link Query}.
     */
    ResultSet query(Class<? extends RootEvent> type, Parameters parameters);

    /**
     * Executes a {@link Query} created from the information provided by the specified {@link QueryBuilder}.
     *
     * @param builder a {@link QueryBuilder}.
     *
     * @return the {@link ResultSet} of the {@link Query}.
     */
    ResultSet query(QueryBuilder builder) throws SanityCheckFailedException;

    /**
     * Executes the specified {@link Query}.
     *
     * @param query a {@link Query}.
     *
     * @return the {@link ResultSet} of the {@link Query}.
     */
    ResultSet query(Query query) throws SanityCheckFailedException;
}
