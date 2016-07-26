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
package org.jayware.e2.component.impl;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentEvent.ComponentAddedEvent;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.Param;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.component.api.ComponentEvent.ComponentParam;
import static org.jayware.e2.entity.api.EntityEvent.EntityRefParam;


public class ComponentIntegrationTest
{
    private static final String A_TEXT = "Pikachu";

    private Context context;
    private EventManager eventManager;
    private EntityManager entityManager;
    private ComponentManager componentManager;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    @BeforeMethod
    public void setUp()
    {
        context = ContextProvider.getInstance().createContext();
        eventManager = context.getService(EventManager.class);
        entityManager = context.getService(EntityManager.class);
        componentManager = context.getService(ComponentManager.class);
    }

    @AfterMethod
    public void tearDown()
    {
        context.dispose();
    }

    @Test
    public void test_()
    throws InterruptedException
    {
        final EntityRef ref = entityManager.createEntity(context);
        final TestHandler handler = new TestHandler(ref);

        eventManager.subscribe(context, handler);

        componentManager.prepareComponent(context, TestComponent.class);
        final TestComponent component = componentManager.createComponent(context, TestComponent.class);
        component.setText(A_TEXT);

        componentManager.addComponent(ref, component);

        lock.lock();
        try
        {
            condition.await(10, SECONDS);
            assertThat(handler.text).isEqualTo(A_TEXT);
        }
        finally
        {
            lock.unlock();
        }
    }

    public class TestHandler
    {
        private final EntityRef myRef;
        private String text;

        public TestHandler(EntityRef ref)
        {
            myRef = ref;
        }

        @Handle(ComponentAddedEvent.class)
        public void handleComponentPushedEvent(@Param(EntityRefParam) EntityRef ref, @Param(ComponentParam) Component component) throws InterruptedException
        {
            if (myRef.equals(ref) && component.type().equals(TestComponent.class))
            {
                TestComponent testComponent = (TestComponent) component;

                Thread.sleep(100);

                lock.lock();
                try
                {
                    text = testComponent.getText();
                    condition.signalAll();
                }
                finally
                {
                    lock.unlock();
                }
            }
        }
    }

    public interface TestComponent
    extends Component
    {
        String getText();
        void setText(String text);
    }
}
