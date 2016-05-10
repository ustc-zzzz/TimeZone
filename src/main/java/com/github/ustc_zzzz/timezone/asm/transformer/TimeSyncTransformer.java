package com.github.ustc_zzzz.timezone.asm.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.ustc_zzzz.timezone.TimeZone;

public class TimeSyncTransformer implements IClassTransformer
{
    public static class S03PacketTimeUpdateVisitor extends ClassVisitor
    {
        private boolean hasDoDayLightCycle = false;

        private final String className;

        public S03PacketTimeUpdateVisitor(String className, ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
            this.className = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc);
            if ("<init>".equals(methodName) && "(JJZ)V".equals(desc))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitVarInsn(int opcode, int var)
                    {
                        if (opcode == Opcodes.ILOAD && var == 5)
                        {
                            this.visitVarInsn(Opcodes.ALOAD, 0);
                            super.visitVarInsn(opcode, var);
                            this.visitFieldInsn(Opcodes.PUTFIELD,
                                    "net/minecraft/network/play/server/S03PacketTimeUpdate", "doDayLightCycle", "Z");
                            this.visitInsn(Opcodes.RETURN);
                            TimeZone.LOGGER.info("- constructor ");
                            return;
                        }
                        if (opcode == Opcodes.LLOAD && var == 3)
                        {
                            super.visitVarInsn(opcode, var);
                            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", "setSyncWorldTimeDelegate",
                                    "(J)J", false);
                            TimeZone.LOGGER.info("- constructor ");
                            return;
                        }
                        super.visitVarInsn(opcode, var);
                    }
                };
            }
            if ("getWorldTime".equals(methodName) || "func_149365_d".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitInsn(int opcode)
                    {
                        if (opcode == Opcodes.LRETURN)
                        {
                            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", "getSyncWorldTimeDelegate",
                                    "(J)J", false);
                            TimeZone.LOGGER.info("- method '" + methodName + "' ");
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
            if ("readPacketData".equals(methodName) || "func_148837_a".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitInsn(int opcode)
                    {
                        if (opcode == Opcodes.RETURN)
                        {
                            this.visitVarInsn(Opcodes.ALOAD, 0);
                            this.visitVarInsn(Opcodes.ALOAD, 1);
                            this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer",
                                    "readBoolean", "()Z", false);
                            this.visitFieldInsn(Opcodes.PUTFIELD,
                                    "net/minecraft/network/play/server/S03PacketTimeUpdate", "doDayLightCycle", "Z");
                            TimeZone.LOGGER.info("- method '" + methodName + "' ");
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
            if ("writePacketData".equals(methodName) || "func_148840_b".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitInsn(int opcode)
                    {
                        if (opcode == Opcodes.RETURN)
                        {
                            this.visitVarInsn(Opcodes.ALOAD, 1);
                            this.visitVarInsn(Opcodes.ALOAD, 0);
                            this.visitFieldInsn(Opcodes.GETFIELD,
                                    "net/minecraft/network/play/server/S03PacketTimeUpdate", "doDayLightCycle", "Z");
                            this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer",
                                    "writeBoolean", "(Z)Lio/netty/buffer/ByteBuf;", false);
                            this.visitInsn(Opcodes.POP);
                            TimeZone.LOGGER.info("- method '" + methodName + "' ");
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
            return mv;
        }

        @Override
        public void visitEnd()
        {
            if (!hasDoDayLightCycle)
            {
                FieldVisitor fv = super.visitField(Opcodes.ACC_PUBLIC, "doDayLightCycle", "Z", null, null);
                if (fv != null)
                {
                    fv.visitEnd();
                }
                hasDoDayLightCycle = true;
                TimeZone.LOGGER.info("- field 'doDayLightCycle' ");
            }
            super.visitEnd();
        }
    }

    public static class WorldClientVisitor extends ClassVisitor
    {
        private final String className;

        public WorldClientVisitor(String className, ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
            this.className = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc);
            if ("setWorldTime".equals(methodName) || "func_72877_b".equals(methodName))
            {
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.LLOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/world/World", methodName, "(J)V", false);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
                TimeZone.LOGGER.info("- method '" + methodName + "' ");
                return null;
            }
            return mv;
        }
    }

    public static class NetHandlerPlayClientVisitor extends ClassVisitor
    {
        private final String className;

        public NetHandlerPlayClientVisitor(String className, ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
            this.className = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc);
            if ("handleTimeUpdate".equals(methodName) || "func_147285_a".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitInsn(int opcode)
                    {
                        if (opcode == Opcodes.RETURN)
                        {
                            this.visitVarInsn(Opcodes.ALOAD, 0);
                            this.visitVarInsn(Opcodes.ALOAD, 1);
                            this.visitFieldInsn(Opcodes.GETFIELD,
                                    "net/minecraft/network/play/server/S03PacketTimeUpdate", "doDayLightCycle", "Z");
                            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", "handleTimeUpdateDelegate",
                                    "(Z)V", false);
                            TimeZone.LOGGER.info("- method '" + methodName + "' ");
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
            return mv;
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if ("net.minecraft.network.play.server.S03PacketTimeUpdate".equals(transformedName))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'S03PacketTimeUpdate'. ");

            classReader.accept(new S03PacketTimeUpdateVisitor(transformedName, classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        if ("net.minecraft.client.multiplayer.WorldClient".equals(transformedName))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'WorldClient'. ");

            classReader.accept(new WorldClientVisitor(transformedName, classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        if ("net.minecraft.client.network.NetHandlerPlayClient".equals(transformedName))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'NetHandlerPlayClient'. ");

            classReader
                    .accept(new NetHandlerPlayClientVisitor(transformedName, classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
}
