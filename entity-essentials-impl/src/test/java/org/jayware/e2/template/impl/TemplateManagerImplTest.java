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


import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.component.api.ComponentPropertyAdapter;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.context.api.ContextProvider;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityPath;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.template.impl.TemplateWrapper.XmlComponentTemplate;
import org.jayware.e2.template.impl.TemplateWrapper.XmlEntityTemplate;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jayware.e2.entity.api.EntityPath.path;
import static org.jayware.e2.template.impl.TemplateWrapper.XmlPropertyTemplate;
import static org.jayware.e2.template.impl.TemplateWrapper.XmlTemplate;


public class TemplateManagerImplTest
{
    Context context;
    EntityManager entityManager;
    ComponentManager componentManager;

    TemplateManagerImpl testee;

    TemplateWrapper template;
    File templateFile = new File("template.xml");

    final File file = new File(new File(System.getProperty("user.dir")), "template.xml");

    @BeforeMethod
    public void setup()
    {
        context = ContextProvider.getInstance().createContext();
        entityManager = context.getEntityManager();

        componentManager = context.getComponentManager();
        componentManager.prepareComponent(context, TestComponentA.class);
        componentManager.prepareComponent(context, TestComponentB.class);
        componentManager.registerPropertyAdapter(context, EntityPathPropertyAdapter.class);

        testee = (TemplateManagerImpl) context.getTemplateManager();
        template = new TemplateWrapper(testee, templateFile.toURI());
    }

    @AfterMethod
    public void teardown()
    throws IOException
    {
        context.dispose();
        if (file.exists())
        {
            Files.delete(file.toPath());
        }
    }

    @Test
    public void testStoreEntity()
    {
        generateTestEntityTree();
        generateTestTemplate();

        TemplateWrapper probe = new TemplateWrapper(testee, file.toURI());
        testee.storeEntity(context, path("/a1/b2/*"), probe);

        assertThat(probe).isEqualTo(template);
    }

    @Test
    public void testLoadEntity()
    {
        generateTestTemplate();

        testee.loadEntity(context, path("/b2"), template);

        // TODO: Bei loadEntity gleich das entsprechende EntityRef zur�ckgeben.

        EntityRef ref = entityManager.getEntity(context, path("/b2"));
        TestComponentA componentA;
        TestComponentB componentB;

        assertThat(ref).isNotNull();

        ref = entityManager.getEntity(context, path("/b2/c1"));
        assertThat(ref).isNotNull();

        ref = entityManager.getEntity(context, path("/b2/c2"));
        assertThat(ref).isNotNull();

        componentA = componentManager.getComponent(ref, TestComponentA.class);
        assertThat(componentA.getValue()).isEqualTo(13.37f);
        assertThat(componentA.getDescription()).isEqualTo("Super duper");

        componentB = componentManager.getComponent(ref, TestComponentB.class);
        assertThat(componentB.getPath()).isEqualTo(path("foo/bar"));

        ref = entityManager.getEntity(context, path("/b2/c1/d1"));
        assertThat(ref).isNotNull();

        componentB = componentManager.getComponent(ref, TestComponentB.class);
        assertThat(componentB.getPath()).isEqualTo(path("foo/bar"));
    }

    public interface TestComponentA
    extends Component
    {
        String getDescription();

        void setDescription(String description);

        float getValue();

        void setValue(float z);
    }

    public static class EntityPathPropertyAdapter
    implements ComponentPropertyAdapter<EntityPath>
    {
        @Override
        public String marshal(Context context, EntityPath value)
        {
            return value.asString();
        }

        @Override
        public EntityPath unmarshal(Context context, String value)
        {
            return EntityPath.path(value);
        }
    }

    public interface TestComponentB
    extends Component
    {
        EntityPath getPath();

        void setPath(EntityPath path);
    }

    private void generateTestEntityTree()
    {
        EntityRef ref;
        TestComponentA componentA;
        TestComponentB componentB;

        ref = entityManager.createEntity(context, path("/a1/b1/c1"));
        componentB = componentManager.addComponent(ref, TestComponentB.class);
        componentB.setPath(path("foo/bar"));
        componentB.pushTo(ref);

        ref = entityManager.createEntity(context, path("/a1/b2/c1/d1"));
        componentB = componentManager.addComponent(ref, TestComponentB.class);
        componentB.setPath(path("foo/bar"));
        componentB.pushTo(ref);

        ref = entityManager.createEntity(context, path("/a1/b2/c2"));
        componentA = componentManager.addComponent(ref, TestComponentA.class);
        componentA.setDescription("Super duper");
        componentA.setValue(13.37f);
        componentA.pushTo(ref);
        componentB = componentManager.addComponent(ref, TestComponentB.class);
        componentB.setPath(path("foo/bar"));
        componentB.pushTo(ref);

        ref = entityManager.createEntity(context, path("/a2"));
    }

    private void generateTestTemplate()
    {
        XmlTemplate xmlTemplate = new XmlTemplate();
        XmlEntityTemplate entityTemplate;
        XmlComponentTemplate componentTemplate;

        XmlEntityTemplate root = new XmlEntityTemplate("b2");
        xmlTemplate.setRoot(root);

        entityTemplate = new XmlEntityTemplate("c2");
        componentTemplate = new XmlComponentTemplate(TestComponentA.class.getName());
        componentTemplate.getProperties().add(new XmlPropertyTemplate("description", "Super duper"));
        componentTemplate.getProperties().add(new XmlPropertyTemplate("value", "13.37"));
        entityTemplate.getComponents().add(componentTemplate);

        componentTemplate = new XmlComponentTemplate(TestComponentB.class.getName());
        componentTemplate.getProperties().add(new XmlPropertyTemplate("path", "foo/bar/"));
        entityTemplate.getComponents().add(componentTemplate);

        root.getChildren().add(entityTemplate);

        entityTemplate = new XmlEntityTemplate("c1");
        root.getChildren().add(entityTemplate);

        root = entityTemplate;

        entityTemplate = new XmlEntityTemplate("d1");
        componentTemplate = new XmlComponentTemplate(TestComponentB.class.getName());
        componentTemplate.getProperties().add(new XmlPropertyTemplate("path", "foo/bar/"));
        entityTemplate.getComponents().add(componentTemplate);
        root.getChildren().add(entityTemplate);

        template.setTemplate(xmlTemplate);
    }
}
