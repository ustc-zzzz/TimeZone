package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.TimeZone
import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

class TimeSyncTransformer extends TimeZoneTransformer {
  class S03PacketTimeUpdateVisitor(name: String, cv: ClassWriter) extends TimeZoneClassVisitor(name, cv) {
    var hasDoDayLightCycle = false

    override def visitDeobfMethod(methodName: String, methodDesc: String) = (methodName, methodDesc) match {
      case ("<init>", "(JJZ)V") => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitVarInsn(opcode: Int, variable: Int) = (opcode, variable) match {
          case (Opcodes.LLOAD, 3) => {
            super.visitVarInsn(opcode, variable)
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
              "setSyncWorldTimeDelegate", "(J)J")
            TimeZone.logger info "- constructor "
          }
          case (Opcodes.ILOAD, 5) => {
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitVarInsn(opcode, variable)
            super.visitFieldInsn(Opcodes.PUTFIELD,
              "net/minecraft/network/play/server/S03PacketTimeUpdate", "doDayLightCycle", "Z")
            super.visitInsn(Opcodes.RETURN)
            TimeZone.logger info "- constructor "
          }
          case _ => super.visitVarInsn(opcode, variable)
        }
      }
      case ("getWorldTime" | "func_149365_d", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitInsn(opcode: Int) = opcode match {
          case Opcodes.LRETURN => {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
              "getSyncWorldTimeDelegate", "(J)J")
            super.visitInsn(opcode)
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case _ => super.visitInsn(opcode)
        }
      }
      case ("readPacketData" | "func_148837_a", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitInsn(opcode: Int) = opcode match {
          case Opcodes.RETURN => {
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitVarInsn(Opcodes.ALOAD, 1)
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer",
              "readBoolean", "()Z")
            super.visitFieldInsn(Opcodes.PUTFIELD, "net/minecraft/network/play/server/S03PacketTimeUpdate",
              "doDayLightCycle", "Z")
            super.visitInsn(opcode)
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case _ => super.visitInsn(opcode)
        }
      }
      case ("writePacketData" | "func_148840_b", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitInsn(opcode: Int) = opcode match {
          case Opcodes.RETURN => {
            super.visitVarInsn(Opcodes.ALOAD, 1)
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/network/play/server/S03PacketTimeUpdate",
              "doDayLightCycle", "Z");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/PacketBuffer",
              "writeBoolean", "(Z)Lio/netty/buffer/ByteBuf;")
            super.visitInsn(Opcodes.POP);
            super.visitInsn(opcode)
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case _ => super.visitInsn(opcode)
        }
      }
      case (_, _) => self
    }

    override def visitEnd() = {
      if (!hasDoDayLightCycle) {
        val fv = super.visitField(Opcodes.ACC_PUBLIC, "doDayLightCycle", "Z", null, null)
        if (fv != null) fv.visitEnd()
        hasDoDayLightCycle = true
        TimeZone.logger info "- field 'doDayLightCycle' "
      }
      super.visitEnd()
    }
  }

  class WorldClientVisitor(name: String, cv: ClassWriter) extends TimeZoneClassVisitor(name, cv) {
    override def visitDeobfMethod(methodName: String, methodDesc: String) = (methodName, methodDesc) match {
      case ("func_72877_b" | "setWorldTime", methodDesc) => mv => {
        mv.visitCode()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.LLOAD, 1)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/world/World", methodName, "(J)V")
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(1, 1)
        mv.visitEnd()
        TimeZone.logger info "- method '" + methodName + "' "
        null
      }
      case (_, _) => self
    }
  }

  class NetHandlerPlayClientVisitor(name: String, cv: ClassWriter) extends TimeZoneClassVisitor(name, cv) {
    override def visitDeobfMethod(methodName: String, methodDesc: String) = (methodName, methodDesc) match {
      case ("func_147285_a" | "handleTimeUpdate", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitInsn(opcode: Int) = opcode match {
          case Opcodes.RETURN => {
            super.visitVarInsn(Opcodes.ALOAD, 0)
            super.visitVarInsn(Opcodes.ALOAD, 1)
            super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/network/play/server/S03PacketTimeUpdate",
              "doDayLightCycle", "Z")
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
              "handleTimeUpdateDelegate", "(Z)V")
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
      case "net.minecraft.network.play.server.S03PacketTimeUpdate" => new S03PacketTimeUpdateVisitor(name, _)
      case "net.minecraft.client.multiplayer.WorldClient" => new WorldClientVisitor(name, _)
      case "net.minecraft.client.network.NetHandlerPlayClient" => new NetHandlerPlayClientVisitor(name, _)
      case _ => null
    }
  }

}