/*
 * Copyright 2022 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.common.inject.service;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ServiceClassLoader extends ClassLoader {

    public Class<?> defineServiceClass(String name, Type... argumentTypes) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            "io/github/lxgaming/common/inject/service/dynamic/" + name,
            null,
            "io/github/lxgaming/common/inject/service/BaseService",
            null
        );
        MethodVisitor methodVisitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC,
            "<init>",
            Type.getMethodDescriptor(Type.VOID_TYPE, argumentTypes),
            null,
            null
        );
        methodVisitor.visitVarInsn(
            Opcodes.ALOAD,
            0
        );
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "io/github/lxgaming/common/inject/service/BaseService",
            "<init>",
            "()V",
            false
        );
        methodVisitor.visitInsn(
            Opcodes.RETURN
        );
        methodVisitor.visitMaxs(-1, -1);
        methodVisitor.visitEnd();
        classWriter.visitEnd();

        byte[] bytes = classWriter.toByteArray();
        return defineClass(null, bytes, 0, bytes.length);
    }
}