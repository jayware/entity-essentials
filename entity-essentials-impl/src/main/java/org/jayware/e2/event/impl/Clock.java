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
package org.jayware.e2.event.impl;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class Clock
{
    private final AtomicInteger myThreadCount;
    private final ThreadLocal<LocalClock> myInternalClock;

    public Clock()
    {
        myThreadCount = new AtomicInteger();
        myInternalClock = ThreadLocal.withInitial(LocalClock::new);
    }

    public void update(Clock clock)
    {

    }

    public void increment()
    {

    }

    public Tick getTick()
    {
        return new Tick(myInternalClock.get().time);
    }

    private static class LocalClock
    {
        private final Map<Integer, Integer> time = new HashMap<>();
    }

    private static class Tick
    {
        private int[][] myTime;

        private Tick(Map<Integer, Integer> time)
        {

        }
    }
}
