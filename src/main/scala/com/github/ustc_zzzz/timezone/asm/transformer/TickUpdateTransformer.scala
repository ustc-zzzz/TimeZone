package com.github.ustc_zzzz.timezone.asm.transformer

import scala.tools.asm._

import com.github.ustc_zzzz.timezone.TimeZone
import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper

class TickUpdateTransformer extends TimeZoneTransformer {
  class WorldVisitor(name: String, cv: ClassWriter) extends TimeZoneClassVisitor(name, cv) {
    override def visitDeobfMethod(methodName: String, methodDesc: String) = (methodName, methodDesc) match {
      case ("func_72939_s" | "updateEntities", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitTypeInsn(opcode: Int, classType: String) = (opcode, FMLDeobfuscatingRemapper.INSTANCE map classType) match {
          case (Opcodes.CHECKCAST, "net/minecraft/entity/Entity") => {
            super.visitTypeInsn(opcode, classType)
            super.visitInsn(Opcodes.DUP)
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
              "updateEntitiesDelegate", "(Lnet/minecraft/entity/Entity;)V")
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case (Opcodes.CHECKCAST, "net/minecraft/tileentity/TileEntity") => {
            super.visitTypeInsn(opcode, classType)
            super.visitInsn(Opcodes.DUP)
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
              "updateTileEntitiesDelegate", "(Lnet/minecraft/tileentity/TileEntity;)V")
            TimeZone.logger info "- method '" + methodName + "' "
          }
          case _ => super.visitTypeInsn(opcode, classType)
        }
      }
      case ("func_175699_k" | "func_175721_c" | "getLight", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitCode() = {
          super.visitCode()
          super.visitVarInsn(Opcodes.ALOAD, 0)
          super.visitVarInsn(Opcodes.ALOAD, 1)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            "preBlockPosLightDelegate", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)V")
          TimeZone.logger.info("- method '" + methodName + "' ")
        }

        override def visitInsn(opcode: Int) = opcode match {
          case Opcodes.IRETURN => {
            super.visitVarInsn(Opcodes.ALOAD, 1)
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
              "postBlockPosLightDelegate", "(ILnet/minecraft/util/BlockPos;)I")
            super.visitInsn(opcode)
          }
          case _ => super.visitInsn(opcode)
        }
      }
      case ("func_175657_ab" | "getSkylightSubtracted", methodDesc) => new MethodVisitor(Opcodes.ASM4, _) {
        override def visitCode() = {
          super.visitCode()
          super.visitVarInsn(Opcodes.ALOAD, 0)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            "getSkylightSubtractedDelegate", "(Lnet/minecraft/world/World;)V")
          TimeZone.logger.info("- method '" + methodName + "' ")
        }
      }
      case (_, _) => self
    }
  }

  override def getClassVisitor(name: String, basicClass: Array[Byte]) = {
    name match {
      case "net.minecraft.world.World" => new WorldVisitor(name, _)
      case _ => null
    }
  }
}