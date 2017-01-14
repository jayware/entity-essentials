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
package org.jayware.e2.component.impl;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;


@Fork(3)
@State(Scope.Benchmark)
@Measurement(iterations = 10)
@Warmup(iterations = 1, batchSize = 1000)
public class Benchmark_ComponentPushPull
{
    private Context myContext;
    private EntityManager myEntityManager;
    private ComponentManager myComponentManager;

    private EntityRef myRef;
    private BenchmarkComponent myComponent;

    @Setup
    public void setup()
    {
        myContext = ContextProvider.getInstance().createContext();
        myEntityManager = myContext.getService(EntityManager.class);
        myComponentManager = myContext.getService(ComponentManager.class);

        myRef = myEntityManager.createEntity(myContext);
        myComponent = myComponentManager.addComponent(myRef, BenchmarkComponent.class);
    }

    @Benchmark
    public void pushTo()
    {
        myComponent.pushTo(myRef);
    }

    @Benchmark
    public void pullFrom()
    {
        myComponent.pullFrom(myRef);
    }

    public interface BenchmarkComponent
    extends Component
    {
        int getValue();

        void setValue(int value);
    }
}
