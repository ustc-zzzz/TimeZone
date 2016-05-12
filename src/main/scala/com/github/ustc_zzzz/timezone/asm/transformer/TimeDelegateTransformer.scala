package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.TimeZone
import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper

class TimeDelegateTransformer extends TimeZoneTransformer {
  class WorldInfoVisitor(name: String, cv: ClassWriter) extends TimeZoneClassVisitor(name, cv) {
    override def visitDeobfMethod(methodName: String, methodDesc: String) = (methodName, methodDesc) match {
      case ("func_76068_b" | "setWorldTime", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitFieldInsn(opcode: Int, owner: String, name: String, desc: String) = (opcode, owner, name, desc) match {
          case (Opcodes.PUTFIELD, owner, name, desc) => {
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", 
                "setWorldTimeDelegete", "(JLnet/minecraft/world/storage/WorldInfo;)J")
            super.visitFieldInsn(opcode, owner, name, desc)
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case _ => super.visitFieldInsn(opcode, owner, name, desc)
        }
      }
      case ("func_76073_f" | "getWorldTime", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitInsn(opcode: Int) = opcode match {
          case Opcodes.LRETURN => {
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", 
                "getWorldTimeDelegete", "(JLnet/minecraft/world/storage/WorldInfo;)J")
            super.visitInsn(opcode)
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case _ => super.visitInsn(opcode)
        }
      }
      case (_, _) => self
    }
  }

  override def getClassVisitor(name: String, basicClass: Array[Byte]) = {
    name match {
      case "net.minecraft.world.storage.WorldInfo" => new WorldInfoVisitor(name, _)
      case _ => null
    }
  }

}