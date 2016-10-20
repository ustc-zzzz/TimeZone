package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

class ControlTransformer extends TimeZoneTransformer {
  hook("net.minecraft.command.CommandTime", "func_71515_b"/*execute*/) {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitCode = {
        super.visitCode()
        super.visitVarInsn(Opcodes.ALOAD, 1)
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
          log("processCommandDelegate"), "(Lnet/minecraft/command/ICommandSender;)V")
      }

      override def visitMethodInsn(o: Int, w: String, n: String, d: String) = (o, w, n, d) match {
        case (Opcodes.INVOKESTATIC, owner, "parseInt", "(Ljava/lang/String;I)I") => {
          super.visitInsn(Opcodes.POP)
          super.visitInsn(Opcodes.POP)
          super.visitVarInsn(Opcodes.ALOAD, 3)
          super.visitInsn(Opcodes.ICONST_1)
          super.visitInsn(Opcodes.AALOAD)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, log("parseInt"), "(Ljava/lang/String;)I")
        }
        case (Opcodes.INVOKESTATIC, owner, "a", "(Ljava/lang/String;I)I") => {
          super.visitInsn(Opcodes.POP)
          super.visitInsn(Opcodes.POP)
          super.visitVarInsn(Opcodes.ALOAD, 3)
          super.visitInsn(Opcodes.ICONST_1)
          super.visitInsn(Opcodes.AALOAD)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, log("func_175755_a"), "(Ljava/lang/String;)I")
        }
        case _ => super.visitMethodInsn(o, w, n, d)
      }
    }
  }
}