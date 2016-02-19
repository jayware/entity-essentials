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
package org.jayware.e2.context.impl;

import org.jayware.e2.assembly.api.GroupManager;
import org.jayware.e2.binding.api.BindingManager;
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
        testee = new DefaultServiceProviderImpl();
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
    public void test_getService_CanLoadBindingManager()
    {
        assertThat(testee.getService(BindingManager.class)).isNotNull();
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
    public void test_findService_CanLoadBindingManager()
    {
        assertThat(testee.findService(BindingManager.class)).isNotNull();
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