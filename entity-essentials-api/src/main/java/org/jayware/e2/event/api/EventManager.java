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
 * @see Handle
 *
 * @since 1.0
 */
public interface EventManager
{
    /**
     * Creates an {@link Event} with the specified {@link EventType}.
     *
     * @param type the event's {@link EventType}.
     *
     * @return an {@link EventBuilder} to set additional {@link Parameters}.
     */
    EventBuilder createEvent(Class<? extends RootEvent> type);

    /**
     * Creates an {@link Event} with the specified {@link EventType} and the passed {@link Parameter Parameters}.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameter Parameters} to set to the {@link Event}
     *
     * @return the newly created {@link Event}.
     */
    Event createEvent(Class<? extends RootEvent> type, Parameter... parameters);

    /**
     * Creates an {@link Event} with the specified {@link EventType} and the passed {@link Parameters}.
     *
     * @param type the event's {@link EventType}.
     * @param parameters the {@link Parameters} to set to the {@link Event}
     *
     * @return the newly created {@link Event}.
     */
    Event createEvent(Class<? extends RootEvent> type, Parameters parameters);

    Query createQuery(Class<? extends RootEvent> type, Parameter... parameters);

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
     * <b>Node:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread returns not until the event has been delivered to all interested subscribers.
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
     * <b>Node:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread returns not until the event has been delivered to all interested subscribers.
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
     * <b>Node:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread returns not until the event has been delivered to all interested subscribers.
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
     * Therefore the calling thread returns not until the event has been delivered to all interested subscribers.
     *
     * @param event an {@link Event} to send.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void send(Event event) throws SanityCheckFailedException;

    /**
     * Posts an {@link Event} of the specified {@link EventType} with the passed {@link Parameter Parameters}.
     * <p>
     * <b>Node:</b> The {@link Event} is delivered to all interested subscribers in a <u>asynchronous</u> manner.
     * Therefore the calling thread will return before the event has been delivered to all interested subscribers.
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
     * <b>Node:</b> The {@link Event} is delivered to all interested subscribers in a <u>asynchronous</u> manner.
     * Therefore the calling thread will return before the event has been delivered to all interested subscribers.
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
     * <b>Node:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread returns not until the event has been delivered to all interested subscribers.
     *
     * @param builder an {@link EventBuilder} to use.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void post(EventBuilder builder) throws SanityCheckFailedException;

    /**
     * Posts the specified {@link Event}.
     * <p>
     * <b>Note:</b> The {@link Event} is delivered to all interested subscribers in a <u>synchronous</u> manner.
     * Therefore the calling thread returns not until the event has been delivered to all interested subscribers.
     *
     * @param event an {@link Event} to send.
     *
     * @throws SanityCheckFailedException if the event fails any sanity check.
     */
    void post(Event event) throws SanityCheckFailedException;

    /**
     *
     * @param query
     * @return
     */
    Result query(Query query) throws SanityCheckFailedException;
}
