package com.github.ustc_zzzz.timezone.asm.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.ustc_zzzz.timezone.TimeZone;

import net.minecraft.launchwrapper.IClassTransformer;

public class TimeSyncTransformer implements IClassTransformer
{
    public static class S03PacketTimeUpdateVisitor extends ClassVisitor
    {
        private boolean hasDoDayLightCycle = false;

        public S03PacketTimeUpdateVisitor(ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if ("<init>".equals(name) && "(JJZ)V".equals(desc))
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
            if ("getWorldTime".equals(name) || "func_149365_d".equals(desc))
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
                            TimeZone.LOGGER.info("- method 'getWorldTime' ");
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
            if ("readPacketData".equals(name) || "func_148837_a".equals(desc))
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
                            TimeZone.LOGGER.info("- method 'readPacketData' ");
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
            if ("writePacketData".equals(name) || "func_148840_b".equals(desc))
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
                            TimeZone.LOGGER.info("- method 'writePacketData' ");
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
        public WorldClientVisitor(ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if ("setWorldTime".equals(name) || "func_72877_b".equals(name))
            {
                final String methodName = name;
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.LLOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/world/World", methodName, "(J)V", false);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
                TimeZone.LOGGER.info("- method 'setWorldTime' ");
                return null;
            }
            return mv;
        }
    }

    public static class NetHandlerPlayClientVisitor extends ClassVisitor
    {
        public NetHandlerPlayClientVisitor(ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if ("handleTimeUpdate".equals(name) || "func_147285_a".equals(name))
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
                            TimeZone.LOGGER.info("- method 'handleTimeUpdate' ");
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
        if ("net.minecraft.network.play.server.S03PacketTimeUpdate".equals(name))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'S03PacketTimeUpdate'. ");

            classReader.accept(new S03PacketTimeUpdateVisitor(classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        if ("net.minecraft.client.multiplayer.WorldClient".equals(name))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'WorldClient'. ");

            classReader.accept(new WorldClientVisitor(classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        if ("net.minecraft.client.network.NetHandlerPlayClient".equals(name))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'NetHandlerPlayClient'. ");

            classReader.accept(new NetHandlerPlayClientVisitor(classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        return basicClass;
    }

}
