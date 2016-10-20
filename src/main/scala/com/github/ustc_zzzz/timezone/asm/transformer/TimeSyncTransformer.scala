package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

class TimeSyncTransformer extends TimeZoneTransformer {
  hook("net.minecraft.network.play.server.SPacketTimeUpdate", "<init>") {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitVarInsn(o: Int, v: Int) = (o, v) match {
        case (Opcodes.LLOAD, 3) => {
          super.visitVarInsn(o, v)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("setSyncWorldTimeDelegate"), "(J)J")
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("packPacketTimeUpdateDelegate"), "(J)J")
        }
        case _ => super.visitVarInsn(o, v)
      }
    }
  }

  hook("net.minecraft.network.play.server.SPacketTimeUpdate", "func_149365_d"/*getWorldTime*/) {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitInsn(o: Int) = o match {
        case Opcodes.LRETURN => {
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("getSyncWorldTimeDelegate"), "(J)J")
          super.visitInsn(o)
        }
        case _ => super.visitInsn(o)
      }
    }
  }
  
  hook("net.minecraft.client.multiplayer.WorldClient", "func_72877_b"/*setWorldTime*/) {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitMethodInsn(o: Int, w: String, n: String, d: String) = (o, w, n, d) match {
        case (Opcodes.INVOKESPECIAL, owner, name, desc) => {
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("unpackPacketTimeUpdateDelegate"), "(J)J")
          super.visitMethodInsn(o, w, n, d)
        }
        case _ => super.visitMethodInsn(o, w, n, d)
      }
    }
  }
}