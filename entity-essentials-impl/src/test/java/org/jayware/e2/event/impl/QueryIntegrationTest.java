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
package org.jayware.e2.event.impl;


import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.EventType.RootEvent;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Query;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.event.api.EventType.RootEvent.ContextParam;
import static org.jayware.e2.event.api.Parameters.param;
import static org.jayware.e2.util.ReferenceType.Strong;


public class QueryIntegrationTest
{
    public static final String TEST_PARAM = "fubar";
    public static final Integer TEST_VALUE = 42;

    private EventManager testee;

    private Context testContext;

    @BeforeMethod
    public void setUp()
    {
        testContext = ContextProvider.getInstance().createContext();
        testee = testContext.getService(EventManager.class);
        testee.subscribe(testContext, new TestHandler(), Strong);
    }

    @AfterMethod
    public void tearDown()
    {
        testContext.dispose();
    }

    @Test
    public void test()
    throws InterruptedException
    {
        final Query testQuery = testee.createQuery(TestQueryEvent.class,
                                                   param(ContextParam, testContext),
                                                   param(TEST_PARAM, TEST_VALUE));

        assertThat((Integer) testee.query(testQuery).get(TEST_PARAM)).isEqualTo(TEST_VALUE * TEST_VALUE);
    }

    public interface TestQueryEvent
    extends RootEvent
    {

    }

    public class TestHandler
    {
        @Handle(TestQueryEvent.class)
        public void handle(Query query, @Param(TEST_PARAM) Integer input)
        {
            query.result(TEST_PARAM, input * input);
        }
    }
}
