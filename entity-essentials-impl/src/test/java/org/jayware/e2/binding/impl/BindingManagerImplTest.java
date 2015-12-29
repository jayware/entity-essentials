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
package org.jayware.e2.binding.impl;


import org.jayware.e2.component.api.Component;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityRef;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class BindingManagerImplTest
{
    private BindingManagerImpl testee;

    @Mock private Context testContext;

    @Mock private EntityRef testRefA;
    @Mock private EntityRef testRefB;

    @BeforeMethod
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        testee = new BindingManagerImpl();
    }

    @Test
    public void test()
    {
//        final ComponentBinding<CmpA, CmpB> binding = testee.createComponentBinding().bind(testRefA, CmpA.class).to(testRefB, CmpB.class).by(new ComponentBindingRule<CmpA, CmpB>()
//        {
//            @Override
//            public void by(CmpA source, CmpB target)
//            {
//                target.setName(String.valueOf(source.getId()));
//            }
//        });
    }

    public interface CmpA
    extends Component
    {
        int getId();

        void setId(int id);
    }

    public interface CmpB
    extends Component
    {
        String getName();

        void setName(String name);
    }
}
