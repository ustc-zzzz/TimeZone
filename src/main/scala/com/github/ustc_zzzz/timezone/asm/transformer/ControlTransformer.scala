package com.github.ustc_zzzz.timezone.asm.transformer

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

import org.objectweb.asm._

class ControlTransformer extends TimeZoneTransformer {
  hook("net.minecraft.command.CommandTime", "func_184881_a" /*execute*/) {
    new MethodVisitor(Opcodes.ASM5, _) {
      override def visitCode() = {
        super.visitCode()
        super.visitVarInsn(Opcodes.ALOAD, 1)
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
          log("processCommandDelegate"), "(Lnet/minecraft/command/ICommandSender;)V", false)
      }

      override def visitMethodInsn(o: Int, w: String, n: String, d: String, i: Boolean) = (o, w, n, d, i) match {
        case (Opcodes.INVOKESTATIC, owner, "parseInt", "(Ljava/lang/String;I)I", _) =>
          super.visitInsn(Opcodes.POP)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, log("parseInt"), "(Ljava/lang/String;)I", false)
        case (Opcodes.INVOKESTATIC, owner, "a", "(Ljava/lang/String;I)I", _) =>
          super.visitInsn(Opcodes.POP)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, log("func_175755_a"), "(Ljava/lang/String;)I", false)
        case _ => super.visitMethodInsn(o, w, n, d, i)
      }
    }
  }
}