package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.TimeZone
import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

class ControlTransformer extends TimeZoneTransformer {
  class CommandTimeVisitor(name: String, cv: ClassWriter) extends TimeZoneClassVisitor(name, cv) {
    override def visitDeobfMethod(methodName: String, methodDesc: String) = (methodName, methodDesc) match {
      case ("func_71515_b" | "processCommand", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitCode() = {
          super.visitCode()
          super.visitVarInsn(Opcodes.ALOAD, 1)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            "processCommandDelegate", "(Lnet/minecraft/command/ICommandSender;)V")
          TimeZone.logger info "- method '" + methodName + "' "
        }

        override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String) = (opcode, owner, name, desc) match {
          case (Opcodes.INVOKESTATIC, owner, "parseInt", "(Ljava/lang/String;I)I") => {
            super.visitInsn(Opcodes.POP)
            super.visitInsn(Opcodes.POP)
            super.visitVarInsn(Opcodes.ALOAD, 2)
            super.visitInsn(Opcodes.ICONST_1)
            super.visitInsn(Opcodes.AALOAD)
            super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, "parseInt", "(Ljava/lang/String;)I")
          }
          case (Opcodes.INVOKESTATIC, owner, "a", "(Ljava/lang/String;I)I") => {
            super.visitInsn(Opcodes.POP)
            super.visitInsn(Opcodes.POP)
            super.visitVarInsn(Opcodes.ALOAD, 2)
            super.visitInsn(Opcodes.ICONST_1)
            super.visitInsn(Opcodes.AALOAD)
            super.visitMethodInsn(Opcodes.INVOKESTATIC, owner, "func_175755_a", "(Ljava/lang/String;)I")
          }
          case _ => super.visitMethodInsn(opcode, owner, name, desc)
        }
      }
      case (_, _) => self
    }
  }

  override def getClassVisitor(name: String, basicClass: Array[Byte]) = {
    name match {
      case "net.minecraft.command.CommandTime" => new CommandTimeVisitor(name, _)
      case _ => null
    }
  }
}