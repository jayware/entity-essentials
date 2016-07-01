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
package org.jayware.e2.component.impl.generation.plan;


import org.jayware.e2.component.api.Component;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ComponentGenerationPlan
{
    private final Class<? extends Component> myComponentClass;

    private ClassWriter myClassWriter;

    private Map<String, ComponentPropertyGenerationPlan> myPropertyGenerationPlans;

    private final String myGeneratedClassPackageName;
    private final String myGeneratedClassName;
    private File myOutputDirectory;

    public ComponentGenerationPlan(Class<? extends Component> component)
    {
        myComponentClass = component;
        myGeneratedClassPackageName = component.getPackage().getName();
        myGeneratedClassName = "_generated_" + component.getSimpleName();
        myPropertyGenerationPlans = new HashMap<String, ComponentPropertyGenerationPlan>();
    }

    public Class<? extends Component> getComponentClass()
    {
        return myComponentClass;
    }

    public String getGeneratedClassInternalName()
    {
        return getGeneratedClassPackagePath() + myGeneratedClassName;
    }

    public void addComponentPropertyGenerationPlan(ComponentPropertyGenerationPlan plan)
    {
        myPropertyGenerationPlans.put(plan.getPropertyName(), plan);
    }

    public void removeComponentPropertyGenerationPlan(ComponentPropertyGenerationPlan plan)
    {
        myPropertyGenerationPlans.remove(plan.getPropertyName());
    }

    public Collection<ComponentPropertyGenerationPlan> getComponentPropertyGenerationPlans()
    {
        return myPropertyGenerationPlans.values();
    }

    public String getGeneratedClassPackageName()
    {
        return myGeneratedClassPackageName;
    }

    public String getGeneratedClassPackagePath()
    {
        return getGeneratedClassPackageName().replace(".", "/") + "/";
    }

    public String getGeneratedClassSimpleName()
    {
        return myGeneratedClassName;
    }

    public void setOutputDirectory(File directory)
    {
        myOutputDirectory = directory;
    }

    public File getOutputDirectory()
    {
        return myOutputDirectory;
    }

    public File getGeneratedClassFile()
    {
        return new File(getOutputDirectory(), getGeneratedClassInternalName() + ".class");
    }

    public String getGeneratedClassName()
    {
        return getGeneratedClassPackageName() + "." + myGeneratedClassName;
    }

    public ClassWriter getClassWriter()
    {
        return myClassWriter;
    }

    public void setClassWriter(ClassWriter classWriter)
    {
        myClassWriter = classWriter;
    }
}
