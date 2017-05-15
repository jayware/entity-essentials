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


import org.jayware.e2.event.api.Parameters;
import org.jayware.e2.event.api.Query;
import org.jayware.e2.event.api.QueryBuilder;
import org.jayware.e2.event.api.ResultSet;
import org.jayware.e2.util.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.event.api.Query.State.Failed;
import static org.jayware.e2.event.api.Query.State.Ready;
import static org.jayware.e2.event.api.Query.State.Running;
import static org.jayware.e2.event.api.Query.State.Success;
import static org.jayware.e2.event.impl.QueryBuilderImpl.createQueryBuilder;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class QueryBuilderTest
{
    private QueryBuilder testee;

    private final TestConsumer testConsumer_Running = new TestConsumer();
    private final TestConsumer testConsumer_Success = new TestConsumer();
    private final TestConsumer testConsumer_Failed = new TestConsumer();

    @BeforeEach
    public void setUp()
    {
        testee = createQueryBuilder(TestEventTypeA.class);
    }

    @Test
    public void test()
    {
        Query query;

        testee.set("foo").to("bar");
        testee.set(param("muh", "kuh"));
        testee.setAll(new Parameters(new Parameters.Parameter[]{param("number1", 42), param("number2", 73)}));
        testee.on(Running, testConsumer_Running);
        testee.on(Success, testConsumer_Success);
        testee.on(Failed, testConsumer_Failed);

        query = testee.build();

        assertThat(query.getType()).isEqualTo(TestEventTypeA.class);
        assertThat((String) query.getParameter("foo")).isEqualTo("bar");
        assertThat((String) query.getParameter("muh")).isEqualTo("kuh");
        assertThat((Integer) query.getParameter("number1")).isEqualTo(42);
        assertThat((Integer) query.getParameter("number2")).isEqualTo(73);
        assertThat(((QueryImpl) query).getConsumers()).containsEntry(Running, testConsumer_Running);
        assertThat(((QueryImpl) query).getConsumers()).containsEntry(Success, testConsumer_Success);
        assertThat(((QueryImpl) query).getConsumers()).containsEntry(Failed, testConsumer_Failed);

        query = testee.reset().build();

        assertThat(query.getType()).isEqualTo(TestEventTypeA.class);
        assertThat(query.hasParameter("foo")).isFalse();
        assertThat(query.hasParameter("muh")).isFalse();
        assertThat(query.hasParameter("number1")).isFalse();
        assertThat(query.hasParameter("number2")).isFalse();
        assertThat(((QueryImpl) query).getConsumers()).isEmpty();
    }

    @Test
    public void test_createQueryBuilder_ThrowsIllegalArgumentExceptionIfEventTypeIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                createQueryBuilder(null);
            }
        });
    }

    @Test
    public void test_set_ParameterName_ThrowsIllegalArgumentExceptionIfNameIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.set((String) null);
            }
        });
    }

    @Test
    public void test_set_ParameterName_ThrowsIllegalArgumentExceptionIfNameIsEmpty()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.set("");
            }
        });
    }

    @Test
    public void test_set_Parameter_ThrowsIllegalArgumentExceptionIfParameterIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.set((Parameters.Parameter) null);
            }
        });
    }

    @Test
    public void test_setAll_ThrowsIllegalArgumentExceptionIfParametersIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.setAll(null);
            }
        });
    }

    @Test
    public void test_on_ThrowsIllegalArgumentExceptionIfStateIsNull()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.on(null, new TestConsumer());
            }
        });
    }

    @Test
    public void test_on_ThrowsIllegalArgumentExceptionIfStateIsReady()
    {
        assertThrows(IllegalArgumentException.class, new Executable()
        {
            @Override
            public void execute()
            {
                testee.on(Ready, new TestConsumer());
            }
        });
    }

    private static class TestConsumer
    implements Consumer<ResultSet>
    {
        @Override
        public void accept(ResultSet resultSet)
        {
            throw new UnsupportedOperationException("TestConsumer.accept");
        }
    }
}
