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
package org.jayware.e2.event.api;

import org.jayware.e2.event.api.Query.State;
import org.jayware.e2.util.Key;
import org.jayware.e2.util.StateLatch;

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
