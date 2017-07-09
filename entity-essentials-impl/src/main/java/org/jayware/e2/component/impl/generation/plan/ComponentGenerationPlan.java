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
package org.jayware.e2.component.impl.generation.plan;


import org.jayware.e2.component.api.Component;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Type.getInternalName;


public class ComponentGenerationPlan
{
    private final Class<? extends Component> myComponentType;

    private ClassWriter myClassWriter;

    private Map<String, ComponentPropertyGenerationPlan> myPropertyGenerationPlans;

    private final String myGeneratedClassPackageName;
    private final String myGeneratedClassName;
    private File myOutputDirectory;

    public ComponentGenerationPlan(Class<? extends Component> component)
    {
        myComponentType = component;
        myGeneratedClassPackageName = component.getPackage().getName();
        myGeneratedClassName = "_generated_" + component.getSimpleName();
        myPropertyGenerationPlans = new HashMap<String, ComponentPropertyGenerationPlan>();
    }

    public Class<? extends Component> getComponentType()
    {
        return myComponentType;
    }

    public String getComponentTypeInternalName()
    {
        return getInternalName(myComponentType);
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
