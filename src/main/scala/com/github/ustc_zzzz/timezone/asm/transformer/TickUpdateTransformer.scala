package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper

class TickUpdateTransformer extends TimeZoneTransformer {
  hook("net.minecraft.world.World", "func_72939_s"/*updateEntities*/) {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitTypeInsn(o: Int, t: String) = (o, t) match {
        case (Opcodes.CHECKCAST, "rw" | "net/minecraft/entity/Entity") => {
          super.visitTypeInsn(o, t)
          super.visitInsn(Opcodes.DUP)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("updateEntitiesDelegate"), "(Lnet/minecraft/entity/Entity;)V")
        }
        case (Opcodes.CHECKCAST, "aqk" | "net/minecraft/tileentity/TileEntity") => {
          super.visitTypeInsn(o, t)
          super.visitInsn(Opcodes.DUP)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("updateTileEntitiesDelegate"), "(Lnet/minecraft/tileentity/TileEntity;)V")
        }
        case _ => super.visitTypeInsn(o, t)
      }
    }
  }

  hook("net.minecraft.world.World", "func_175699_k"/*getLight*/, "func_175721_c"/*getLight*/) {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitCode() = {
        super.visitCode()
        super.visitVarInsn(Opcodes.ALOAD, 0)
        super.visitVarInsn(Opcodes.ALOAD, 1)
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
          log("preBlockPosLightDelegate"), "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V")
      }

      override def visitInsn(o: Int) = o match {
        case Opcodes.IRETURN => {
          super.visitVarInsn(Opcodes.ALOAD, 1)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("postBlockPosLightDelegate"), "(ILnet/minecraft/util/math/BlockPos;)I")
          super.visitInsn(o)
        }
        case _ => super.visitInsn(o)
      }
    }
  }

  hook("net.minecraft.world.World", "func_175657_ab"/*getSkylightSubtracted*/) {
    new MethodVisitor(Opcodes.ASM4, _) {
      override def visitCode() = {
        super.visitCode()
        super.visitVarInsn(Opcodes.ALOAD, 0)
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
          log("getSkylightSubtractedDelegate"), "(Lnet/minecraft/world/World;)V")
      }
    }
  }
}