package com.github.ustc_zzzz.timezone.asm.transformer

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

import org.objectweb.asm._

class TimeSyncTransformer extends TimeZoneTransformer {
  hook("net.minecraft.network.play.server.SPacketTimeUpdate", "<init>") {
    new MethodVisitor(Opcodes.ASM5, _) {
      override def visitVarInsn(o: Int, v: Int) = (o, v) match {
        case (Opcodes.LLOAD, 3) =>
          super.visitVarInsn(o, v)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("setSyncWorldTimeDelegate"), "(J)J", false)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("packPacketTimeUpdateDelegate"), "(J)J", false)
        case _ => super.visitVarInsn(o, v)
      }
    }
  }

  hook("net.minecraft.network.play.server.SPacketTimeUpdate", "func_149365_d" /*getWorldTime*/) {
    new MethodVisitor(Opcodes.ASM5, _) {
      override def visitInsn(o: Int) = o match {
        case Opcodes.LRETURN =>
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("unpackPacketTimeUpdateDelegate"), "(J)J", false)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("getSyncWorldTimeDelegate"), "(J)J", false)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("packPacketTimeUpdateDelegate"), "(J)J", false)
          super.visitInsn(o)
        case _ => super.visitInsn(o)
      }
    }
  }

  hook("net.minecraft.client.multiplayer.WorldClient", "func_72835_b" /*tick*/) {
    new MethodVisitor(Opcodes.ASM5, _) {
      override def visitMethodInsn(o: Int, w: String, n: String, d: String, i: Boolean) = (o, w, n, d, i) match {
        case (Opcodes.INVOKEVIRTUAL, owner, "b" | "setWorldTime", "(J)V", itf) =>
          super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/world/World", n, d, false)
          log
        case _ => super.visitMethodInsn(o, w, n, d, i)
      }
    }
  }

  hook("net.minecraft.client.multiplayer.WorldClient", "func_72877_b" /*setWorldTime*/) {
    new MethodVisitor(Opcodes.ASM5, _) {
      override def visitMethodInsn(o: Int, w: String, n: String, d: String, i: Boolean) = (o, w, n, d, i) match {
        case (Opcodes.INVOKESPECIAL, owner, name, desc, itf) =>
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("unpackPacketTimeUpdateDelegate"), "(J)J", false)
          super.visitMethodInsn(o, w, n, d, i)
        case _ => super.visitMethodInsn(o, w, n, d, i)
      }
    }
  }
}