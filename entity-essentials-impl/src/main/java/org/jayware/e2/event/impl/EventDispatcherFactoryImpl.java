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
package org.jayware.e2.event.impl;


import org.jayware.e2.event.api.Event;
import org.jayware.e2.event.api.EventDispatcher;
import org.jayware.e2.event.api.EventDispatcherFactory;
import org.jayware.e2.event.api.EventType;
import org.jayware.e2.event.api.Handle;
import org.jayware.e2.event.api.IllegalHandlerException;
import org.jayware.e2.event.api.Param;
import org.jayware.e2.event.api.Presence;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static org.jayware.e2.util.ConfigurationUtil.getPropertyOrDefault;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;
import static org.objectweb.asm.Type.getType;


public class EventDispatcherFactoryImpl
implements EventDispatcherFactory
{
    private static final String EVENT_DISPATCHER_NAME_PREFIX = "org.jayware.e2.event.generated.EventDispatcher_";
    private static final Logger log = LoggerFactory.getLogger(EventDispatcherFactoryImpl.class);

    private File myOutputDirectory;
    private MessageDigest myMessageDigest;

    private final Map<Class<?>, TargetDescriptor> myTargetDescriptionMap;

    public EventDispatcherFactoryImpl()
    {
        this(null);
    }

    public EventDispatcherFactoryImpl(Dictionary<String, ?> properties)
    {
        myOutputDirectory = new File(getPropertyOrDefault(properties, PROPERTY_OUT_DIRECTORY, System.getProperty("user.dir") + "/.generated"));
        myTargetDescriptionMap = new HashMap<>();

        try
        {
            myMessageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    public EventDispatcher createEventDispatcher(Class<?> target)
    {
        TargetDescriptor targetDescriptor = myTargetDescriptionMap.get(target);

        if (targetDescriptor == null)
        {
            log.debug("Creating EventDispatcher for: {}", target);

            try
            {
                targetDescriptor = createTargetDescription(target);
            }
            catch (Exception e)
            {
                log.error("Failed to create EventDispatcher:", e);
                throw e;
            }

            myTargetDescriptionMap.put(target, targetDescriptor);
        }

        return targetDescriptor.eventDispatcher;
    }

    private TargetDescriptor createTargetDescription(Class<?> target)
    {
        if (!Modifier.isPublic(target.getModifiers()))
        {
            throw new IllegalHandlerException("The class '" + target.getName() + "' is appointed as event handler, " +
            "but is declared 'private' and therefore not accessible by any event dispatcher!");
        }

        final TargetDescriptor targetDescriptor = new TargetDescriptor(target);

        createHandlerDescriptions(targetDescriptor);
        createEventDispatcher(targetDescriptor);

        return targetDescriptor;
    }

    private void createHandlerDescriptions(TargetDescriptor targetDescriptor)
    {
        for (Method method : targetDescriptor.target.getDeclaredMethods())
        {
            final Handle handleAnnotation = method.getAnnotation(Handle.class);

            if (handleAnnotation != null)
            {
                if (Modifier.isPrivate(method.getModifiers()))
                {
                    throw new IllegalHandlerException("Method '" + method + "' is appointed as event handler but is " +
                    "declared 'private' and therefore not accessible by any event dispatcher!");
                }

                for (Parameter parameter : method.getParameters())
                {
                    final Param paramAnnotation = parameter.getAnnotation(Param.class);

                    if (!Event.class.isAssignableFrom(parameter.getType()))
                    {
                        if (paramAnnotation == null)
                        {
                            throw new IllegalHandlerException("Method '" + method + "' is appointed as event handler " +
                            "but exhibits an parameter which is not annotated with @Param!");
                        }
                    }
                }

                final HandlerDescriptor handlerDescriptor = new HandlerDescriptor(method, handleAnnotation);
                targetDescriptor.addHandlerDescriptor(handlerDescriptor);

                log.debug("Event handler found: '{}'. Event handler accepts: {}", method, handlerDescriptor.getEventTypes());
            }
        }

        if (targetDescriptor.handlerDescriptors.isEmpty())
        {
            throw new IllegalHandlerException("The class '" + targetDescriptor.target.getName() + "' is appointed as " +
            "event handler, but does not have any method annotated with @Handle!");
        }
    }

    private void createEventDispatcher(TargetDescriptor targetDescriptor)
    {
        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        final Class<?> target = targetDescriptor.target;
        final String className = createDispatcherName(target);
        final String classInternalName = className.replace('.', '/');
        final String classFileName = classInternalName + ".class";
        final File classFile = new File(myOutputDirectory, classFileName);

        classWriter.visit(
        V1_8,
        ACC_PUBLIC + ACC_SUPER,
        classInternalName,
        null,
        getInternalName(Object.class),
        new String[]{getInternalName(EventDispatcher.class)}
        );

        {
            final FieldVisitor fv = classWriter.visitField(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, "log", getDescriptor(Logger.class), null, null);
        }

        {   // <static>
            final MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            mv.visitLdcInsn(getType(EventDispatcher.class));
            mv.visitMethodInsn(INVOKESTATIC, getInternalName(LoggerFactory.class), "getLogger", "(Ljava/lang/Class;)" + getDescriptor(Logger.class), false);
            mv.visitFieldInsn(PUTSTATIC, classInternalName, "log", getDescriptor(Logger.class));
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        {   // Constructor
            final MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, getInternalName(Object.class), "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        {   // dispatch
            final MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "dispatch", "(" + getDescriptor(Event.class) + getDescriptor(Object.class) + ")V", null, null);
            final Label endMethod = new Label();

            mv.visitCode();

            // Is this a suitable dispatcher for the passed target? Sub-classes are
            // not allowed, therefore check the passed target's class for equality.
            mv.visitLdcInsn(targetDescriptor.getTargetType());
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, getInternalName(Object.class), "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, getInternalName(Class.class), "equals", "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(IFEQ, endMethod);

            // Yeah...the right target...let's cast!
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, targetDescriptor.getTargetInternalName());
            mv.visitVarInsn(ASTORE, 3);

            for (Class<? extends EventType> eventType : targetDescriptor.getEventTypeSet())
            {
                final Label endEvenType = new Label();

                // Does the event match an event-type one of the handlers is interested?
                mv.visitVarInsn(ALOAD, 1);
                mv.visitLdcInsn(getType(eventType));
                mv.visitMethodInsn(INVOKEINTERFACE, getInternalName(Event.class), "matches", "(Ljava/lang/Class;)Z", true);
                mv.visitJumpInsn(IFEQ, endEvenType);

                for (HandlerDescriptor descriptor : targetDescriptor.eventTypeHandlerDescriptorMap.get(eventType))
                {
                    final Label endHandler = new Label();

                    // Required and conditional parameters have to be present
                    for (HandlerDescriptor.ParameterDescriptor parameter : descriptor.getParameters())
                    {
                        if (!parameter.isEventParameter())
                        {
                            if (parameter.presence == Presence.Required || parameter.presence == Presence.Conditional)
                            {
                                final Label parameterNotPresent = new Label();
                                mv.visitVarInsn(ALOAD, 1);
                                mv.visitLdcInsn(parameter.getName());
                                mv.visitMethodInsn(INVOKEINTERFACE, getInternalName(Event.class), "hasParameter", "(Ljava/lang/String;)Z", true);
                                mv.visitJumpInsn(IFNE, parameterNotPresent);
                                mv.visitFieldInsn(GETSTATIC, classInternalName, "log", getDescriptor(Logger.class));
                                mv.visitLdcInsn("Could not dispatch '" + eventType.getName() + "' to " + descriptor.getMethod() + "', because the event does not provide the " + parameter.getPresence().toString().toLowerCase() + " parameter '" + parameter.getName() + "'!");
                                mv.visitMethodInsn(INVOKEINTERFACE, getInternalName(Logger.class), "warn", "(Ljava/lang/String;)V", true);
                                mv.visitJumpInsn(GOTO, endHandler);
                                mv.visitLabel(parameterNotPresent);
                            }
                        }
                    }

                    final int startIndex = 4;
                    int index = startIndex;
                    for (HandlerDescriptor.ParameterDescriptor parameter : descriptor.getParameters())
                    {
                        if (!parameter.isEventParameter())
                        {
                            final Label endInstanceOf = new Label();

                            // Extract the parameter from the event and store it to a variable.
                            mv.visitVarInsn(ALOAD, 1);
                            mv.visitLdcInsn(parameter.getName());
                            mv.visitMethodInsn(INVOKEINTERFACE, getInternalName(Event.class), "getParameter", "(Ljava/lang/String;)Ljava/lang/Object;", true);
                            mv.visitVarInsn(ASTORE, index);

                            // A required parameter mustn't be null ...
                            if (parameter.presence == Presence.Required)
                            {
                                final Label notNull = new Label();
                                mv.visitVarInsn(ALOAD, index);
                                mv.visitJumpInsn(IFNONNULL, notNull);
                                mv.visitFieldInsn(GETSTATIC, classInternalName, "log", getDescriptor(Logger.class));
                                mv.visitLdcInsn("Could not dispatch '" + eventType.getName() + "' to " + descriptor.getMethod() + "', because the " + Presence.Required.toString().toLowerCase() + " parameter '" + parameter.getName() + "' was null!");
                                mv.visitMethodInsn(INVOKEINTERFACE, getInternalName(Logger.class), "warn", "(Ljava/lang/String;)V", true);
                                mv.visitJumpInsn(GOTO, endHandler);
                                mv.visitLabel(notNull);
                            }
                            else
                            // ...but instanceof-check is bad if the parameter is null
                            // which is legal for conditional and optional parameters.
                            {
                                mv.visitVarInsn(ALOAD, index);
                                mv.visitJumpInsn(IFNULL, endInstanceOf);
                            }

                            // Check the type of the parameter (only if it is not null)
                            // whether it matches the declared parameter type of the handler.
                            final Label notInstanceOf = new Label();
                            mv.visitVarInsn(ALOAD, index);
                            mv.visitTypeInsn(INSTANCEOF, parameter.getParameterInternalName());
                            mv.visitJumpInsn(IFNE, notInstanceOf);
                            mv.visitFieldInsn(GETSTATIC, classInternalName, "log", getDescriptor(Logger.class));
                            mv.visitLdcInsn("Could not dispatch '" + eventType.getName() + "' to " + descriptor.getMethod() + "', because the parameter '" + parameter.getName() + "' cannot be cast to the appropriate type '" + parameter.getParameterClass().getName() + "'!");
                            mv.visitMethodInsn(INVOKEINTERFACE, getInternalName(Logger.class), "warn", "(Ljava/lang/String;)V", true);
                            mv.visitJumpInsn(GOTO, endHandler);
                            mv.visitLabel(notInstanceOf);
                            mv.visitLabel(endInstanceOf);

                            ++index;
                        }
                    }

                    mv.visitVarInsn(ALOAD, 3);

                    // Load and cast all necessary parameters.
                    index = startIndex;
                    for (HandlerDescriptor.ParameterDescriptor parameter : descriptor.getParameters())
                    {
                        if (parameter.isEventParameter())
                        {
                            mv.visitVarInsn(ALOAD, 1);
                        }
                        else
                        {
                            mv.visitVarInsn(ALOAD, index++);
                            mv.visitTypeInsn(CHECKCAST, parameter.getParameterInternalName());
                        }
                    }

                    // At last call the handler.
                    mv.visitMethodInsn(INVOKEVIRTUAL, targetDescriptor.getTargetInternalName(), descriptor.getMethodName(), descriptor.getMethodDescriptor(), false);

                    mv.visitLabel(endHandler);
                }

                mv.visitLabel(endEvenType);
            }

            mv.visitLabel(endMethod);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        {   // accepts
            final MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "accepts", "(Ljava/lang/Class;)Z", null, null);
            mv.visitCode();
            for (Class<?> eventType : targetDescriptor.getEventTypeSet())
            {
                final Label endIf = new Label();
                mv.visitLdcInsn(getType(eventType));
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, getInternalName(Class.class), "isAssignableFrom", "(Ljava/lang/Class;)Z", false);
                mv.visitJumpInsn(IFEQ, endIf);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);
                mv.visitLabel(endIf);
            }
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        try
        {
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
            log.error("Saving dispatcher class failed!", e);
        }

        try
        {
            Class<? extends EventDispatcher> eventDispatcherClass = (Class<? extends EventDispatcher>) new URLClassLoader(new URL[]{myOutputDirectory.toURI().toURL()}, getClass().getClassLoader()).loadClass(className);
            targetDescriptor.eventDispatcher = eventDispatcherClass.newInstance();

            log.info("Created EventDispatcher for: '{}'. EventDispatcher class stored in: '{}' ", target, classFile.getAbsolutePath());
        }
        catch (ClassNotFoundException | MalformedURLException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private String createDispatcherName(Class target)
    {
        String name = EVENT_DISPATCHER_NAME_PREFIX + target.getSimpleName();
        name += "_" + printHexBinary(myMessageDigest.digest(target.getName().getBytes())).substring(0, 8);

        return name;
    }

    private static class TargetDescriptor
    {
        private final Class<?> target;
        private final List<HandlerDescriptor> handlerDescriptors = new ArrayList<>();
        private final Map<Class<? extends EventType>, List<HandlerDescriptor>> eventTypeHandlerDescriptorMap = new HashMap<>();
        private EventDispatcher eventDispatcher;

        private TargetDescriptor(Class<?> target)
        {
            this.target = target;
        }

        public void addHandlerDescriptor(HandlerDescriptor descriptor)
        {
            for (Class<? extends EventType> type : descriptor.getEventTypes())
            {
                List<HandlerDescriptor> descriptorList = eventTypeHandlerDescriptorMap.get(type);
                if (descriptorList == null)
                {
                    descriptorList = new ArrayList<>();
                    eventTypeHandlerDescriptorMap.put(type, descriptorList);
                }

                if (!descriptorList.contains(descriptor))
                {
                    descriptorList.add(descriptor);
                }
                else
                {
                    log.warn("Event handler '{}' is annotated with the same EventType ({}) multiple times. " +
                    "The event dispatcher doesn't cate about, but this may be a flaw.", descriptor.handlerMethod, type.getName());
                }
            }

            handlerDescriptors.add(descriptor);
        }

        public Class getTargetClass()
        {
            return target;
        }

        public Type getTargetType()
        {
            return getType(target);
        }

        public String getTargetInternalName()
        {
            return getInternalName(target);
        }

        public Set<Class<? extends EventType>> getEventTypeSet()
        {
            return eventTypeHandlerDescriptorMap.keySet();
        }
    }

    private static class HandlerDescriptor
    {
        private final Method handlerMethod;
        private final Handle handleAnnotation;
        private final Set<Class<? extends EventType>> eventTypes;
        private final List<ParameterDescriptor> parameters = new ArrayList<>();

        public HandlerDescriptor(Method handlerMethod, Handle handleAnnotation)
        {
            this.handlerMethod = handlerMethod;
            this.handleAnnotation = handleAnnotation;
            this.eventTypes = new HashSet<>(asList(handleAnnotation.value()));
            for (Parameter parameter : handlerMethod.getParameters())
            {
                parameters.add(new ParameterDescriptor(parameter));
            }
        }

        public Method getMethod()
        {
            return handlerMethod;
        }

        public String getMethodName()
        {
            return handlerMethod.getName();
        }

        public String getMethodDescriptor()
        {
            return Type.getMethodDescriptor(handlerMethod);
        }

        public Set<Class<? extends EventType>> getEventTypes()
        {
            return eventTypes;
        }

        public List<ParameterDescriptor> getParameters()
        {
            return parameters;
        }

        private class ParameterDescriptor
        {
            private final Parameter parameter;
            private final String name;
            private final Presence presence;

            private final boolean isEventParameter;

            private ParameterDescriptor(Parameter parameter)
            {
                this.parameter = parameter;
                final Param annotation = parameter.getAnnotation(Param.class);

                if (annotation != null)
                {
                    isEventParameter = false;
                    this.name = annotation.value();
                    this.presence = annotation.presence();
                }
                else
                {
                    isEventParameter = true;
                    name = null;
                    presence = null;
                }
            }

            public Parameter getParameter()
            {
                return parameter;
            }

            public Class getParameterClass()
            {
                return parameter.getType();
            }

            public String getParameterInternalName()
            {
                return getInternalName(getParameterClass());
            }

            public Type getParameterType()
            {
                return getType(getParameterClass());
            }

            public int getParameterOpcode(int opcode)
            {
                return getParameterType().getOpcode(opcode);
            }

            public String getName()
            {
                return name;
            }

            public Presence getPresence()
            {
                return presence;
            }

            public boolean isEventParameter()
            {
                return isEventParameter;
            }
        }
    }
}