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
