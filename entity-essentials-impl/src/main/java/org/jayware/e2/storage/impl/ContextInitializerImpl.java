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
package org.jayware.e2.storage.impl;

import org.jayware.e2.component.api.Component;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextInitializer;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.event.api.EventManager;
import org.jayware.e2.storage.api.ComponentDatabase;
import org.jayware.e2.storage.api.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.jayware.e2.storage.impl.StorageImpl.STORAGE_KEY;


public class ContextInitializerImpl
implements ContextInitializer
{
    @Override
    public void initialize(Context context)
    {
        final EventManager eventManager = context.getService(EventManager.class);
        final ComponentDatabase componentDatabase = new ComponentDatabaseImpl(new HashMap<Class<? extends Component >, Map<EntityRef, Component>>());
        final Storage storage = new StorageImpl(context, new HashMap<UUID, EntityRef>(), componentDatabase);

        context.put(STORAGE_KEY, storage);

        eventManager.subscribe(context, storage);
    }
}
