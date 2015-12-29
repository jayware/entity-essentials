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
package org.jayware.e2.component.impl;


import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentFactory;
import org.jayware.e2.component.api.ComponentFactoryException;
import org.jayware.e2.component.api.ComponentInstancer;
import org.jayware.e2.component.api.ComponentProperty;
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlanFactory;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.jayware.e2.component.impl.generation.writer.ComponentCopyConstructorWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentCopyOtherMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentCopyThisMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentDefaultConstructorWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentGetMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentHasMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentPropertiesMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentPropertyFieldWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentPropertyGetterMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentPropertySetterMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentSetMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentStaticInitializerWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentToStringMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentTypeMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentWriterFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.V1_8;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;


public class ComponentFactoryImpl
implements ComponentFactory
{
    private final ComponentWriterFactory myWriterFactory = new ComponentWriterFactory();
    private final ComponentGenerationPlanFactory myGenerationPlanFactory = new ComponentGenerationPlanFactory();

    private final File myOutputDirectory;

    private final Map<String, ComponentInstancer<?>> myCache;

    private boolean forceClassGeneration = true;

    public ComponentFactoryImpl()
    throws ComponentFactoryException
    {
        try
        {
            myOutputDirectory = new File(System.getProperty("user.dir") + "/.generated");

            if (!myOutputDirectory.exists())
            {
                myOutputDirectory.mkdirs();
            }

            myCache = new ConcurrentHashMap<>();
        }
        catch (Exception e)
        {
            throw new ComponentFactoryException("Failed to instantiate component factory!", e);
        }
    }

    @Override
    public void prepareComponent(Class<? extends Component> componentClass, Class<? extends Component>... componentClasses)
    throws ComponentFactoryException, MalformedComponentException
    {
        try
        {
            if (!myCache.containsKey(componentClass.getName()) || forceClassGeneration)
            {
                final ComponentGenerationPlan plan = analyseComponent(componentClass);
                generateComponentClass(plan);
            }

            if (componentClasses != null)
            {
                for (Class<?> clazz : componentClasses)
                {
                    if (!myCache.containsKey(clazz.getName()) || forceClassGeneration)
                    {
                        final ComponentGenerationPlan plan = analyseComponent(componentClass);
                        generateComponentClass(plan);
                    }
                }
            }
        }
        catch (MalformedComponentException e)
        {
            throw new MalformedComponentException("Failed to prepare component: " + componentClass.getName(), e);
        }
    }

    @Override
    public <T extends Component> ComponentInstancer<T> createComponent(Class<T> componentClass)
    throws ComponentFactoryException, MalformedComponentException
    {
        checkNotNull(componentClass, "The component's class mustn't be null to create a component!");
        return createComponent(componentClass.getName());
    }

    @Override
    public <C extends Component> ComponentInstancer<C> createComponent(String componentClass)
    throws ComponentFactoryException, MalformedComponentException
    {
        checkNotNull(componentClass, "The component's class name mustn't be null to create a component!");

        try
        {
            ComponentInstancer<C> instancer = (ComponentInstancer<C>) myCache.get(componentClass);

            if (instancer == null)
            {
                throw new ComponentFactoryException("Unprepared component! Cannot create component: " + componentClass);
            }

            return instancer;
        }
        catch (Exception e)
        {
            throw new ComponentFactoryException("Something went wrong on instantiating a component for: " + componentClass, e);
        }
    }

    private ComponentGenerationPlan analyseComponent(Class<? extends Component> componentClass)
    {
        final ComponentGenerationPlan componentGenerationPlan = myGenerationPlanFactory.createComponentGenerationPlan(componentClass);
        final Map<String, ComponentPropertyGenerationPlan> propertyDescriptorMap = new HashMap<>();

        for (Method method : componentClass.getDeclaredMethods())
        {
            final String methodName = method.getName();
            final String methodNamePrefix = methodName.substring(0, 3);

            ComponentPropertyGenerationPlan propertyGenerationPlan;
            String propertyName;

            if ("get".equals(methodNamePrefix) || "set".equals(methodNamePrefix))
            {
                propertyName = methodName.substring(3);
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                propertyGenerationPlan = propertyDescriptorMap.get(propertyName);

                final ComponentProperty componentPropertyAnnotation = method.getAnnotation(ComponentProperty.class);

                if (propertyGenerationPlan == null)
                {
                    propertyGenerationPlan = myGenerationPlanFactory.createComponentPropertyGenerationPlan(componentGenerationPlan, propertyName);
                    propertyDescriptorMap.put(propertyName, propertyGenerationPlan);
                }

                if (methodName.startsWith("get"))
                {
                    if (method.getParameterCount() != 0)
                    {
                        throw new MalformedComponentException("Invalid getter for property '" + propertyGenerationPlan.getPropertyName() + "'! A getter mustn't have any parameter!");
                    }

                    propertyGenerationPlan.setPropertyGetterMethod(method);
                    propertyGenerationPlan.setPropertyType(method.getReturnType());
                }
                else if (methodName.startsWith("set"))
                {
                    if (method.getParameterCount() != 1)
                    {
                        throw new MalformedComponentException("Invalid setter for property '" + propertyGenerationPlan.getPropertyName() + "'! A setter has to take exactly one parameter with the appropriate type!");
                    }

                    propertyGenerationPlan.setPropertySetterMethod(method);
                }

                if (componentPropertyAnnotation != null)
                {
                    if (propertyGenerationPlan.getPropertyAnnotation() != null)
                    {
                        throw new MalformedComponentException("Ambiguous ComponentProperty annotation for '" + propertyGenerationPlan.getPropertyName() + "' in '" + propertyGenerationPlan.getPropertyType().getName() + "'! Remove the annotation either from the setter or getter.");
                    }

                    propertyGenerationPlan.setPropertyAnnotation(componentPropertyAnnotation);
                }

                if (propertyGenerationPlan.hasGetter() && propertyGenerationPlan.hasSetter())
                {
                    propertyDescriptorMap.remove(propertyName);
                    componentGenerationPlan.addComponentPropertyGenerationPlan(propertyGenerationPlan);
                }
            }
        }

        if (!propertyDescriptorMap.isEmpty())
        {
            throw new MalformedComponentException();
        }

        return componentGenerationPlan;
    }

    private void generateComponentClass(ComponentGenerationPlan componentGenerationPlan)
    throws ComponentFactoryException, MalformedComponentException
    {
        final ComponentStaticInitializerWriter staticInitializerWriter = myWriterFactory.createComponentStaticInitializerWriter();
        final ComponentDefaultConstructorWriter defaultConstructorWriter = myWriterFactory.createComponentDefaultConstructorWriter();
        final ComponentCopyConstructorWriter copyConstructorWriter = myWriterFactory.createComponentCopyConstructorWriter();
        final ComponentPropertyGetterMethodWriter propertyGetterWriter = myWriterFactory.createComponentPropertyGetterWriter();
        final ComponentPropertySetterMethodWriter propertySetterWriter = myWriterFactory.createComponentPropertySetterWriter();
        final ComponentPropertyFieldWriter propertyFieldWriter = myWriterFactory.createComponentPropertyFieldWriter();
        final ComponentPropertiesMethodWriter propertiesMethodWriter = myWriterFactory.createPropertiesMethodWriter();
        final ComponentGetMethodWriter getMethodWriter = myWriterFactory.createComponentGetMethodWriter();
        final ComponentSetMethodWriter setMethodWriter = myWriterFactory.createComponentSetMethodWriter();
        final ComponentHasMethodWriter hasMethodWriter = myWriterFactory.createComponentHasMethodWriter();
        final ComponentTypeMethodWriter typeMethodWriter = myWriterFactory.createComponentTypeMethodWriter();
        final ComponentCopyOtherMethodWriter copyOtherMethodWriter = myWriterFactory.createComponentCopyOtherMethodWriter();
        final ComponentCopyThisMethodWriter copyThisMethodWriter = myWriterFactory.createComponentCopyThisMethodWriter();
        final ComponentToStringMethodWriter toStringMethodWriter = myWriterFactory.createComponentToStringMethodWriter();

        final Class<? extends Component> componentClass = componentGenerationPlan.getComponentClass();

        componentGenerationPlan.setOutputDirectory(myOutputDirectory);
        componentGenerationPlan.setClassWriter(new ClassWriter(ClassWriter.COMPUTE_FRAMES));

        final ClassWriter classWriter = componentGenerationPlan.getClassWriter();
        final String classInternalName = componentGenerationPlan.getGeneratedClassInternalName();

        classWriter.visit(
            V1_8,
            ACC_PUBLIC + ACC_SUPER,
            componentGenerationPlan.getGeneratedClassInternalName(),
            null,
            getInternalName(AbstractComponent.class),
            new String[]{getInternalName(componentClass)}
        );

        {
            final FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE + ACC_STATIC + ACC_FINAL, "ourProperties", getDescriptor(List.class), null, null);
        }

        for (ComponentPropertyGenerationPlan propertyPlan : componentGenerationPlan.getComponentPropertyGenerationPlans())
        {
            propertyFieldWriter.writePropertyFieldFor(propertyPlan);
        }

        staticInitializerWriter.writeStaticInitializerFor(componentGenerationPlan);

        defaultConstructorWriter.writeDefaultConstructorFor(componentGenerationPlan);
        copyConstructorWriter.writeCopyConstructorFor(componentGenerationPlan);

        for (ComponentPropertyGenerationPlan plan : componentGenerationPlan.getComponentPropertyGenerationPlans())
        {
            propertyGetterWriter.writePropertyGetterFor(plan);
            propertySetterWriter.writePropertySetterFor(plan);
        }

        propertiesMethodWriter.writePropertiesMethodFor(componentGenerationPlan);

        getMethodWriter.writeGetMethodFor(componentGenerationPlan);

        setMethodWriter.writeSetMethodFor(componentGenerationPlan);

        hasMethodWriter.writeHasMethodFor(componentGenerationPlan);

        typeMethodWriter.writeTypeMethodFor(componentGenerationPlan);

        copyThisMethodWriter.writeCopyThisMethodFor(componentGenerationPlan);

        copyOtherMethodWriter.writeCopyOtherMethodFor(componentGenerationPlan);

        toStringMethodWriter.writeToStringMethodFor(componentGenerationPlan);

        try
        {
            final File classFile = componentGenerationPlan.getGeneratedClassFile();
            final File parentFile = classFile.getParentFile();

            if (!parentFile.exists())
            {
                parentFile.mkdirs();
            }

            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(classFile));
            dataOutputStream.write(classWriter.toByteArray());
            dataOutputStream.flush();
            dataOutputStream.close();
        }
        catch (IOException e)
        {
            throw new ComponentFactoryException("Failed to write class file to: " + myOutputDirectory.getAbsolutePath(), e);
        }

        try
        {
            final ClassLoader classLoader = new URLClassLoader(new URL[]{myOutputDirectory.toURI().toURL()}, componentClass.getClassLoader());
            final Class<? extends Component> loadedClass = (Class<? extends Component>) classLoader.loadClass(componentGenerationPlan.getGeneratedClassName());
            myCache.put(componentClass.getName(), new ComponentInstancerImpl<>(componentGenerationPlan, loadedClass));
        }
        catch (ClassNotFoundException | MalformedURLException e)
        {
            throw new ComponentFactoryException("Failed to load class '" + classInternalName + "' from: " + myOutputDirectory.getAbsolutePath(), e);
        }
    }

    @Override
    public String toString()
    {
        return "ComponentFactoryServiceImpl{" +
        "myOutputDirectory=" + myOutputDirectory +
        ", myCache=" + myCache +
        '}';
    }
}
