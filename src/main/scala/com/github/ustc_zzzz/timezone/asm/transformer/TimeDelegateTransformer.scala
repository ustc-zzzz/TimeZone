package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

class TimeDelegateTransformer extends TimeZoneTransformer {
  hook("net.minecraft.world.storage.WorldInfo", "func_76068_b"/*setWorldTime*/) { 
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitFieldInsn(o: Int, w: String, n: String, d:String) = (o, w, n, d) match {
        case (Opcodes.PUTFIELD, owner, name, desc) => {
          super.visitVarInsn(Opcodes.ALOAD, 0)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", 
              log("setWorldTimeDelegate"), "(JLnet/minecraft/world/storage/WorldInfo;)J")
          super.visitFieldInsn(o, w, n, d)
        }
        case _ => super.visitFieldInsn(o, w, n, d)
      }
    }
  }
  
  hook("net.minecraft.world.storage.WorldInfo", "func_76073_f"/*getWorldTime*/) {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitInsn(o: Int) = o match {
        case Opcodes.LRETURN => {
          super.visitVarInsn(Opcodes.ALOAD, 0)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks", 
              log("getWorldTimeDelegate"), "(JLnet/minecraft/world/storage/WorldInfo;)J")
          super.visitInsn(o)
        }
        case _ => super.visitInsn(o)
      }
    }
  }
}