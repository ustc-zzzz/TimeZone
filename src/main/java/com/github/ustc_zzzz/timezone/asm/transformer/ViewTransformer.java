package com.github.ustc_zzzz.timezone.asm.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.ustc_zzzz.timezone.TimeZone;

public class ViewTransformer implements IClassTransformer
{
    public static class WorldProviderVisitor extends ClassVisitor
    {
        private final String className;

        public WorldProviderVisitor(String className, ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
            this.className = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc);
            if ("calculateCelestialAngle".equals(methodName) || "func_76563_a".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitVarInsn(int opcode, int var)
                    {
                        super.visitVarInsn(opcode, var);
                        if (opcode == Opcodes.ISTORE)
                        {
                            Label l = new Label();
                            this.visitVarInsn(Opcodes.ILOAD, 4);
                            this.visitJumpInsn(Opcodes.IFGE, l);
                            this.visitIincInsn(4, 24000);
                            this.visitLabel(l);
                            TimeZone.LOGGER.info("- method '" + methodName + "' ");
                        }
                    }
                };
            }
            return mv;
        }
    }

    public static class GuiOverlayDebugVisitor extends ClassVisitor
    {
        private final String className;

        public GuiOverlayDebugVisitor(String className, ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
            this.className = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc);
            if ("call".equals(name) && "()Ljava/util/List;".equals(desc))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
                    {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        if (opcode == Opcodes.INVOKEVIRTUAL
                                && ("func_76073_f".equals(methodName) || "getWorldTime".equals(methodName))
                                && "()J".equals(desc))
                        {
                            Label l = new Label();
                            this.visitInsn(Opcodes.DUP2);
                            this.visitInsn(Opcodes.LCONST_0);
                            this.visitInsn(Opcodes.LCMP);
                            this.visitJumpInsn(Opcodes.IFGE, l);
                            this.visitLdcInsn(new Long(24000));
                            this.visitInsn(Opcodes.LSUB);
                            this.visitLabel(l);
                            TimeZone.LOGGER.info("- method '" + methodName + "' ");
                        }
                    }
                };
            }
            return mv;
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if ("net.minecraft.world.WorldProvider".equals(transformedName))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'WorldProvider'. ");

            classReader.accept(new WorldProviderVisitor(transformedName, classWriter), ClassReader.EXPAND_FRAMES);
            basicClass = classWriter.toByteArray();
        }
        if ("net.minecraft.client.gui.GuiOverlayDebug".equals(transformedName))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'GuiOverlayDebug'. ");

            classReader.accept(new GuiOverlayDebugVisitor(transformedName, classWriter), ClassReader.EXPAND_FRAMES);
            basicClass = classWriter.toByteArray();
        }
        return basicClass;
    }

}
