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
package org.jayware.e2.examples;


import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;

import java.io.IOException;
import java.util.List;

import static org.jayware.e2.component.api.Aspect.aspect;


public class AspectExample
{
    public static void main(String[] args)
    throws IOException
    {
        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EntityManager entityManager = context.getService(EntityManager.class);
        ComponentManager componentManager = context.getService(ComponentManager.class);

        /* A couple of entities */
        EntityRef clark = entityManager.createEntity(context);
        EntityRef steve = entityManager.createEntity(context);
        EntityRef tony = entityManager.createEntity(context);

        /* All three are super heroes */
        HeroComponent heroComponent = componentManager.createComponent(context, HeroComponent.class);

        heroComponent.setName("Superman");
        componentManager.addComponent(clark, heroComponent);

        heroComponent.setName("Captain America");
        heroComponent = componentManager.addComponent(steve, heroComponent);

        heroComponent.setName("Iron Man");
        componentManager.addComponent(tony, heroComponent);

        /* Tony und Steve are humans */
        componentManager.addComponent(steve, HumanComponent.class);
        componentManager.addComponent(tony, HumanComponent.class);

        /* The Aspect class provides a couple of static operations to define aspects */
        Aspect humanSuperHeroesAspect = aspect().withAllOf(HeroComponent.class, HumanComponent.class);
        Aspect noneHumanSuperHeroesAspect = aspect().withAllOf(HeroComponent.class).withNoneOf(HumanComponent.class);

        /* Aspects are the key concept to query entities */
        List<EntityRef> humanSuperHeroes = entityManager.findEntities(context, humanSuperHeroesAspect);
        List<EntityRef> noneHumanSuperHeroes = entityManager.findEntities(context, noneHumanSuperHeroesAspect);

        System.out.println("\nHuman Super Heroes:");
        for (EntityRef hero : humanSuperHeroes)
        {
            HeroComponent component = componentManager.findComponent(hero, HeroComponent.class);
            System.out.println(" " + component.getName());
        }

        System.out.println("\nNone Human Super Heroes:");
        for (EntityRef hero : noneHumanSuperHeroes)
        {
            HeroComponent component = componentManager.findComponent(hero, HeroComponent.class);
            System.out.println(" " + component.getName());
        }

        System.out.println();

        /* Shutdown everything */
        context.dispose();
    }

    public interface HeroComponent
    extends Component
    {
        String getName();

        void setName(String name);
    }

    public interface HumanComponent
    extends Component
    {

    }
}
