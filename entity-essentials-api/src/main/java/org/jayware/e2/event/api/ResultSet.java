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
import org.jayware.e2.util.Key;
import org.jayware.e2.util.StateLatch;
import org.jayware.e2.util.TimeoutException;

import java.util.concurrent.TimeUnit;


/**
 * A <code>ResultSet</code> is the outcome of the execution of a {@link Query}.
 *
 * @see Query
 */
public interface ResultSet
{
    /**
     * Returns the preceded {@link Query} which leads this {@link ResultSet}.
     *
     * @return a {@link Query}.
     */
    Query getQuery();

    /**
     * Causes the current thread to wait until the {@link Query} which preceded this {@link ResultSet} enters the
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
     * Causes the current thread to wait until the {@link Query} which preceded this {@link ResultSet} enters the
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

    void timeout(State state, long timeInMilliseconds) throws TimeoutException;

    void timeout(State state, long time, TimeUnit unit) throws TimeoutException;

    void timeout(State state, long timeInMilliseconds, String message, Object... args) throws TimeoutException;

    void timeout(State state, long time, TimeUnit unit, String message, Object... args) throws TimeoutException;

    /**
     * Returns whether the {@link Query} which preceded this {@link ResultSet} is currently in the specified {@link State}.
     *
     * @param state a {@link State}.
     *
     * @return <code>true</code> if the {@link Query} which preceded this {@link ResultSet} is currently in the specified
     *         {@link State}, otherwise <code>false</code>
     */
    boolean hasStatus(State state);

    /**
     * Returns whether results of the preceded {@link Query} might be present.
     * <p>
     * It is assumed, that results are present when the {@link Query} enters the state {@link State#Success}.
     * Therefore this operation is a shorthand for {@link #hasStatus hasStatus(Success)}
     *
     * @return <code>true</code> if the preceded {@link Query} entered the state {@link State#Success}, otherwise
     *         <code>false</code>.
     */
    boolean hasResult();

    /**
     * Returns the value of this {@link ResultSet} which is associated to the specified key.
     * <p>
     * In contrast to {@link ResultSet#find(String)} this operation throws a {@link MissingResultException} if no value is
     * associated to the specified key.
     * <p>
     * <b>Note:</b> This operation blocks until the {@link Query} which preceded this {@link ResultSet} enters
     * the state {@link State#Success}.
     *
     * @param key a {@link String}
     * @param <V> the type of the value.
     *
     * @return the value which is associated to the specified key, never <code>null</code>.
     *
     * @throws MissingResultException if the {@link Query} which preceded this {@link ResultSet} entered the state
     *                                {@link State#Success} but did not produce the expected value.
     */
    <V> V get(String key);

    /**
     * Returns the value of this {@link ResultSet} which is associated to the specified {@link Key}.
     * <p>
     * In contrast to {@link ResultSet#find(Key)} this operation throws a {@link MissingResultException} if no value is
     * associated to the specified {@link Key}.
     * <p>
     * <b>Note:</b> This operation blocks until the {@link Query} which preceded this {@link ResultSet} enters
     * the state {@link State#Success}.
     *
     * @param key a {@link Key}
     * @param <V> the type of the value.
     *
     * @return the value which is associated to the specified {@link Key}, never <code>null</code>.
     *
     * @throws MissingResultException if the {@link Query} which preceded this {@link ResultSet} entered the state
     *                                {@link State#Success} but did not produce the expected value.
     */
    <V> V get(Key<V> key);

    /**
     * Returns the value of this {@link ResultSet} which is associated to the specified key or <code>null</code>.
     * <p>
     * In contrast to {@link ResultSet#get(String)} this operation returns <code>null</code> if no value is associated to
     * the specified key.
     * <p>
     * <b>Note:</b> This operation blocks until the {@link Query} which preceded this {@link ResultSet} enters
     * the state {@link State#Success}.
     *
     * @param key a {@link String}
     * @param <V> the type of the value.
     *
     * @return the value which is associated to the specified key or <code>null</code>.
     */
    <V> V find(String key);

    /**
     * Returns the value of this {@link ResultSet} which is associated to the specified {@link Key} or <code>null</code>.
     * <p>
     * In contrast to {@link ResultSet#get(Key)} this operation returns <code>null</code> if no value is associated to
     * the specified key.
     * <p>
     * <b>Note:</b> This operation blocks until the {@link Query} which preceded this {@link ResultSet} enters
     * the state {@link State#Success}.
     *
     * @param key a {@link Key}
     * @param <V> the type of the value.
     *
     * @return the value which is associated to the specified {@link Key} or <code>null</code>.
     */
    <V> V find(Key<V> key);

    /**
     * Returns whether this {@link ResultSet} contains a value associated to the specified name.
     *
     * @param name a {@link String}
     *
     * @return <code>true</code> if a value is associated to specified name, otherwise <code>false</code>.
     */
    boolean has(String name);

    /**
     * Returns whether this {@link ResultSet} contains a value associated to the specified {@link Key}.
     *
     * @param key a {@link Key}
     *
     * @return <code>true</code> if a value is associated to specified {@link Key}, otherwise <code>false</code>.
     */
    boolean has(Key<?> key);

    <T> Result<T> resultOf(Key<T> key);

    <T> Result<T> resultOf(String name);
}
