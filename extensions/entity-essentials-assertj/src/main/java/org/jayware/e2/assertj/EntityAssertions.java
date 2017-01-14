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
package org.jayware.e2.assertj;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;


public class EntityAssertions
extends AbstractAssert<EntityAssertions, EntityRef>
{
    private final Context myContext;
    private final ComponentManager myComponentManager;

    public EntityAssertions(EntityRef actual)
    {
        super(actual, EntityAssertions.class);

        myContext = actual.getContext();
        myComponentManager = myContext.getService(ComponentManager.class);
    }

    public static EntityAssertions assertThat(EntityRef ref)
    {
        return new EntityAssertions(ref);
    }

    public EntityAssertions isValid()
    {
        isNotNull();

        if (actual.isInvalid())
        {
            failWithMessage("Expected EntityRef { %s } to be valid", actual.getId());
        }

        return this;
    }

    public EntityAssertions isInvalid()
    {
        isNotNull();

        if (actual.isValid())
        {
            failWithMessage("Expected EntityRef { %s } to be invalid", actual.getId());
        }

        return this;
    }

    public EntityAssertions matches(Aspect aspect)
    {
        isNotNull();

        if (!aspect.matches(actual))
        {
            failWithMessage("Expected Entity { %s } to match the Aspect: %s", actual.getId(), aspect);
        }

        return this;
    }

    public EntityAssertions hasAtLeast(Class<? extends Component>... expectedComponents)
    {
        isNotNull();

        final Collection<Class<? extends Component>> actualComponents = myComponentManager.getComponentTypes(actual);

        Assertions.assertThat(myComponentManager.getComponentTypes(actual))
            .withFailMessage("Expected that Entity { %s } has the components:\n\n%s\nbut has:\n\n%s", actual.getId(), stringify(expectedComponents), stringify(actualComponents))
            .contains(expectedComponents);

        return this;
    }

    public EntityAssertions hasExactly(Class<? extends Component>... expectedComponents)
    {
        isNotNull();

        final Collection<Class<? extends Component>> actualComponents = myComponentManager.getComponentTypes(actual);

        Assertions.assertThat(actualComponents)
            .withFailMessage("Expected that Entity { %s } has exactly the components:\n\n%s\nbut has:\n\n%s", actual.getId(), stringify(expectedComponents), stringify(actualComponents))
            .containsExactlyInAnyOrder(expectedComponents);

        return this;
    }

    public EntityAssertions doesNotHave(Class<? extends Component>... expectedComponents)
    {
        isNotNull();

        final Collection<Class<? extends Component>> actualComponents = myComponentManager.getComponentTypes(actual);

        Assertions.assertThat(actualComponents)
            .withFailMessage("Expected that Entity { %s } does not have the components:\n\n%s\nbut has:\n\n%s", actual.getId(), stringify(expectedComponents), stringify(actualComponents))
            .doesNotContain(expectedComponents);

        return this;
    }

    public EntityAssertions hasId(String expectedId)
    {
        isNotNull();

        if (!actual.getId().toString().equals(expectedId))
        {
            failWithMessage("Expected EntityRef { %s } has id: %s", actual.getId(), expectedId);
        }

        return this;
    }

    public EntityAssertions hasId(UUID expectedId)
    {
        isNotNull();

        hasId(expectedId.toString());

        return this;
    }

    private static String stringify(Class<? extends Component>... components)
    {
        return stringify(Arrays.asList(components));
    }

    private static String stringify(Collection<Class<? extends Component>> components)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (Class<? extends Component> component : components)
        {
            stringBuilder.append("\t - ").append(component.getName()).append("\n");
        }

        return stringBuilder.toString();
    }
}
