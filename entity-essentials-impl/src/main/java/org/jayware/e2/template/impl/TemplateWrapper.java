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


import org.jayware.e2.template.api.ComponentTemplate;
import org.jayware.e2.template.api.EntityTemplate;
import org.jayware.e2.template.api.PropertyTemplate;
import org.jayware.e2.template.api.Template;
import org.jayware.e2.template.api.TemplateManager;
import org.jayware.e2.template.api.TemplateVisitor;
import org.jayware.e2.template.api.TemplateVisitorContext;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import static java.util.Objects.hash;
import static javax.xml.bind.annotation.XmlAccessType.NONE;


public class TemplateWrapper
implements Template
{
    private final TemplateManager myTemplateManager;
    private final URI myURI;

    private XmlTemplate myTemplate;

    public TemplateWrapper(TemplateManager templateManager, URI uri)
    {
        myTemplateManager = templateManager;
        myURI = uri;
    }

    @Override
    public URI uri()
    {
        return myURI;
    }

    @Override
    public void read()
    {
        myTemplateManager.readTemplate(this, myURI);
    }

    @Override
    public void write()
    {
        myTemplateManager.writeTemplate(this, myURI);
    }

    @Override
    public void accept(TemplateVisitor visitor)
    {
        accept(visitor, new TemplateVisitorContextImpl());
    }

    @Override
    public void accept(TemplateVisitor visitor, TemplateVisitorContext context)
    {
        final Queue<XmlEntityTemplate> queue = new LinkedList<>();
        queue.add(myTemplate.getRoot());

        while (!queue.isEmpty())
        {
            final XmlEntityTemplate entity = queue.poll();

            visitor.visitEntityTemplate(context, entity);

            for (ComponentTemplate component : new ArrayList<>(entity.components()))
            {
                visitor.visitComponentTemplate(context, entity, component);

                for (PropertyTemplate property : new ArrayList<>(component.properties()))
                {
                    visitor.visitPropertyTemplate(context, entity, component, property);
                }
            }

            queue.addAll(entity.getChildren());
        }
    }

    public XmlTemplate getTemplate()
    {
        return myTemplate;
    }

    public void setTemplate(XmlTemplate template)
    {
        myTemplate = template;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        TemplateWrapper template = (TemplateWrapper) o;

        return Objects.equals(myURI, template.myURI)
            && Objects.equals(myTemplate, template.myTemplate);
    }

    @Override
    public int hashCode()
    {
        return hash(myURI, myTemplate);
    }

    @Override
    public String toString()
    {
        return "TemplateWrapper{" +
        "uri=" + myURI +
        '}';
    }

    @XmlRootElement(name = "template")
    @XmlAccessorType(NONE)
    public static class XmlTemplate
    {
        @XmlAttribute(name = "version")
        private String version = "1.0";

        @XmlElement(name = "entity")
        private XmlEntityTemplate myRootEntityTemplate;

        public XmlTemplate()
        {
        }

        public XmlTemplate(XmlEntityTemplate rootEntityTemplate)
        {
            myRootEntityTemplate = rootEntityTemplate;
        }

        public XmlEntityTemplate getRoot()
        {
            return myRootEntityTemplate;
        }

        public void setRoot(XmlEntityTemplate root)
        {
            myRootEntityTemplate = root;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            XmlTemplate that = (XmlTemplate) o;

            return Objects.equals(myRootEntityTemplate, that.myRootEntityTemplate);
        }

        @Override
        public int hashCode()
        {
            return hash(myRootEntityTemplate);
        }
    }

    @XmlAccessorType(NONE)
    public static class XmlEntityTemplate
    implements EntityTemplate
    {
        @XmlAttribute(name = "name", required = true)
        private String myName;

        @XmlElement(name = "entity")
        private List<XmlEntityTemplate> myChildren = new ArrayList<>();

        @XmlElement(name = "component")
        private List<XmlComponentTemplate> myComponents = new ArrayList<>();

        public XmlEntityTemplate()
        {
        }

        public XmlEntityTemplate(String name)
        {
            myName = name;
        }

        public String getName()
        {
            return myName;
        }

        public void setName(String name)
        {
            myName = name;
        }

        public List<XmlEntityTemplate> getChildren()
        {
            return myChildren;
        }

        @Override
        public List<? extends EntityTemplate> children()
        {
            return myChildren;
        }

        public List<XmlComponentTemplate> getComponents()
        {
            return myComponents;
        }

        public List<? extends ComponentTemplate> components()
        {
            return myComponents;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            XmlEntityTemplate that = (XmlEntityTemplate) o;

            return Objects.equals(myName, that.myName)
                && myChildren.containsAll(that.myChildren) && that.myChildren.containsAll(myChildren)
                && myComponents.containsAll(that.myComponents) && that.myComponents.containsAll(myComponents);
        }

        @Override
        public int hashCode()
        {
            return hash(myName, myChildren, myComponents);
        }

        @Override
        public String toString()
        {
            return "XmlEntityTemplate{" +
            "name='" + myName + '\'' +
            ", children=" + myChildren.size() +
            ", components=" + myComponents.size() +
            '}';
        }
    }

    @XmlAccessorType(NONE)
    public static class XmlComponentTemplate
    implements ComponentTemplate
    {
        @XmlAttribute(name = "name", required = true)
        private String myName;

        @XmlElement(name = "property")
        private List<XmlPropertyTemplate> myProperties = new ArrayList<>();

        public XmlComponentTemplate()
        {
        }

        public XmlComponentTemplate(String name)
        {
            myName = name;
        }

        public String getName()
        {
            return myName;
        }

        public void setName(String name)
        {
            myName = name;
        }

        public List<XmlPropertyTemplate> getProperties()
        {
            return myProperties;
        }

        @Override
        public List<? extends PropertyTemplate> properties()
        {
            return myProperties;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            XmlComponentTemplate that = (XmlComponentTemplate) o;

            return Objects.equals(myName, that.myName)
                && myProperties.containsAll(that.myProperties)
                && that.myProperties.containsAll(myProperties);
        }

        @Override
        public int hashCode()
        {
            return hash(myName, myProperties);
        }

        @Override
        public String toString()
        {
            return "XmlComponentTemplate{" +
            "name='" + myName + '\'' +
            ", properties=" + myProperties.size() +
            '}';
        }
    }

    @XmlAccessorType(NONE)
    public static class XmlPropertyTemplate
    implements PropertyTemplate
    {
        @XmlAttribute(name = "name", required = true)
        private String myName;

        @XmlValue
        private String myValue;

        public XmlPropertyTemplate()
        {
        }

        public XmlPropertyTemplate(String name, String value)
        {
            myName = name;
            myValue = value;
        }

        public String getName()
        {
            return myName;
        }

        public void setName(String name)
        {
            myName = name;
        }

        public String getValue()
        {
            return myValue;
        }

        public void setValue(String value)
        {
            myValue = value;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            XmlPropertyTemplate that = (XmlPropertyTemplate) o;

            return Objects.equals(myName, that.myName) && Objects.equals(myValue, that.myValue);
        }

        @Override
        public int hashCode()
        {
            return hash(myName, myValue);
        }

        @Override
        public String toString()
        {
            return "XmlPropertyTemplate{" +
            "name='" + myName + '\'' +
            ", value='" + myValue + '\'' +
            '}';
        }
    }
}
