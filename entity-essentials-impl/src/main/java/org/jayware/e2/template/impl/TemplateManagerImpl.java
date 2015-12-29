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

import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentManager;
import org.jayware.e2.context.api.Context;
import org.jayware.e2.entity.api.EntityManager;
import org.jayware.e2.entity.api.EntityPath;
import org.jayware.e2.entity.api.EntityRef;
import org.jayware.e2.template.api.Template;
import org.jayware.e2.template.api.TemplateInstantiationException;
import org.jayware.e2.template.api.TemplateManager;
import org.jayware.e2.template.api.TemplateManagerException;
import org.jayware.e2.template.impl.TemplateWrapper.XmlTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.jayware.e2.entity.api.EntityPathGlobFilter.globFilter;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.jayware.e2.util.Traversal.BreadthFirstLeftToRight;


public class TemplateManagerImpl
implements TemplateManager
{
    private final JAXBContext myJAXBContext;
    private final Marshaller myMarshaller;
    private final Unmarshaller myUnmarshaller;

    public TemplateManagerImpl()
    {
        try
        {
            myJAXBContext = JAXBContext.newInstance(XmlTemplate.class);
            myMarshaller = myJAXBContext.createMarshaller();
            myMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            myUnmarshaller = myJAXBContext.createUnmarshaller();
        }
        catch (JAXBException e)
        {
            throw new TemplateManagerException(e);
        }
    }

    @Override
    public Template createTemplate(URI uri)
    {
        return new TemplateWrapper(this, uri);
    }

    @Override
    public Template createTemplate(File file)
    {
        return createTemplate(file.toURI());
    }

    @Override
    public Template createTemplate(String data)
    {
        final TemplateWrapper template = new TemplateWrapper(this, null);

        try (final StringReader stringReader = new StringReader(data))
        {
            final XmlTemplate xmlTemplate = (XmlTemplate) myUnmarshaller.unmarshal(stringReader);
            template.setTemplate(xmlTemplate);
            return template;
        }
        catch (JAXBException e)
        {
            throw new TemplateManagerException(e);
        }
    }

    @Override
    public void readTemplate(Template template)
    {
        readTemplate(template, template.uri());
    }

    @Override
    public void readTemplate(Template template, URI uri)
    {
        try (final FileInputStream inputStream = new FileInputStream(new File(uri)))
        {
            final XmlTemplate xmlTemplate = (XmlTemplate) myUnmarshaller.unmarshal(inputStream);
            ((TemplateWrapper) template).setTemplate(xmlTemplate);
        }
        catch (IOException | JAXBException e)
        {
            throw new TemplateManagerException(e);
        }
    }

    @Override
    public void readTemplate(Template template, File file)
    {
        readTemplate(template, file.toURI());
    }

    @Override
    public void writeTemplate(Template template)
    {
        writeTemplate(template, template.uri());
    }

    @Override
    public void writeTemplate(Template template, URI uri)
    {
        checkNotNull(template);
        checkNotNull(uri);

        try (final FileOutputStream outputStream = new FileOutputStream(new File(uri)))
        {
            final XmlTemplate xmlTemplate = ((TemplateWrapper) template).getTemplate();
            myMarshaller.marshal(xmlTemplate, outputStream);
        }
        catch (IOException | JAXBException e)
        {
            throw new TemplateManagerException(e);
        }
    }

    @Override
    public void writeTemplate(Template template, File file)
    {
        writeTemplate(template, file.toURI());
    }

    @Override
    public void importEntity(Context context, EntityPath path, URI uri)
    {
        importEntity(context, path, createTemplate(uri));
    }

    @Override
    public void importEntity(Context context, EntityPath path, File file)
    {
        importEntity(context, path, createTemplate(file));
    }

    @Override
    public void importEntity(Context context, EntityPath path, Template template)
    {
        checkNotNull(context);
        checkNotNull(path);
        checkNotNull(template);

        readTemplate(template);
        loadEntity(context, path, template);
    }

    @Override
    public void exportEntity(Context context, EntityPath path, URI uri)
    {
        exportEntity(context, path, createTemplate(uri));
    }

    @Override
    public void exportEntity(Context context, EntityPath path, File file)
    {
        exportEntity(context, path, createTemplate(file));
    }

    @Override
    public void exportEntity(Context context, EntityPath path, Template template)
    {
        storeEntity(context, path, template);
        writeTemplate(template);
    }

    @Override
    public void storeEntity(Context context, EntityPath path, Template template)
    throws IllegalArgumentException
    {
        checkNotNull(context);
        checkNotNull(path);
        checkNotNull(template);

        final EntityManager entityManager = context.getEntityManager();

        final Collection<EntityRef> entities = entityManager.findEntities(context, BreadthFirstLeftToRight, globFilter().include(path));
        final Queue<EntityRef> refQueue = new LinkedList<>(entities);
        final Map<EntityPath, TemplateWrapper.XmlEntityTemplate> parentMap = new HashMap<>();

        EntityRef ref = refQueue.poll();
        EntityPath refPath = ref.getPath();
        TemplateWrapper.XmlEntityTemplate entityTemplate;

        final TemplateWrapper.XmlEntityTemplate rootEntityTemplate = new TemplateWrapper.XmlEntityTemplate(refPath.getName());
        storeComponents(ref, rootEntityTemplate);
        parentMap.put(refPath, rootEntityTemplate);

        while (!refQueue.isEmpty())
        {
            ref = refQueue.poll();
            refPath = ref.getPath();
            entityTemplate = new TemplateWrapper.XmlEntityTemplate(ref.getPath().getName());
            storeComponents(ref, entityTemplate);
            parentMap.get(refPath.getParent()).getChildren().add(entityTemplate);
            parentMap.put(refPath, entityTemplate);
        }

        final XmlTemplate xmlTemplate = new XmlTemplate(rootEntityTemplate);
        ((TemplateWrapper) template).setTemplate(xmlTemplate);
    }

    protected void storeComponents(EntityRef ref, TemplateWrapper.XmlEntityTemplate template)
    {
        final Context context = ref.getContext();
        final ComponentManager componentManager = context.getComponentManager();

        for (Component component : componentManager.getComponents(ref))
        {
            final AbstractComponent abstractComponent = (AbstractComponent) component;
            final TemplateWrapper.XmlComponentTemplate componentTemplate = new TemplateWrapper.XmlComponentTemplate(abstractComponent.type().getName());

            for (String name : abstractComponent.properties())
            {
                final String value = abstractComponent.get(name);
                componentTemplate.getProperties().add(new TemplateWrapper.XmlPropertyTemplate(name, value));
            }

            template.getComponents().add(componentTemplate);
        }
    }

    @Override
    public void loadEntity(Context context, EntityPath path, Template template)
    throws IllegalArgumentException, TemplateInstantiationException
    {
        checkNotNull(context);
        checkNotNull(path);
        checkNotNull(template);

        if (path.isRelative())
        {
            throw new IllegalArgumentException("The passed entity path mustn't be relative to load an entity!");
        }

        final EntityManager entityManager = context.getEntityManager();
        final Queue<TemplateQueueElement> templateQueue = new LinkedList<>();
        final XmlTemplate xmlTemplate = ((TemplateWrapper) template).getTemplate();
        final TemplateWrapper.XmlEntityTemplate rootEntityTemplate;
        final EntityRef rootEntity;

        if (xmlTemplate == null)
        {
            throw new IllegalStateException("Passed template has not been read! The readTemplate operation has to be called first!");
        }

        rootEntityTemplate = xmlTemplate.getRoot();
        rootEntity = entityManager.createEntity(context, path);

        try
        {
            loadComponents(rootEntity, rootEntityTemplate);

            for (TemplateWrapper.XmlEntityTemplate xmlEntityTemplate : rootEntityTemplate.getChildren())
            {
                templateQueue.add(new TemplateQueueElement(path, xmlEntityTemplate));
            }

            while (!templateQueue.isEmpty())
            {
                final TemplateQueueElement element = templateQueue.poll();
                final EntityPath entityPath = element.path.append(element.template.getName());

                EntityRef ref = entityManager.createEntity(context, entityPath);
                loadComponents(ref, element.template);

                for (TemplateWrapper.XmlEntityTemplate entityTemplate : element.template.getChildren())
                {
                    templateQueue.add(new TemplateQueueElement(entityPath, entityTemplate));
                }
            }
        }
        catch (TemplateInstantiationException e)
        {
            entityManager.deleteEntity(rootEntity);
            throw e;
        }
    }

    @Override
    public String printTemplate(Template template)
    {
        checkNotNull(template);

        try (final StringWriter stringWriter = new StringWriter())
        {
            final XmlTemplate xmlTemplate = ((TemplateWrapper) template).getTemplate();
            myMarshaller.marshal(xmlTemplate, stringWriter);

            return stringWriter.toString();
        }
        catch (IOException | JAXBException e)
        {
            throw new TemplateManagerException(e);
        }
    }

    protected void loadComponents(EntityRef ref, TemplateWrapper.XmlEntityTemplate template)
    {
        final ComponentManager componentManager = ref.getContext().getComponentManager();

        for (TemplateWrapper.XmlComponentTemplate componentTemplate : template.getComponents())
        {
            final Class<? extends Component> component = componentManager.resolveComponent(ref.getContext(), componentTemplate.getName());

            if (component == null)
            {
                throw new TemplateInstantiationException("Could not resolve component: " + componentTemplate.getName() + "! All components used in the template have to be prepared first!");
            }

            final AbstractComponent instance = (AbstractComponent) componentManager.addComponent(ref, component);

            try
            {
                for (TemplateWrapper.XmlPropertyTemplate propertyTemplate : componentTemplate.getProperties())
                {
                    instance.set(propertyTemplate.getName(), propertyTemplate.getValue());
                }

                componentManager.pushComponent(ref, instance);
            }
            catch (Exception e)
            {
                componentManager.removeComponent(ref, component);
                throw new TemplateInstantiationException("Failed to set component properties of component: " + componentTemplate.getName(), e);
            }
        }
    }

    private static class TemplateQueueElement
    {
        public EntityPath path;
        public TemplateWrapper.XmlEntityTemplate template;

        public TemplateQueueElement(EntityPath path, TemplateWrapper.XmlEntityTemplate template)
        {
            this.path = path;
            this.template = template;
        }

        @Override
        public String toString()
        {
            return "TemplateQueueElement{" +
            "path=" + path +
            ", template=" + template +
            '}';
        }
    }
}
