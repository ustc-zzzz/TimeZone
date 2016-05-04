package com.github.ustc_zzzz.timezone.asm.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.ustc_zzzz.timezone.TimeZone;

import net.minecraft.launchwrapper.IClassTransformer;

public class ViewTransformer implements IClassTransformer
{
    public static class WorldProviderVisitor extends ClassVisitor
    {
        public WorldProviderVisitor(ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

            if ("calculateCelestialAngle".equals(name) || "func_76563_a".equals(name))
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
                            TimeZone.LOGGER.info("- method 'calculateCelestialAngle' ");
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
        if ("net.minecraft.world.WorldProvider".equals(name))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info("Inject code into class 'WorldProvider'. ");

            classReader.accept(new WorldProviderVisitor(classWriter), ClassReader.EXPAND_FRAMES);
            basicClass = classWriter.toByteArray();
        }
        return basicClass;
    }

}
