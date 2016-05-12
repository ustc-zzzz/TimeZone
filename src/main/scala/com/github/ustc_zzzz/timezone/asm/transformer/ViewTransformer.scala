package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.TimeZone
import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

class ViewTransformer extends TimeZoneTransformer {
  class WorldProviderVisitor(name: String, cv: ClassWriter) extends TimeZoneClassVisitor(name, cv) {
    override def visitDeobfMethod(methodName: String, methodDesc: String) = (methodName, methodDesc) match {
      case ("func_76563_a" | "calculateCelestialAngle", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitVarInsn(opcode: Int, variable: Int) = (opcode, variable) match {
          case (Opcodes.ISTORE, variable: Int) => {
            val l = new Label
            super.visitVarInsn(opcode, variable)
            super.visitVarInsn(Opcodes.ILOAD, 4)
            super.visitJumpInsn(Opcodes.IFGE, l)
            super.visitIincInsn(4, 24000)
            super.visitLabel(l)
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case _ => super.visitVarInsn(opcode, variable)
        }
      }
      case (_, _) => self
    }
  }

  class GuiOverlayDebugVisitor(name: String, cv: ClassWriter) extends TimeZoneClassVisitor(name, cv) {
    override def visitDeobfMethod(methodName: String, methodDesc: String) = (methodName, methodDesc) match {
      case ("call", "()Ljava/util/List;") => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String) = (opcode, owner, name, desc) match {
          case (Opcodes.INVOKEVIRTUAL, owner, "L" | "getWorldTime", "()J") => {
            val l = new Label
            super.visitMethodInsn(opcode, owner, name, desc)
            super.visitInsn(Opcodes.DUP2)
            super.visitInsn(Opcodes.LCONST_0)
            super.visitInsn(Opcodes.LCMP)
            super.visitJumpInsn(Opcodes.IFGE, l)
            super.visitLdcInsn(Long.box(24000))
            super.visitInsn(Opcodes.LSUB);
            super.visitLabel(l);
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case _ => super.visitMethodInsn(opcode, owner, name, desc)
        }
      }
      case (_, _) => self
    }
  }

  override def getClassVisitor(name: String, basicClass: Array[Byte]) = {
    name match {
      case "net.minecraft.world.WorldProvider" => new WorldProviderVisitor(name, _)
      case "net.minecraft.client.gui.GuiOverlayDebug" => new GuiOverlayDebugVisitor(name, _)
      case _ => null
    }
  }
}