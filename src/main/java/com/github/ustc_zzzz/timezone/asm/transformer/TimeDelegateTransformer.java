package com.github.ustc_zzzz.timezone.asm.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.ustc_zzzz.timezone.TimeZone;

import net.minecraft.launchwrapper.IClassTransformer;

public class TimeDelegateTransformer implements IClassTransformer
{
    public static class WorldInfoVisitor extends ClassVisitor
    {
        public WorldInfoVisitor(ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if ("func_76068_b".equals(name) || "setWorldTime".equals(name))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitFieldInsn(int opcode, String owner, String name, String desc)
                    {
                        if (opcode == Opcodes.PUTFIELD)
                        {
                            this.visitVarInsn(Opcodes.ALOAD, 0);
                            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", "setWorldTimeDelegete",
                                    "(JLnet/minecraft/world/storage/WorldInfo;)J", false);
                            TimeZone.LOGGER.info("- method 'setWorldTime' ");
                        }
                        super.visitFieldInsn(opcode, owner, name, desc);
                    }
                };
            }
            if ("func_76073_f".equals(name) || "getWorldTime".equals(name))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitInsn(int opcode)
                    {
                        if (opcode == Opcodes.LRETURN)
                        {
                            this.visitVarInsn(Opcodes.ALOAD, 0);
                            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", "getWorldTimeDelegete",
                                    "(JLnet/minecraft/world/storage/WorldInfo;)J", false);
                            TimeZone.LOGGER.info("- method 'getWorldTime' ");
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
        if ("net.minecraft.world.storage.WorldInfo".equals(name))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'WorldInfo'. ");

            classReader.accept(new WorldInfoVisitor(classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
}
