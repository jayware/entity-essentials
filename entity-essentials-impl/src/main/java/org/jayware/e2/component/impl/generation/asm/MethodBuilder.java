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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isArrayType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBooleanPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isBytePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isDoublePrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isFloatPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isIntegerPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isLongPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isObjectType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.isShortPrimitiveType;
import static org.jayware.e2.component.impl.generation.asm.TypeUtil.resolveOpcodePrimitiveType;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SWAP;
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

    public MethodVisitor custom()
    {
        return myVisitor;
    }

    public void push_0i()
    {
        myVisitor.visitInsn(ICONST_0);
    }

    public void push_1i()
    {
        myVisitor.visitInsn(ICONST_1);
    }

    public void newInstanceOf(Class<?> type)
    {
        myVisitor.visitTypeInsn(NEW, getInternalName(type));
    }

    public void pushNull()
    {
        myVisitor.visitInsn(ACONST_NULL);
    }

    public void loadConstant(String constant)
    {
        myVisitor.visitLdcInsn(constant);
    }

    public void loadConstant(Class constant)
    {
        myVisitor.visitLdcInsn(getType(constant));
    }

    public void loadPrimitiveTypeConstant(Class type)
    {
        if (isBooleanPrimitiveType(type))
        {
            loadStaticField(Boolean.class, "TYPE", Class.class);
        }
        else if (isBytePrimitiveType(type))
        {
            loadStaticField(Byte.class, "TYPE", Class.class);
        }
        else if (isShortPrimitiveType(type))
        {
            loadStaticField(Short.class, "TYPE", Class.class);
        }
        else if (isIntegerPrimitiveType(type))
        {
            loadStaticField(Integer.class, "TYPE", Class.class);
        }
        else if (isLongPrimitiveType(type))
        {
            loadStaticField(Long.class, "TYPE", Class.class);
        }
        else if (isFloatPrimitiveType(type))
        {
            loadStaticField(Float.class, "TYPE", Class.class);
        }
        else if (isDoublePrimitiveType(type))
        {
            loadStaticField(Double.class, "TYPE", Class.class);
        }
        else
        {
            throw new RuntimeException();
        }
    }

    public void loadThis()
    {
        loadReferenceVariable(0);
    }

    public void loadVariable(int index, Class type)
    {
        myVisitor.visitVarInsn(Type.getType(type).getOpcode(ILOAD), index);
    }

    public void storeVariable(int index, Class type)
    {
        myVisitor.visitVarInsn(Type.getType(type).getOpcode(ISTORE), index);
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

    /**
     * Jumps if the current stack element is 0.
     */
    public void jumpIfEquals(Label label)
    {
        myVisitor.visitJumpInsn(IFEQ, label);
    }

    /**
     * Jumps if the current stack element not 0.
     */
    public void jumpIfNotEquals(Label label)
    {
        myVisitor.visitJumpInsn(IFNE, label);
    }

    public void jumpIfNull(Label label)
    {
        myVisitor.visitJumpInsn(IFNULL, label);
    }

    public void jumpIfNotNull(Label label)
    {
        myVisitor.visitJumpInsn(IFNONNULL, label);
    }

    public void jumpTo(Label label)
    {
        myVisitor.visitJumpInsn(GOTO, label);
    }

    public void jumpIfIntIsEqualsOrGreater(Label label)
    {
        myVisitor.visitJumpInsn(IF_ICMPGE, label);
    }

    public void label(Label label)
    {
        myVisitor.visitLabel(label);
    }

    public void castTo(Class<?> type)
    {
        myVisitor.visitTypeInsn(CHECKCAST, getInternalName(type));
    }

    public void swap()
    {
        myVisitor.visitInsn(SWAP);
    }

    public void incrementInt(int index, int increment)
    {
        myVisitor.visitIincInsn(index, increment);
    }

    public void arrayLength()
    {
        myVisitor.visitInsn(ARRAYLENGTH);
    }

    public void newPrimitiveArray(Class<?> type)
    {
        myVisitor.visitIntInsn(NEWARRAY, resolveOpcodePrimitiveType(type));
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

    public void push_0(Class<?> type)
    {
        if (isBooleanPrimitiveType(type) || isBytePrimitiveType(type) || isShortPrimitiveType(type) || isIntegerPrimitiveType(type))
        {
            myVisitor.visitInsn(ICONST_0);
        }
        else if (isLongPrimitiveType(type))
        {
            myVisitor.visitInsn(LCONST_0);
        }
        else if (isFloatPrimitiveType(type))
        {
            myVisitor.visitInsn(FCONST_0);
        }
        else if (isDoublePrimitiveType(type))
        {
            myVisitor.visitInsn(DCONST_0);
        }
        else if (isObjectType(type) || isArrayType(type))
        {
            myVisitor.visitInsn(ACONST_NULL);
        }
        else
        {
            throw new RuntimeException();
        }
    }
}