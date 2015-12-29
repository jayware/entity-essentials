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


import org.jayware.e2.template.impl.TemplateWrapper.XmlComponentTemplate;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class TemplateImplXmlComponentTemplateTest
{
    @Test
    public void testEquals()
    {
        TemplateWrapper.XmlPropertyTemplate propertyA = new TemplateWrapper.XmlPropertyTemplate("foo", "bar");
        TemplateWrapper.XmlPropertyTemplate propertyB = new TemplateWrapper.XmlPropertyTemplate("pi", "3.1415");

        TemplateWrapper.XmlComponentTemplate componentA = new TemplateWrapper.XmlComponentTemplate("fubar");
        componentA.getProperties().add(propertyA);
        componentA.getProperties().add(propertyB);

        TemplateWrapper.XmlComponentTemplate componentB= new XmlComponentTemplate("fubar");
        componentB.getProperties().add(propertyB);
        componentB.getProperties().add(propertyA);

        assertThat(componentA).isEqualTo(componentB);
    }
}
