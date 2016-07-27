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
package org.jayware.e2.entity.impl;

import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.LinkedList;
import java.util.Queue;

import static org.openjdk.jmh.annotations.Scope.Benchmark;


@Fork(3)
@State(Benchmark)
@Measurement(iterations = 10)
@Warmup(iterations = 1, batchSize = 1000)
public class Benchmark_DeleteEntity
{
    private Context myContext;
    private EntityManager myEntityManager;
    private Queue<EntityRef> myQueue;

    @Setup()
    public void setup()
    {
        myContext = ContextProvider.getInstance().createContext();
        myEntityManager = myContext.getService(EntityManager.class);
        myQueue = new LinkedList<>();

        for(int i = 0; i < 200000; ++i)
        {
            myQueue.add(myEntityManager.createEntity(myContext));
        }
    }

    @TearDown
    public void teardown()
    {
        myEntityManager.deleteEntities(myContext);
    }

    @Benchmark
    public void benchmark()
    {
        myEntityManager.deleteEntity(myQueue.poll());
    }
}
