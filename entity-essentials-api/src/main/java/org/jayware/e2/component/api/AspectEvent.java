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
package org.jayware.e2.component.api;


import org.jayware.e2.entity.api.EntityEvent.EntityChangedEvent;
import org.jayware.e2.event.api.DeclarativeSanityChecker;
import org.jayware.e2.event.api.SanityCheck;


@SanityCheck(AspectEvent.AspectEventSanityChecker.class)
public interface AspectEvent
extends EntityChangedEvent
{
    /**
     * The new {@link Aspect} of the entity which is subject of the event.
     */
    String NewAspectParam = "org.jayware.e2.event.param.NewAspect";

    /**
     * The old {@link Aspect} of the entity which is subject of the event.
     */
    String OldAspectParam = "org.jayware.e2.event.param.OldAspect";

    interface AspectGainedEvent
    extends AspectEvent
    {
    }

    interface AspectLostEvent
    extends AspectEvent
    {
    }

    class AspectEventSanityChecker
    extends DeclarativeSanityChecker
    {
        @Override
        protected void setup(SanityCheckerRuleBuilder checker)
        {
            checker.check(AspectEvent.class).param(NewAspectParam, "NewAspectParam").notNull().instanceOf(Aspect.class).done();
            checker.check(AspectEvent.class).param(OldAspectParam, "OldAspectParam").notNull().instanceOf(Aspect.class).done();
        }
    }
}
