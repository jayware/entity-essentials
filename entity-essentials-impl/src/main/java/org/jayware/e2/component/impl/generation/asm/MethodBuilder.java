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
package org.jayware.e2.component.impl.generation.asm;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Type.VOID_TYPE;
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;
import static org.objectweb.asm.Type.getType;


public class MethodBuilder
{
    private final MethodVisitor myVisitor;

    private MethodBuilder(MethodVisitor visitor)
    {
        myVisitor = visitor;
    }

    public static MethodBuilder createMethodBuilder(ClassWriter classWriter, int acc, String name, String descriptor)
    {
        return createMethodBuilder(classWriter, acc, name, descriptor, null, null);
    }

    public static MethodBuilder createMethodBuilder(ClassWriter classWriter, int acc, String name, String descriptor, String signature, String[] exceptions)
    {
        return new MethodBuilder(classWriter.visitMethod(acc, name, descriptor, signature, exceptions));
    }

    public void beginMethod()
    {
        myVisitor.visitCode();
    }

    public void newInstanceOf(Class<?> type)
    {
        myVisitor.visitTypeInsn(NEW, getInternalName(type));
    }

    public void loadConstant(String constant)
    {
        myVisitor.visitLdcInsn(constant);
    }

    public void loadThis()
    {
        loadReferenceVariable(0);
    }

    public void loadVariable(int index, Class type)
    {
        myVisitor.visitVarInsn(Type.getType(type).getOpcode(ILOAD), index);
    }

    public void loadReferenceVariable(int index)
    {
        myVisitor.visitVarInsn(ALOAD, index);
    }

    public void storeReferenceVariable(int index)
    {
        myVisitor.visitVarInsn(ASTORE, index);
    }

    public void loadField(Class<?> owner, String name, Class<?> type)
    {
        loadField(getInternalName(owner), name, type);
    }

    public void loadField(String owner, String name, Class<?> type)
    {
        myVisitor.visitFieldInsn(GETFIELD, owner, name, getDescriptor(type));
    }

    public void storeField(Class<?> owner, String name, Class<?> type)
    {
        storeField(getInternalName(owner), name, type);
    }

    public void storeField(String owner, String name, Class<?> type)
    {
        myVisitor.visitFieldInsn(PUTFIELD, owner, name, getDescriptor(type));
    }

    public void loadStaticField(Class<?> owner, String name, Class<?> type)
    {
        loadStaticField(getInternalName(owner), name, type);
    }

    public void loadStaticField(String owner, String name, Class<?> type)
    {
        myVisitor.visitFieldInsn(GETSTATIC, owner, name, getDescriptor(type));
    }

    public void storeStaticField(Class<?> owner, String name, Class<?> type)
    {
        storeStaticField(getInternalName(owner), name, type);
    }

    public void storeStaticField(String owner, String name, Class<?> type)
    {
        myVisitor.visitFieldInsn(PUTSTATIC, owner, name, getDescriptor(type));
    }

    public void invokeConstructor(Class<?> owner, Class<?>... parametersTypes)
    {
        myVisitor.visitMethodInsn(INVOKESPECIAL, getInternalName(owner), "<init>", getVoidMethodDescriptor(parametersTypes), false);
    }

    public void invokeInterfaceMethod(Class<?> owner, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        invokeInterfaceMethod(getInternalName(owner), name, returnType, parameterTypes);
    }

    public void invokeInterfaceMethod(String owner, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        myVisitor.visitMethodInsn(INVOKEINTERFACE, owner, name, getMethodDescriptor(returnType, parameterTypes), true);
    }

    public void invokeVirtualMethod(Class<?> owner, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        invokeVirtualMethod(getInternalName(owner), name, returnType, parameterTypes);
    }

    public void invokeVirtualMethod(String owner, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        myVisitor.visitMethodInsn(INVOKEVIRTUAL, owner, name, getMethodDescriptor(returnType, parameterTypes), false);
    }

    public void invokeStaticMethod(Class<?> owner, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        invokeStaticMethod(getInternalName(owner), name, returnType, parameterTypes);
    }

    public void invokeStaticMethod(String owner, String name, Class<?> returnType, Class<?>... parameterTypes)
    {
        myVisitor.visitMethodInsn(INVOKESTATIC, owner, name, getMethodDescriptor(returnType, parameterTypes), false);
    }

    public void returnReference()
    {
        myVisitor.visitInsn(ARETURN);
    }

    public void returnValue(Class<?> type)
    {
        myVisitor.visitInsn(getType(type).getOpcode(IRETURN));
    }

    public void returnVoid()
    {
        myVisitor.visitInsn(RETURN);
    }

    public void endMethod()
    {
        myVisitor.visitMaxs(0, 0);
        myVisitor.visitEnd();
    }

    public void duplicateTopStackElement()
    {
        myVisitor.visitInsn(DUP);
    }

    public void throwException()
    {
        myVisitor.visitInsn(ATHROW);
    }

    private static String getMethodDescriptor(Class<?> returnType, Class<?>... parameterTypes)
    {
        return Type.getMethodDescriptor(getType(returnType), convertClassesToTypes(parameterTypes));
    }

    private static String getVoidMethodDescriptor(Class<?>... parameterTypes)
    {
        return Type.getMethodDescriptor(VOID_TYPE, convertClassesToTypes(parameterTypes));
    }

    private static Type[] convertClassesToTypes(Class<?>... classes)
    {
        if (classes == null)
        {
            return new Type[0];
        }

        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; ++i)
        {
            types[i] = getType(classes[i]);
        }

        return types;
    }
}