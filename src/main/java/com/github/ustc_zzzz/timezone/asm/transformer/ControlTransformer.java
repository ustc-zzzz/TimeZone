package com.github.ustc_zzzz.timezone.asm.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.github.ustc_zzzz.timezone.TimeZone;

public class ControlTransformer implements IClassTransformer
{
    public static class CommandTimeVisitor extends ClassVisitor
    {
        private final String className;

        public CommandTimeVisitor(String className, ClassVisitor cv)
        {
            super(Opcodes.ASM5, cv);
            this.className = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            final String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc);
            if ("func_71515_b".equals(methodName) || "processCommand".equals(methodName))
            {
                return new MethodVisitor(Opcodes.ASM5, mv)
                {
                    @Override
                    public void visitCode()
                    {
                        super.visitCode();
                        this.visitVarInsn(Opcodes.ALOAD, 1);
                        this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
                                "processCommandDelegate", "(Lnet/minecraft/command/ICommandSender;)V", false);
                        TimeZone.LOGGER.info("- method '" + methodName + "' ");
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
                    {
                        if (opcode == Opcodes.INVOKESTATIC && "parseInt".equals(name)
                                && "(Ljava/lang/String;I)I".equals(desc))
                        {
                            this.visitInsn(Opcodes.POP);
                            this.visitInsn(Opcodes.POP);
                            this.visitVarInsn(Opcodes.ALOAD, 2);
                            this.visitInsn(Opcodes.ICONST_1);
                            this.visitInsn(Opcodes.AALOAD);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, "parseInt", "(Ljava/lang/String;)I",
                                    false);
                            return;
                        }
                        if (opcode == Opcodes.INVOKESTATIC && "a".equals(name) && "(Ljava/lang/String;I)I".equals(desc))
                        {
                            this.visitInsn(Opcodes.POP);
                            this.visitInsn(Opcodes.POP);
                            this.visitVarInsn(Opcodes.ALOAD, 2);
                            this.visitInsn(Opcodes.ICONST_1);
                            this.visitInsn(Opcodes.AALOAD);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, "func_175755_a",
                                    "(Ljava/lang/String;)I", false);
                            return;
                        }
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                    }
                };
            }
            return mv;
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if ("net.minecraft.command.CommandTime".equals(transformedName))
        {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            TimeZone.LOGGER.info(this.getClass().getSimpleName() + ": Inject code into class 'CommandTime'. ");

            classReader.accept(new CommandTimeVisitor(transformedName, classWriter), ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        return basicClass;
    }
}
