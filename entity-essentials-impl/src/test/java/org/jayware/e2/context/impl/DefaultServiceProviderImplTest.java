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
package org.jayware.e2.context.impl;

import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.ServiceProvider;
import org.jayware.e2.context.api.ServiceUnavailableException;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.template.api.TemplateManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class DefaultServiceProviderImplTest
{
    private ServiceProvider testee;

    @BeforeMethod
    public void setUp()
    {
        testee = new DefaultServiceProviderImpl(getClass().getClassLoader());
    }

    @Test(expectedExceptions = ServiceUnavailableException.class)
    public void test_getService_ThrowsServiceUnavailableExceptionIfASuitableServiceCouldNotBeFound()
    {
        testee.getService(UnavailableTestService.class);
    }

    @Test
    public void test_getService_CanLoadEntityManager()
    {
        assertThat(testee.getService(EntityManager.class)).isNotNull();
    }

    @Test
    public void test_getService_CanLoadComponentManager()
    {
        assertThat(testee.getService(ComponentManager.class)).isNotNull();
    }

    @Test
    public void test_getService_CanLoadEventManager()
    {
        assertThat(testee.getService(EventManager.class)).isNotNull();
    }

    @Test
    public void test_getService_CanLoadTemplateManager()
    {
        assertThat(testee.getService(TemplateManager.class)).isNotNull();
    }

    @Test
    public void test_getService_CanLoadGroupManager()
    {
        assertThat(testee.getService(GroupManager.class)).isNotNull();
    }

    @Test
    public void test_findService_ReturnsNullIfASuitableServiceCouldNotBeFound()
    {
        assertThat(testee.findService(UnavailableTestService.class)).isNull();
    }

    @Test
    public void test_findService_CanLoadEntityManager()
    {
        assertThat(testee.findService(EntityManager.class)).isNotNull();
    }

    @Test
    public void test_findService_CanLoadComponentManager()
    {
        assertThat(testee.findService(ComponentManager.class)).isNotNull();
    }

    @Test
    public void test_findService_CanLoadEventManager()
    {
        assertThat(testee.findService(EventManager.class)).isNotNull();
    }

    @Test
    public void test_findService_CanLoadTemplateManager()
    {
        assertThat(testee.findService(TemplateManager.class)).isNotNull();
    }

    @Test
    public void test_findService_CanLoadGroupManager()
    {
        assertThat(testee.findService(GroupManager.class)).isNotNull();
    }
}