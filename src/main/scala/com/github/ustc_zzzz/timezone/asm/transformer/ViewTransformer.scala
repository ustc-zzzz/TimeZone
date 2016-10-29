package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

class ViewTransformer extends TimeZoneTransformer {
  hook("net.minecraft.world.WorldProvider", "func_76563_a"/*calculateCelestialAngle*/) {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitVarInsn(o: Int, v: Int) = (o, v) match {
        case (Opcodes.ISTORE, variable) => {
          val l = new Label
          super.visitVarInsn(o, v)
          super.visitVarInsn(Opcodes.ILOAD, 4)
          super.visitJumpInsn(Opcodes.IFGE, l)
          super.visitIincInsn(4, 24000)
          super.visitLabel(l)
          log
        }
        case _ => super.visitVarInsn(o, v)
      }
    }
  }

  hook("net.minecraft.client.gui.GuiOverlayDebug", "call") {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitMethodInsn(o: Int, w: String, n: String, d: String) = (o, w, n, d) match {
        case (Opcodes.INVOKEVIRTUAL, owner, "Q" | "getWorldTime", "()J") => {
          val l = new Label
          super.visitMethodInsn(o, w, n, d)
          super.visitInsn(Opcodes.DUP2)
          super.visitInsn(Opcodes.LCONST_0)
          super.visitInsn(Opcodes.LCMP)
          super.visitJumpInsn(Opcodes.IFGE, l)
          super.visitLdcInsn(Long.box(24000))
          super.visitInsn(Opcodes.LSUB);
          super.visitLabel(l);
          log
        }
        case _ => super.visitMethodInsn(o, w, n, d)
      }
    }
  }
}