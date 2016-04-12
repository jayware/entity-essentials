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
