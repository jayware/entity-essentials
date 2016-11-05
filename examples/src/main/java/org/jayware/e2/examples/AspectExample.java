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
package org.jayware.e2.examples;


import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;

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
        EventManager eventManager = context.getService(EventManager.class);
        EntityManager entityManager = context.getService(EntityManager.class);
        ComponentManager componentManager = context.getService(ComponentManager.class);

        componentManager.prepareComponent(context, HeroComponent.class);
        componentManager.prepareComponent(context, HumanComponent.class);

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
