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
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ServiceProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.template.api.TemplateManager;
import org.jayware.e2.util.Key;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.jayware.e2.context.api.Context.ValueProvider;
import static org.jayware.e2.util.Key.createKey;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class ContextImplTest
{
    private ContextImpl testee;

    private @Mock ServiceProvider serviceProvider;

    private @Mock EntityManager entityManager;
    private @Mock ComponentManager componentManager;
    private @Mock TemplateManager templateManager;
    private @Mock EventManager eventManager;
    private @Mock GroupManager myGroupManager;

    final Key<Object> keyA = createKey("foo");
    final Key<Object> keyB = createKey("bar");

    @BeforeMethod
    public void setup()
    {
        initMocks(this);

        when(serviceProvider.getService(EntityManager.class)).thenReturn(entityManager);
        when(serviceProvider.findService(EntityManager.class)).thenReturn(entityManager);
        when(serviceProvider.getService(ComponentManager.class)).thenReturn(componentManager);
        when(serviceProvider.findService(ComponentManager.class)).thenReturn(componentManager);
        when(serviceProvider.getService(TemplateManager.class)).thenReturn(templateManager);
        when(serviceProvider.findService(TemplateManager.class)).thenReturn(templateManager);
        when(serviceProvider.getService(EventManager.class)).thenReturn(eventManager);
        when(serviceProvider.findService(EventManager.class)).thenReturn(eventManager);
        when(serviceProvider.getService(GroupManager.class)).thenReturn(myGroupManager);
        when(serviceProvider.findService(GroupManager.class)).thenReturn(myGroupManager);

        testee = new ContextImpl(serviceProvider);
    }

    @Test
    public void testPut()
    {
        assertThat(testee.contains(keyA)).isFalse();
        testee.put(keyA, "test-value");
        assertThat(testee.contains(keyA)).isTrue();
    }

    @Test
    void testPutWithNullKey()
    {
        try
        {
            testee.put((Key) null, "");
            fail("IllegalArgumentException expected!");
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testPutWhenDisposed()
    {
        testee.dispose();

        try
        {
            testee.put(keyA, "test-value");
            fail("IllegalStateException expected!");
        }
        catch (IllegalStateException e)
        {

        }
    }

    @Test
    public void testPutIfAbsent()
    {
        assertThat(testee.contains(keyA)).isFalse();
        assertThat(testee.putIfAbsent(keyA, "test-value-a")).isTrue();
        assertThat(testee.putIfAbsent(keyA, "test-value-b")).isFalse();
        assertThat(testee.contains(keyA)).isTrue();

        testee.put(keyB, "an-other-test-value");
        assertThat(testee.contains(keyB)).isTrue();
        assertThat(testee.putIfAbsent(keyB, "")).isFalse();
    }

    @Test
    void testPutIfAbsentWithNullKey()
    {
        try
        {
            testee.putIfAbsent(null, "");
            fail("IllegalArgumentException expected!");
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testPutIfAbsentWhenDisposed()
    {
        testee.dispose();

        try
        {
            testee.putIfAbsent(keyA, "test-value");
            fail("IllegalStateException expected!");
        }
        catch (IllegalStateException e)
        {

        }
    }

    @Test
    public void testPutIfAbsentValueProvider()
    {
        Key<String> key = createKey("fubar");
        ValueProvider<String> valueProvider = mock(ValueProvider.class);
        when(valueProvider.provide((Context) any())).thenReturn("test-value");

        assertThat(testee.contains(key)).isFalse();
        assertThat(testee.putIfAbsent(key, valueProvider)).isTrue();
        assertThat(testee.putIfAbsent(key, "")).isFalse();
        assertThat(testee.get(key)).isEqualTo("test-value");

        verify(valueProvider, times(1)).provide((Context) any());
    }

    @Test
    void testPutIfAbsentValueProviderWithNullKey()
    {
        try
        {
            testee.putIfAbsent((Key<String>) null, mock(ValueProvider.class));
            fail("IllegalArgumentException expected!");
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    void testPutIfAbsentValueProviderWithNullProvider()
    {
        final Key<String> key = createKey("fubar");

        try
        {
            testee.putIfAbsent(key, (ValueProvider<String>) null);
            fail("IllegalArgumentException expected!");
        }
        catch (IllegalArgumentException e)
        {

        }
    }


    @Test
    public void testPutIfValueProviderAbsentWhenDisposed()
    {
        testee.dispose();

        try
        {
            testee.putIfAbsent((Key<String>) null, mock(ValueProvider.class));
            fail("IllegalStateException expected!");
        }
        catch (IllegalStateException e)
        {

        }
    }

    @Test
    public void testRemove()
    {
        assertThat(testee.contains(keyA)).isFalse();
        testee.put(keyA, "test-value");
        assertThat(testee.contains(keyA)).isTrue();
        testee.remove(keyA);
        assertThat(testee.contains(keyA)).isFalse();
    }

    @Test
    public void testRemoveUnknownKey()
    {
        assertThat(testee.contains(keyB)).isFalse();
        testee.remove(keyB);
        assertThat(testee.contains(keyB)).isFalse();
    }

    @Test
    public void testRemoveWithNullKey()
    {
        try
        {
            testee.remove(null);
            fail("IllegalArgumentException expected!");
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testGet()
    {
        testee.put(keyA, "test-value");
        assertThat(testee.get(keyA)).isEqualTo("test-value");
    }

    @Test
    public void testGetWithNullKey()
    {
        try
        {
            testee.get(null);
            fail("IllegalArgumentException expected!");
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testGetWhenDisposed()
    {
        testee.dispose();

        try
        {
            testee.get(keyA);
            fail("IllegalStateException expected!");
        }
        catch (IllegalStateException e)
        {

        }
    }

    @Test
    public void testGetOrDefault()
    {
        testee.put(keyA, "test-value");
        assertThat(testee.get(keyA, "fubar")).isEqualTo("test-value");
        assertThat(testee.get(keyB, "fubar")).isEqualTo("fubar");
    }

    @Test
    public void testGetOrDefaultWithNullKey()
    {
        try
        {
            testee.get(null, "");
            fail("IllegalArgumentException expected!");
        }
        catch (IllegalArgumentException e)
        {

        }
    }

    @Test
    public void testGetOrDefaultWhenDisposed()
    {
        testee.dispose();

        try
        {
            testee.get(keyA, "");
            fail("IllegalStateException expected!");
        }
        catch (IllegalStateException e)
        {

        }
    }

    @Test
    public void testContains()
    {
        assertThat(testee.contains(keyA)).isFalse();
        assertThat(testee.contains(keyB)).isFalse();
        assertThat(testee.contains(null)).isFalse();

        testee.put(keyA, "test-value");

        assertThat(testee.contains(keyA)).isTrue();
        assertThat(testee.contains(keyB)).isFalse();
        assertThat(testee.contains(null)).isFalse();
    }

    @Test
    public void testContainsWhenDisposed()
    {
        testee.dispose();

        try
        {
            testee.contains(keyA);
            fail("IllegalStateException expected!");
        }
        catch (IllegalStateException e)
        {

        }

        try
        {
            testee.contains(null);
            fail("IllegalStateException expected!");
        }
        catch (IllegalStateException e)
        {

        }
    }
}
