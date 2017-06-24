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
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlan;
import org.jayware.e2.component.impl.generation.plan.ComponentGenerationPlanFactory;
import org.jayware.e2.component.impl.generation.plan.ComponentPropertyGenerationPlan;
import org.jayware.e2.component.impl.generation.writer.ComponentCopyConstructorWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentCopyOtherMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentCopyThisMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentDefaultConstructorWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentEqualsMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentGetMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentGetPropertyNamesMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentGetPropertyTypesMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentHasMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentHashCodeMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentPropertyFieldWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentPropertyGetterMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentPropertySetterMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentSetMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentStaticInitializerWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentToStringMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentTypeMethodWriter;
import org.jayware.e2.component.impl.generation.writer.ComponentWriterFactory;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Class.forName;
import static java.util.Arrays.asList;
import static org.jayware.e2.util.IOUtil.closeQuietly;
import static org.jayware.e2.util.Preconditions.checkNotNull;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.V1_6;
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

    private final Object myLock = new Object();

    private final Logger log = LoggerFactory.getLogger(ComponentFactoryImpl.class);

    public ComponentFactoryImpl()
    throws ComponentFactoryException
    {
        try
        {
            myOutputDirectory = new File(System.getProperty("user.dir") + "/.generated");

            if (!myOutputDirectory.exists())
            {
                if (!myOutputDirectory.mkdirs())
                {
                    throw new IOException("Failed to create output directory: " + myOutputDirectory.getAbsolutePath());
                }
            }

            myCache = new ConcurrentHashMap<String, ComponentInstancer<?>>();
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
            if (!isComponentPrepared(componentClass) || forceClassGeneration)
            {
                prepareComponent(componentClass);
            }

            if (componentClasses != null)
            {
                for (Class<?> clazz : componentClasses)
                {
                    if (!isComponentPrepared(componentClass) || forceClassGeneration)
                    {
                        prepareComponent(componentClass);
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

        final String componentClassName = componentClass.getName();

        try
        {
            if (!isComponentPrepared(componentClass))
            {
                prepareComponent(componentClass);
            }

            final ComponentInstancer<T> instancer = (ComponentInstancer<T>) myCache.get(componentClassName);

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

    @Override
    public <C extends Component> ComponentInstancer<C> createComponent(String componentClass)
    throws ComponentFactoryException, MalformedComponentException, ClassNotFoundException
    {
        checkNotNull(componentClass, "The component's class name mustn't be null to create a component!");

        return createComponent((Class<C>) forName(componentClass));
    }

    @Override
    public boolean isComponentPrepared(final Class<? extends Component> componentClass)
    {
        return myCache.containsKey(componentClass.getName());
    }

    private void prepareComponent(final Class<? extends Component> componentClass)
    {
        synchronized (myLock)
        {
            if (!isComponentPrepared(componentClass))
            {
                log.debug("Preparing Component: {}", componentClass.getName());
                generateComponentClass(analyseComponent(componentClass));
            }
        }
    }

    private ComponentGenerationPlan analyseComponent(Class<? extends Component> componentClass)
    {
        final ComponentGenerationPlan componentGenerationPlan = myGenerationPlanFactory.createComponentGenerationPlan(componentClass);
        final Map<String, ComponentPropertyGenerationPlan> propertyDescriptorMap = new HashMap<String, ComponentPropertyGenerationPlan>();
        final Queue<Class> componentClasses = new LinkedList<Class>();
        final Set<Method> methods = new HashSet<Method>();

        componentClasses.add(componentClass);
        while (!componentClasses.isEmpty())
        {
            final Class aClass = componentClasses.poll();
            for (Class interfaceClass : aClass.getInterfaces())
            {
                if (!(interfaceClass.isInterface()))
                {
                    throw new MalformedComponentException("Invalid inheritance of a non component interface: " + interfaceClass.getName());
                }

                if (!interfaceClass.equals(Component.class))
                {
                    componentClasses.add(interfaceClass);
                }
            }

            methods.addAll(asList(aClass.getDeclaredMethods()));
        }

        for (Method method : methods)
        {
            final int parameterCount = method.getParameterTypes().length;
            final String methodName = method.getName();
            final String methodNamePrefix = methodName.substring(0, 3);
            final boolean isGetter = "get".equals(methodNamePrefix);
            final boolean isSetter = "set".equals(methodNamePrefix);

            ComponentPropertyGenerationPlan propertyGenerationPlan;
            String propertyName;

            if (isGetter || isSetter)
            {
                propertyName = methodName.substring(3);
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
                propertyGenerationPlan = propertyDescriptorMap.get(propertyName);

                if (propertyGenerationPlan == null)
                {
                    propertyGenerationPlan = myGenerationPlanFactory.createComponentPropertyGenerationPlan(componentGenerationPlan, propertyName);
                    propertyDescriptorMap.put(propertyName, propertyGenerationPlan);
                }

                if (isGetter)
                {
                    if (parameterCount != 0)
                    {
                        throw new MalformedComponentException("Invalid getter for property '" + propertyGenerationPlan.getPropertyName() + "'! A getter mustn't have any parameter!");
                    }

                    if (propertyGenerationPlan.hasSetter())
                    {
                        final Method setter = propertyGenerationPlan.getPropertySetterMethod();
                        final Class<?> parameterType = setter.getParameterTypes()[0];

                        if (!parameterType.equals(method.getReturnType()))
                        {
                            throw new MalformedComponentException("Invalid getter for property '" + propertyGenerationPlan.getPropertyName() + "'! The return type of the getter does not match the parameter type of the setter!");
                        }
                    }

                    propertyGenerationPlan.setPropertyGetterMethod(method);
                    propertyGenerationPlan.setPropertyType(method.getReturnType());
                }
                else if (isSetter)
                {
                    if (parameterCount != 1)
                    {
                        throw new MalformedComponentException("Invalid setter for property '" + propertyGenerationPlan.getPropertyName() + "'! A setter has to take exactly one parameter with the appropriate type!");
                    }

                    if (propertyGenerationPlan.hasGetter())
                    {
                        final Method getter = propertyGenerationPlan.getPropertyGetterMethod();
                        final Class<?> returnType = getter.getReturnType();

                        if (!returnType.equals(method.getParameterTypes()[0]))
                        {
                            throw new MalformedComponentException("Invalid setter for property '" + propertyGenerationPlan.getPropertyName() + "'! The parameter type of the setter does not match the return type of the getter!");
                        }
                    }

                    propertyGenerationPlan.setPropertySetterMethod(method);
                }
            }
            else
            {
                throw new MalformedComponentException("Method '" + method + "' is neither a setter nor a getter!");
            }
        }

        for (ComponentPropertyGenerationPlan propertyGenerationPlan : propertyDescriptorMap.values())
        {
            if (propertyGenerationPlan.isComplete())
            {
                componentGenerationPlan.addComponentPropertyGenerationPlan(propertyGenerationPlan);
            }
            else
            {
                final String propertyName = propertyGenerationPlan.getPropertyName();

                if (!propertyGenerationPlan.hasGetter())
                {
                    throw new MalformedComponentException("There is no getter for property: " + propertyName);
                }
                else if (!propertyGenerationPlan.hasSetter())
                {
                    throw new MalformedComponentException("There is no setter for property: " + propertyName);
                }
                else
                {
                    throw new MalformedComponentException("Incomplete ComponentPropertyGenerationPlan: " + propertyGenerationPlan);
                }
            }
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
        final ComponentGetPropertyNamesMethodWriter getPropertyNamesMethodWriter = myWriterFactory.createGetPropertyNamesMethodWriter();
        final ComponentGetPropertyTypesMethodWriter getPropertyTypeNamesMethodWriter = myWriterFactory.createGetPropertyTypeNamesMethodWriter();
        final ComponentGetMethodWriter getMethodWriter = myWriterFactory.createComponentGetMethodWriter();
        final ComponentSetMethodWriter setMethodWriter = myWriterFactory.createComponentSetMethodWriter();
        final ComponentHasMethodWriter hasMethodWriter = myWriterFactory.createComponentHasMethodWriter();
        final ComponentTypeMethodWriter typeMethodWriter = myWriterFactory.createComponentTypeMethodWriter();
        final ComponentCopyOtherMethodWriter copyOtherMethodWriter = myWriterFactory.createComponentCopyOtherMethodWriter();
        final ComponentCopyThisMethodWriter copyThisMethodWriter = myWriterFactory.createComponentCopyThisMethodWriter();
        final ComponentEqualsMethodWriter equalsMethodWriter = myWriterFactory.createComponentEqualsMethodWriter();
        final ComponentHashCodeMethodWriter hashCodeMethodWriter = myWriterFactory.createComponentHashcodeMethodWriter();
        final ComponentToStringMethodWriter toStringMethodWriter = myWriterFactory.createComponentToStringMethodWriter();

        final Class<? extends Component> componentClass = componentGenerationPlan.getComponentType();

        componentGenerationPlan.setOutputDirectory(myOutputDirectory);
        componentGenerationPlan.setClassWriter(new ClassWriter(ClassWriter.COMPUTE_FRAMES));

        final ClassWriter classWriter = componentGenerationPlan.getClassWriter();
        final String classInternalName = componentGenerationPlan.getGeneratedClassInternalName();

        classWriter.visit(
            V1_6,
            ACC_PUBLIC + ACC_SUPER,
            componentGenerationPlan.getGeneratedClassInternalName(),
            null,
            getInternalName(AbstractComponent.class),
            new String[]{getInternalName(componentClass)}
        );

        {
            classWriter.visitField(ACC_PRIVATE + ACC_STATIC + ACC_FINAL, "ourPropertyNames", getDescriptor(List.class), null, null);
            classWriter.visitField(ACC_PRIVATE + ACC_STATIC + ACC_FINAL, "ourPropertyTypes", getDescriptor(List.class), null, null);
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

        getPropertyNamesMethodWriter.writeGetPropertyNamesMethodFor(componentGenerationPlan);

        getPropertyTypeNamesMethodWriter.writeGetPropertyTypeNamesMethodFor(componentGenerationPlan);

        getMethodWriter.writeGetMethodFor(componentGenerationPlan);

        setMethodWriter.writeSetMethodFor(componentGenerationPlan);

        hasMethodWriter.writeHasMethodFor(componentGenerationPlan);

        typeMethodWriter.writeTypeMethodFor(componentGenerationPlan);

        copyThisMethodWriter.writeCopyThisMethodFor(componentGenerationPlan);

        copyOtherMethodWriter.writeCopyOtherMethodFor(componentGenerationPlan);

        equalsMethodWriter.writeEqualsMethodFor(componentGenerationPlan);

        hashCodeMethodWriter.writeHashCodeMethodFor(componentGenerationPlan);

        toStringMethodWriter.writeToStringMethodFor(componentGenerationPlan);

        FileOutputStream fileOutputStream = null;
        try
        {
            final File classFile = componentGenerationPlan.getGeneratedClassFile();
            final File parentFile = classFile.getParentFile();

            if (!parentFile.exists())
            {
                if (!parentFile.mkdirs())
                {
                    throw new IOException("Failed to create output directory: " + parentFile.getAbsolutePath());
                }
            }

            fileOutputStream = new FileOutputStream(classFile);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            dataOutputStream.write(classWriter.toByteArray());
            dataOutputStream.flush();
        }
        catch (IOException e)
        {
            throw new ComponentFactoryException("Failed to write class file to: " + myOutputDirectory.getAbsolutePath(), e);
        }
        finally
        {
            closeQuietly(fileOutputStream);
        }

        URLClassLoader classLoader = null;
        try
        {
            classLoader = new URLClassLoader(new URL[]{myOutputDirectory.toURI().toURL()}, componentClass.getClassLoader());
            final Class<? extends Component> loadedClass = (Class<? extends Component>) classLoader.loadClass(componentGenerationPlan.getGeneratedClassName());

            myCache.put(componentClass.getName(), new ComponentInstancerImpl<Component, Component>(componentGenerationPlan, loadedClass));

            log.debug("Component prepared: {}", componentClass.getName());
        }
        catch (Exception e)
        {
            throw new ComponentFactoryException("Failed to load class '" + classInternalName + "' from: " + myOutputDirectory.getAbsolutePath(), e);
        }
        finally
        {
            closeQuietly(classLoader);
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
