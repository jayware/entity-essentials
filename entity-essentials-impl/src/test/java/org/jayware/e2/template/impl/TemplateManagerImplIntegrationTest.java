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
package org.jayware.e2.template.impl;


import mockit.Expectations;
import mockit.Mocked;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.template.api.ComponentTemplate;
import org.jayware.e2.template.api.PropertyTemplate;
import org.jayware.e2.template.api.TemplateManager;
import org.jayware.e2.template.api.TemplateProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.jayware.e2.component.impl.TestComponents.*;


public class TemplateManagerImplIntegrationTest
{
    private Context context;
    private EntityManager entityManager;
    private ComponentManager componentManager;
    private TemplateManager templateManager;

    private @Mocked TemplateProvider testTemplateProvider;

    @BeforeMethod
    public void setUp()
    {
        context = ContextProvider.getInstance().createContext();
        entityManager = context.getService(EntityManager.class);
        componentManager = context.getService(ComponentManager.class);
        templateManager = context.getService(TemplateManager.class);

        new Expectations()
        {{
            testTemplateProvider.createEntityTemplate(); result = null; minTimes = 0;
            testTemplateProvider.createComponentTemplate((Class) any); result = new TestComponentTemplateImpl(); minTimes = 0;
            testTemplateProvider.createPropertyTemplate((Class) any); result = new TestPropertyTemplateImpl(); minTimes = 0;
        }};
    }

    @Test
    public void test()
    {
        componentManager.prepareComponent(context, TestComponentC.class);
        final TestComponentC component = componentManager.createComponent(context, TestComponentC.class);

        final TestComponentTemplateImpl template = templateManager.exportComponent(component, testTemplateProvider);
    }

    public static class TestComponentTemplateImpl
    implements ComponentTemplate
    {
        private Class type;
        private final List<PropertyTemplate> properties;

        public TestComponentTemplateImpl()
        {
            properties = new ArrayList<>();
        }

        @Override
        public Class getType()
        {
            return type;
        }

        @Override
        public void setType(Class type)
        {
            this.type = type;
        }

        @Override
        public List<PropertyTemplate> properties()
        {
            return properties;
        }
    }

    public static class TestPropertyTemplateImpl
    implements PropertyTemplate
    {
        private String name;
        private Class type;
        private Object value;

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public void setName(String name)
        {
            this.name = name;
        }

        @Override
        public Class getType()
        {
            return type;
        }

        @Override
        public void setType(Class type)
        {
            this.type = type;
        }

        @Override
        public Object getValue()
        {
            return value;
        }

        @Override
        public void setValue(Object value)
        {
            this.value = value;
        }
    }
}
