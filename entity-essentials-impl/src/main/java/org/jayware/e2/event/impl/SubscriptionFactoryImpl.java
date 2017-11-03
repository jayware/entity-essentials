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

import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventFilter;
import org.jayware.e2.event.api.Subscription;
import org.jayware.e2.event.api.SubscriptionFactory;
import org.jayware.e2.util.ObjectUtil;
import org.jayware.e2.util.ReferenceType;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jayware.e2.util.ReferenceType.STRONG;
import static org.jayware.e2.util.ReferenceType.WEAK;


public class SubscriptionFactoryImpl
implements SubscriptionFactory
{
    @Override
    public Subscription createSubscription(final Object subscriber, final ReferenceType referenceType, final EventFilter[] filters, final EventDispatcher dispatcher)
    {
        if (referenceType == STRONG)
        {
            return new StrongReferenceSubscriptionImpl(subscriber, filters, dispatcher);
        }
        else
        {
            return new WeakReferenceSubscriptionImpl(subscriber, filters, dispatcher);
        }
    }

    static class StrongReferenceSubscriptionImpl
    implements Subscription
    {
        private final Object mySubscriber;
        private final EventDispatcher myEventDispatcher;
        private final EventFilter[] myFilters;

        private final AtomicBoolean myIsValid;

        StrongReferenceSubscriptionImpl(Object subscriber, EventFilter[] filters, EventDispatcher eventDispatcher)
        {
            mySubscriber = subscriber;
            myEventDispatcher = eventDispatcher;
            myFilters = filters != null ? filters : new EventFilter[0];
            myIsValid = new AtomicBoolean(true);
        }

        @Override
        public Object getSubscriber()
        {
            return mySubscriber;
        }

        @Override
        public EventFilter[] getFilters()
        {
            return myFilters;
        }

        public EventDispatcher getEventDispatcher()
        {
            return myEventDispatcher;
        }

        @Override
        public ReferenceType getReferenceType()
        {
            return STRONG;
        }

        @Override
        public void invalidate()
        {
            myIsValid.set(false);
        }

        @Override
        public boolean isValid()
        {
            return myIsValid.get();
        }
    }

    static class WeakReferenceSubscriptionImpl
    implements Subscription
    {
        private final WeakReference<Object> mySubscriber;
        private final EventDispatcher myEventDispatcher;
        private final EventFilter[] myFilters;

        private final AtomicBoolean myIsValid;

        WeakReferenceSubscriptionImpl(Object subscriber, EventFilter[] filters, EventDispatcher eventDispatcher)
        {
            mySubscriber = new WeakReference<Object>(subscriber);
            myEventDispatcher = eventDispatcher;
            myFilters = filters != null ? filters : new EventFilter[0];
            myIsValid = new AtomicBoolean(true);
        }

        @Override
        public Object getSubscriber()
        {
            return mySubscriber.get();
        }

        @Override
        public EventFilter[] getFilters()
        {
            return myFilters;
        }

        public EventDispatcher getEventDispatcher()
        {
            return myEventDispatcher;
        }

        @Override
        public ReferenceType getReferenceType()
        {
            return WEAK;
        }

        @Override
        public void invalidate()
        {
            myIsValid.set(false);
            mySubscriber.clear();
        }

        @Override
        public boolean isValid()
        {
            return mySubscriber.get() != null && myIsValid.get();
        }

        @Override
        public boolean equals(final Object other)
        {
            if (this == other)
            {
                return true;
            }
            if (!(other instanceof Subscription))
            {
                return false;
            }

            final Subscription that = (Subscription) other;
            final Object subscriber = getSubscriber();

            return getReferenceType().equals(that.getReferenceType()) &&
                   subscriber != null ? subscriber.equals(that.getSubscriber()) : that.getSubscriber() == null;
        }

        @Override
        public int hashCode()
        {
            return ObjectUtil.hashCode(getReferenceType(), getSubscriber());
        }
    }
}
