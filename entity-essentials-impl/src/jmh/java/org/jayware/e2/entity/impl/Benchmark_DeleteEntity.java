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
        myQueue = new LinkedList<EntityRef>();

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
