package com.github.ustc_zzzz.timezone.asm.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.ustc_zzzz.timezone.TimeZone;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class TickUpdateTransformer implements IClassTransformer
{
    public static class WorldVisitor extends ClassVisitor
    {
        private final String className;

        public WorldVisitor(String className, ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
            this.className = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc);
            if ("func_72939_s".equals(methodName) || "updateEntities".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitTypeInsn(int opcode, String type)
                    {
                        super.visitTypeInsn(opcode, type);
                        if ("net/minecraft/entity/Entity".equals(FMLDeobfuscatingRemapper.INSTANCE.map(type)))
                        {
                            this.visitInsn(Opcodes.DUP);
                            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", "updateEntitiesDelegate",
                                    "(Lnet/minecraft/entity/Entity;)V", false);
                            TimeZone.LOGGER.info("- method '" + methodName + "' ");
                            return;
                        }
                        if ("net/minecraft/tileentity/TileEntity".equals(FMLDeobfuscatingRemapper.INSTANCE.map(type)))
                        {
                            this.visitInsn(Opcodes.DUP);
                            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", "updateTileEntitiesDelegate",
                                    "(Lnet/minecraft/tileentity/TileEntity;)V", false);
                            TimeZone.LOGGER.info("- method '" + methodName + "' ");
                            return;
                        }
                    }
                };
            }
            if ("func_175699_k".equals(methodName) || "func_175721_c".equals(methodName)
                    || "getLight".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitCode()
                    {
                        super.visitCode();
                        this.visitVarInsn(Opcodes.ALOAD, 0);
                        this.visitVarInsn(Opcodes.ALOAD, 1);
                        this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
                                "preBlockPosLightDelegate",
                                "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)V", false);
                        TimeZone.LOGGER.info("- method '" + methodName + "' ");
                    }

                    @Override
                    public void visitInsn(int opcode)
                    {
                        if (opcode == Opcodes.IRETURN)
                        {
                            this.visitVarInsn(Opcodes.ALOAD, 1);
                            this.visitMethodInsn(Opcodes.INVOKESTATIC,
                                    "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", "postBlockPosLightDelegate",
                                    "(ILnet/minecraft/util/BlockPos;)I", false);
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
            if ("func_175657_ab".equals(methodName) || "getSkylightSubtracted".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitCode()
                    {
                        super.visitCode();
                        this.visitVarInsn(Opcodes.ALOAD, 0);
                        this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
                                "getSkylightSubtractedDelegate", "(Lnet/minecraft/world/World;)V", false);
                        TimeZone.LOGGER.info("- method '" + methodName + "' ");
                    }
                };
            }
            return mv;
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if ("net.minecraft.world.World".equals(transformedName))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'World'. ");

            classReader.accept(new WorldVisitor(transformedName, classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        return basicClass;
    }

}
