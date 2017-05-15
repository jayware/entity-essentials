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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.jayware.e2.component.impl.TestComponents.TestComponentC;


public class TemplateManagerImplIntegrationTest
{
    private Context context;
    private EntityManager entityManager;
    private ComponentManager componentManager;
    private TemplateManager templateManager;

    private @Mocked TemplateProvider testTemplateProvider;

    @BeforeEach
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
            properties = new ArrayList<PropertyTemplate>();
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
