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

import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.util.StateLatch;

import java.util.concurrent.TimeUnit;


public interface Result<T>
{
    /**
     * Returns the preceded {@link Query} which leads to this {@link Result}.
     *
     * @return a {@link Query}.
     */
    Query getQuery();

    /**
     * Causes the current thread to wait until the {@link Query} which preceded this {@link Result} enters the
     * specified {@link State} or the current thread is interrupted.
     * <p>
     * <b>Note:</b> When the current thread is interrupted while waiting, than this operation swallows the
     * {@link InterruptedException} and returns <code>false</code> instead. (s. {@link StateLatch#await(Enum)})
     *
     * @param state a {@link State}.
     *
     * @return <code>true</code> if the {@link Query} entered the specified {@link State}, otherwise <code>false</code>.
     */
    boolean await(State state);

    /**
     * Causes the current thread to wait until the {@link Query} which preceded this {@link Result} enters the
     * specified {@link State}, the current thread is interrupted, or the specified waiting time elapses.
     * <p>
     * <b>Note:</b> When the current thread is interrupted while waiting, than this operation swallows the
     * {@link InterruptedException} and returns <code>false</code>. (s. {@link StateLatch#await(Enum, long, TimeUnit)})
     *
     * @param state a {@link State} to await.
     * @param time the maximum time to wait.
     * @param unit the {@link TimeUnit} of the time argument
     *
     * @return <code>true</code> if the {@link Query} entered the specified {@link State}, otherwise <code>false</code>.
     */
    boolean await(State state, long time, TimeUnit unit);

    /**
     * Returns whether the {@link Query} which preceded this {@link Result} is currently in the specified {@link State}.
     *
     * @param state a {@link State}.
     *
     * @return <code>true</code> if the {@link Query} which preceded this {@link Result} is currently in the specified
     *         {@link State}, otherwise <code>false</code>
     */
    boolean hasStatus(State state);

    /**
     * Returns whether a result is present.
     *
     * @return <code>true</code> if the preceded {@link Query} entered the state {@link State#Success} and produced
     *         the expected result, otherwise <code>false</code>.
     */
    boolean hasResult();

    /**
     * Returns the value of this {@link Result}.
     * <p>
     * In contrast to {@link Result#find()} this operation throws a {@link MissingResultException} if no value is present.
     * <p>
     * <b>Note:</b> This operation blocks until the {@link Query} which preceded this {@link Result} enters the state {@link State#Success}.
     *
     * @return the value, never <code>null</code>.
     *
     * @throws MissingResultException if the {@link Query} which preceded this {@link ResultSet} entered the state
     *                                {@link State#Success} but did not produce the expected value.
     */
    T get();

    /**
     * Returns the value of this {@link Result}.
     * <p>
     * In contrast to {@link Result#get()} this operation returns <code>null</code> if no value is present.
     * <p>
     * <b>Note:</b> This operation blocks until the {@link Query} which preceded this {@link Result} enters the state {@link State#Success}.
     *
     * @return the value or <code>null</code>.
     *
     * @throws MissingResultException if the {@link Query} which preceded this {@link ResultSet} entered the state
     *                                {@link State#Success} but did not produce the expected value.
     */
    T find();
}
