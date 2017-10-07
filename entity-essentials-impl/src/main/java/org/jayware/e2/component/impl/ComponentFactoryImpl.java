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
package org.jayware.e2.component.impl;


import org.jayware.e2.component.api.AbstractComponent;
import org.jayware.e2.component.api.Component;
import org.jayware.e2.component.api.ComponentFactory;
import org.jayware.e2.component.api.ComponentFactoryException;
import org.jayware.e2.component.api.ComponentInstancer;
import org.jayware.e2.component.api.MalformedComponentException;
import org.jayware.e2.component.api.generation.analyse.ComponentAnalyser;
import org.jayware.e2.component.api.generation.analyse.ComponentDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor;
import org.jayware.e2.component.api.generation.analyse.ComponentPropertyDescriptor;
import org.jayware.e2.component.impl.generation.analyse.ComponentAnalyserImpl;
import org.jayware.e2.component.impl.generation.analyse.DefaultComponentAnalyserFactory;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Class.forName;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.READ;
import static org.jayware.e2.component.api.generation.analyse.ComponentPropertyAccessorDescriptor.AccessorType.WRITE;
import static org.jayware.e2.util.IOUtil.closeQuietly;
import static org.jayware.e2.util.IOUtil.writeBytes;
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
    private final ComponentAnalyser myComponentAnalyser = new ComponentAnalyserImpl(new DefaultComponentAnalyserFactory(null));

    private final File myOutputDirectory;

    private final Map<String, ComponentInstancer<?>> myCache;

    private boolean forceClassGeneration = true;

    private final Object myLock = new Object();

    private final Logger log = LoggerFactory.getLogger(ComponentFactoryImpl.class);

    public ComponentFactoryImpl()
    {
        try
        {
            myOutputDirectory = new File(System.getProperty("user.dir") + "/.generated");

            if (!myOutputDirectory.exists() && !myOutputDirectory.mkdirs())
            {
                throw new IOException("Failed to create output directory: " + myOutputDirectory.getAbsolutePath());
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
    {
        try
        {
            if (!isComponentPrepared(componentClass) || forceClassGeneration)
            {
                prepareComponent(componentClass);
            }

            if (componentClasses != null)
            {
                for (Class<? extends Component> clazz : componentClasses)
                {
                    if (!isComponentPrepared(clazz) || forceClassGeneration)
                    {
                        prepareComponent(clazz);
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
    throws ClassNotFoundException
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
                generateComponentClass(myComponentAnalyser.analyse(componentClass));
            }
        }
    }

    private void generateComponentClass(ComponentDescriptor descriptor)
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

        final Class<? extends Component> componentClass = descriptor.getDeclaringComponent();
        final ComponentGenerationContext generationContext = new ComponentGenerationContext(descriptor, myOutputDirectory);

        final ClassWriter classWriter = generationContext.getClassWriter();

        classWriter.visit(
            V1_6,
            ACC_PUBLIC + ACC_SUPER,
            generationContext.getGeneratedClassInternalName(),
            null,
            getInternalName(AbstractComponent.class),
            new String[]{getInternalName(componentClass)}
        );

        classWriter.visitField(ACC_PRIVATE + ACC_STATIC + ACC_FINAL, "ourPropertyNames", getDescriptor(List.class), null, null);
        classWriter.visitField(ACC_PRIVATE + ACC_STATIC + ACC_FINAL, "ourPropertyTypes", getDescriptor(List.class), null, null);

        for (ComponentPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
        {
            propertyFieldWriter.writePropertyFieldFor(generationContext, propertyDescriptor);
        }

        staticInitializerWriter.writeStaticInitializerFor(generationContext, descriptor);

        defaultConstructorWriter.writeDefaultConstructorFor(generationContext);
        copyConstructorWriter.writeCopyConstructorFor(generationContext);

        for (ComponentPropertyDescriptor propertyDescriptor : descriptor.getPropertyDescriptors())
        {
            for (ComponentPropertyAccessorDescriptor accessorDescriptor : descriptor.getPropertyAccessorDescriptors(propertyDescriptor.getPropertyName()))
            {
                if (accessorDescriptor.getAccessorType() == READ)
                {
                    propertyGetterWriter.writePropertyGetterFor(generationContext, accessorDescriptor);
                }
                else if (accessorDescriptor.getAccessorType() == WRITE)
                {
                    propertySetterWriter.writePropertySetterFor(generationContext, accessorDescriptor);
                }
                else
                {
                    throw new UnsupportedOperationException();
                }
            }
        }

        getPropertyNamesMethodWriter.writeGetPropertyNamesMethodFor(generationContext);

        getPropertyTypeNamesMethodWriter.writeGetPropertyTypeNamesMethodFor(generationContext);

        getMethodWriter.writeGetMethodFor(generationContext, descriptor);

        setMethodWriter.writeSetMethodFor(generationContext, descriptor);

        hasMethodWriter.writeHasMethodFor(generationContext);

        typeMethodWriter.writeTypeMethodFor(generationContext, descriptor);

        copyThisMethodWriter.writeCopyThisMethodFor(generationContext);

        copyOtherMethodWriter.writeCopyOtherMethodFor(generationContext, descriptor);

        equalsMethodWriter.writeEqualsMethodFor(generationContext, descriptor);

        hashCodeMethodWriter.writeHashCodeMethodFor(generationContext, descriptor);

        toStringMethodWriter.writeToStringMethodFor(generationContext, descriptor);

        try
        {
            writeBytes(generationContext.getGeneratedClassFile(), classWriter.toByteArray());
        }
        catch (IOException e)
        {
            throw new ComponentFactoryException("Failed to write class file to: " + myOutputDirectory.getAbsolutePath(), e);
        }

        URLClassLoader classLoader = null;
        try
        {
            classLoader = new URLClassLoader(new URL[]{myOutputDirectory.toURI().toURL()}, componentClass.getClassLoader());
            final Class<? extends Component> loadedClass = (Class<? extends Component>) classLoader.loadClass(generationContext.getGeneratedClassName());

            myCache.put(componentClass.getName(), new ComponentInstancerImpl<Component, Component>(loadedClass));

            log.debug("Component prepared: {}", componentClass.getName());
        }
        catch (Exception e)
        {
            throw new ComponentFactoryException("Failed to load class '" + generationContext.getGeneratedClassInternalName() + "' from: " + myOutputDirectory.getAbsolutePath(), e);
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

    public static class ComponentGenerationContext
    {
        private final ClassWriter myClassWriter;
        private final File myOutputDirectory;
        private final File myGeneratedClassFile;
        private final String myGeneratedClassName;
        private final String myGeneratedClassPackageName;

        public ComponentGenerationContext(ComponentDescriptor descriptor, File outputDirectory)
        {
            final Class<? extends Component> declaringComponent = descriptor.getDeclaringComponent();

            myClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            myOutputDirectory = outputDirectory;
            myGeneratedClassPackageName = declaringComponent.getPackage().getName();
            myGeneratedClassName = "_generated_" + declaringComponent.getSimpleName();
            myGeneratedClassFile = new File(myOutputDirectory, getGeneratedClassInternalName() + ".class");
        }

        public ClassWriter getClassWriter()
        {
            return myClassWriter;
        }

        public File getGeneratedClassFile()
        {
            return myGeneratedClassFile;
        }

        public String getGeneratedClassName()
        {
            return getGeneratedClassPackageName() + "." + myGeneratedClassName;
        }

        public String getGeneratedClassInternalName()
        {
            return getGeneratedClassPackagePath() + myGeneratedClassName;
        }

        public String getGeneratedClassPackagePath()
        {
            return getGeneratedClassPackageName().replace(".", "/") + "/";
        }

        public String getGeneratedClassPackageName()
        {
            return myGeneratedClassPackageName;
        }
    }
}
