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
package org.jayware.e2.storage.impl;

import org.jayware.e2.component.api.Aspect;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.storage.api.EntityFinder;
import org.jayware.e2.storage.api.EntityFinderException;
import org.jayware.e2.util.Filter;
import org.jayware.e2.util.Provider;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class EntityFinderImpl
implements EntityFinder
{
    private final Context myContext;
    private final Provider<List<EntityRef>> myListProvider;

    public EntityFinderImpl(final Context context, final Provider<List<EntityRef>> listProvider)
    {
        myContext = context;
        myListProvider = listProvider;
    }

    @Override
    public List<EntityRef> filter(final Collection<EntityRef> entities, final Aspect aspect, final List<Filter<EntityRef>> filters)
    {
        final List<EntityRef> result = myListProvider.provide();
        final Aspect computationAspect = aspect != null ? aspect : Aspect.ANY;
        final List<Filter<EntityRef>> computationFilterList = filters != null ? filters : Collections.<Filter<EntityRef>>emptyList();

        for (EntityRef ref : entities)
        {
            boolean addToResult = filterEntity(ref, computationAspect, computationFilterList);

            if (addToResult)
            {
                result.add(ref);
            }
        }

        return result;
    }

    boolean filterEntity(final EntityRef ref, final Aspect aspect, final List<Filter<EntityRef>> filters)
    {
        if (!aspect.matches(ref))
        {
            return false;
        }

        for (Filter<EntityRef> filter : filters)
        {
            if (!applyFilter(ref, filter))
            {
                return false;
            }
        }

        return true;
    }

    private boolean applyFilter(final EntityRef ref, final Filter<EntityRef> filter)
    {
        try
        {
            return filter.accepts(myContext, ref);
        }
        catch (Exception e)
        {
            throw new EntityFinderException("Failed to apply filter '%s' on entity '%s' because filter threw exception!", e, filter, ref);
        }
    }
}
