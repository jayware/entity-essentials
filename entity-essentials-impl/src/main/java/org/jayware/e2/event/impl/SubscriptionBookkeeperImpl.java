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
package org.jayware.e2.event.impl;

import org.jayware.e2.event.api.Subscription;
import org.jayware.e2.event.api.SubscriptionBookkeeper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;


public class SubscriptionBookkeeperImpl
implements SubscriptionBookkeeper
{
    private final Map<Object, Subscription> mySubscriptions;
    private final AtomicReference<Iterable<Subscription>> myLastSubscriptions;

    private final Object lock = new Object();

    public SubscriptionBookkeeperImpl()
    {
        mySubscriptions = new WeakHashMap<Object, Subscription>();
        myLastSubscriptions = new AtomicReference<Iterable<Subscription>>(Collections.<Subscription>emptySet());
    }

    @Override
    public void subscribe(final Subscription subscription)
    {
        final Object subscriber = subscription.getSubscriber();

        synchronized (lock)
        {
            if (mySubscriptions.containsKey(subscriber))
            {
                return;
            }

            mySubscriptions.put(subscriber, subscription);
            synchronize();
        }
    }

    @Override
    public void unsubscribe(final Object subscriber)
    {
        synchronized (lock)
        {
            if (!mySubscriptions.containsKey(subscriber))
            {
                return;
            }

            mySubscriptions.remove(subscriber);
            synchronize();
        }
    }

    @Override
    public Iterable<Subscription> subscriptions()
    {
        return myLastSubscriptions.get();
    }

    @Override
    public boolean isSubscribed(final Object subscriber)
    {
        synchronized (lock)
        {
            return mySubscriptions.containsKey(subscriber);
        }
    }

    public void clear()
    {
        synchronized (lock)
        {
            mySubscriptions.clear();
            myLastSubscriptions.set(Collections.<Subscription>emptySet());
        }
    }

    private void synchronize()
    {
        final Set<Subscription> subscriptions = new HashSet<Subscription>();

        for (Subscription subscription : mySubscriptions.values())
        {
            subscriptions.add(subscription);
        }

        myLastSubscriptions.set(subscriptions);
    }
}
