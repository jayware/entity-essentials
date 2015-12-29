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
    private EntityManager entityManager;

    @BeforeMethod
    public void setup()
    {
        context = ContextProvider.getInstance().createContext();
        entityManager = context.getEntityManager();
    }

    @Test
    public void testCreateEntityAbsolute()
    {
        EntityRef entityRef = entityManager.createEntity(context, path("/a/b"));

        assertThat(entityRef.isValid()).isTrue();
        assertThat(entityRef.getPath().asString()).isEqualTo("/a/b/");
    }

    @Test
    public void testCreateEntityRelative()
    {
        final EntityRef ref = entityManager.createEntity(context, path("/a"));

        EntityRef entityRef = entityManager.createEntity(ref, path("b"));

        assertThat(entityRef.isValid()).isTrue();
        assertThat(entityRef.getPath().asString()).isEqualTo("/a/b/");
    }

    @Test
    public void testFindEntity()
    {
        entityManager.createEntity(context, path("/a/b"));

        assertThat(entityManager.findEntity(context, path("/"))).isNotNull();
        assertThat(entityManager.findEntity(context, path("/a"))).isNotNull();
        assertThat(entityManager.findEntity(context, path("/a/b"))).isNotNull();
        assertThat(entityManager.findEntity(context, path("/a/b/c"))).isNull();
        assertThat(entityManager.findEntity(context, path(""))).isNull();
    }

    @Test
    public void testDeleteEntity()
    {
        EntityRef ref = entityManager.createEntity(context, path("/a/b"));

        assertThat(ref).isNotNull();
        entityManager.deleteEntity(ref);

        assertThat(entityManager.findEntity(context, path("/a/b"))).isNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDeleteRootEntity()
    {
        EntityRef ref = entityManager.findEntity(context, path("/"));
        assertThat(ref).isNotNull();

        try
        {
            entityManager.deleteEntity(ref);
        }
        finally
        {
            // Assert that when an exception is thrown by the
            // deleteEntity operation the entity does still exist.
            assertThat(entityManager.findEntity(context, path("/"))).isNotNull();
        }
    }

    @Test
    public void testGetEntity()
    {
        entityManager.createEntity(context, path("/a/b"));

        assertThat(entityManager.getEntity(context, path("/"))).isNotNull();
        assertThat(entityManager.getEntity(context, path("/a"))).isNotNull();
        assertThat(entityManager.getEntity(context, path("/a/b"))).isNotNull();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetEntity_WithEmptyPath()
    {
        entityManager.getEntity(context, path(""));
    }

    @Test(expectedExceptions = EntityNotFoundException.class)
    public void testGetEntity_WithAbsentEntity()
    {
        entityManager.getEntity(context, path("/a/b/c"));
    }

    @Test
    public void testExistsEntity()
    {
        entityManager.createEntity(context, path("/a/b"));

        assertThat(entityManager.existsEntity(context, path("/"))).isTrue();
        assertThat(entityManager.existsEntity(context, path("/a"))).isTrue();
        assertThat(entityManager.existsEntity(context, path("/a/b"))).isTrue();
        assertThat(entityManager.existsEntity(context, path("/a/b/c"))).isFalse();
    }
}
