package com.github.ustc_zzzz.timezone.asm.transformer

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer
import org.objectweb.asm.{MethodVisitor, Opcodes}

class ItemClockTransformer extends TimeZoneTransformer {
  hook("net.minecraft.item.ItemClock$1", "func_185087_a" /* wobble */) {
    new MethodVisitor(Opcodes.ASM5, _) {
      override def visitCode() = {
        super.visitCode()
        super.visitVarInsn(Opcodes.DLOAD, 2)
        super.visitVarInsn(Opcodes.ALOAD, 1)
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
          log("getClockAngleDiffDelegate"), "(Lnet/minecraft/world/World;)D", false)
        super.visitInsn(Opcodes.DSUB)
        super.visitVarInsn(Opcodes.DSTORE, 2)
      }

      override def visitInsn(o: Int) = o match {
        case Opcodes.DRETURN =>
          super.visitVarInsn(Opcodes.ALOAD, 1)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("getClockAngleDiffDelegate"), "(Lnet/minecraft/world/World;)D", false)
          super.visitInsn(Opcodes.DADD)
          super.visitInsn(Opcodes.DNEG)
          super.visitInsn(Opcodes.DCONST_1)
          super.visitInsn(Opcodes.DREM)
          super.visitInsn(Opcodes.DCONST_1)
          super.visitInsn(Opcodes.DADD)
          super.visitInsn(Opcodes.DCONST_1)
          super.visitInsn(Opcodes.DREM)
          super.visitInsn(o)
        case _ => super.visitInsn(o)
      }
    }
  }
}