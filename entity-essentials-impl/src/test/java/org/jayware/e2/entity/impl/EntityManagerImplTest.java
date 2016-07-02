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
package org.jayware.e2.entity.impl;


import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityNotFoundException;
import org.jayware.e2.entity.api.EntityRef;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityPath.path;


public class EntityManagerImplTest
{
    private Context context;
    private EntityManager testee;

    private @Mocked Context mockedContext;

    @BeforeMethod
    public void setup()
    {
        context = ContextProvider.getInstance().createContext();
        testee = new EntityManagerImpl();
    }

    @Test
    public void testCreateEntity()
    {
        EntityRef ref = testee.createEntity(context);
        assertThat(ref.isValid()).isTrue();
    }

    @Test
    public void testCreateEntityAbsolute()
    {
        EntityRef entityRef = testee.createEntity(context, path("/a/b"));

        assertThat(entityRef.isValid()).isTrue();
        assertThat(entityRef.getPath().asString()).isEqualTo("/a/b/");
    }

    @Test
    public void testCreateEntityRelative()
    {
        final EntityRef ref = testee.createEntity(context, path("/a"));

        EntityRef entityRef = testee.createEntity(ref, path("b"));

        assertThat(entityRef.isValid()).isTrue();
        assertThat(entityRef.getPath().asString()).isEqualTo("/a/b/");
    }

    @Test
    public void testFindEntity()
    {
        testee.createEntity(context, path("/a/b"));

        assertThat(testee.findEntity(context, path("/"))).isNotNull();
        assertThat(testee.findEntity(context, path("/a"))).isNotNull();
        assertThat(testee.findEntity(context, path("/a/b"))).isNotNull();
        assertThat(testee.findEntity(context, path("/a/b/c"))).isNull();
        assertThat(testee.findEntity(context, path(""))).isNull();
    }

    @Test
    public void testDeleteEntity()
    {
        EntityRef ref = testee.createEntity(context, path("/a/b"));

        assertThat(ref).isNotNull();
        testee.deleteEntity(ref);

        assertThat(testee.findEntity(context, path("/a/b"))).isNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteRootEntity()
    {
        EntityRef ref = testee.findEntity(context, path("/"));
        assertThat(ref).isNotNull();

        try
        {
            testee.deleteEntity(ref);
        }
        finally
        {
            // Assert that when an exception is thrown by the
            // deleteEntity operation the entity does still exist.
            assertThat(testee.findEntity(context, path("/"))).isNotNull();
        }
    }

    @Test
    public void testGetEntity()
    {
        testee.createEntity(context, path("/a/b"));

        assertThat(testee.getEntity(context, path("/"))).isNotNull();
        assertThat(testee.getEntity(context, path("/a"))).isNotNull();
        assertThat(testee.getEntity(context, path("/a/b"))).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetEntity_WithEmptyPath()
    {
        testee.getEntity(context, path(""));
    }

    @Test(expectedExceptions = EntityNotFoundException.class)
    public void testGetEntity_WithAbsentEntity()
    {
        testee.getEntity(context, path("/a/b/c"));
    }

    @Test
    public void testExistsEntity()
    {
        testee.createEntity(context, path("/a/b"));

        assertThat(testee.existsEntity(context, path("/"))).isTrue();
        assertThat(testee.existsEntity(context, path("/a"))).isTrue();
        assertThat(testee.existsEntity(context, path("/a/b"))).isTrue();
        assertThat(testee.existsEntity(context, path("/a/b/c"))).isFalse();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_asContextual_Throws_IllegalArgumentException_if_null_is_passed()
    throws Exception
    {
        testee.asContextual(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void test_asContextual_Throws_IllegalStateException_if_the_passed_Context_is_disposed()
    throws Exception
    {
        new Expectations() {{
            mockedContext.isDisposed(); result = true;
        }};

        testee.asContextual(mockedContext);
    }
}
