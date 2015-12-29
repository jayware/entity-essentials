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
