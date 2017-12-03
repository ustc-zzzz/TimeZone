package com.github.ustc_zzzz.timezone.asm.transformer

import com.github.ustc_zzzz.timezone.asm.TimeZoneTransformer
import org.objectweb.asm.{MethodVisitor, Opcodes}

class DoRenderTransformer extends TimeZoneTransformer {
  hook("net.minecraft.client.renderer.entity.RenderManager", "func_188391_a" /* renderEntity */) {
    new MethodVisitor(Opcodes.ASM5, _) {
      override def visitCode() = {
        super.visitCode()
        super.visitVarInsn(Opcodes.ALOAD, 1)
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
          log("preRenderEntityDelegate"), "(Lnet/minecraft/entity/Entity;)V", false)
      }

      override def visitInsn(o: Int) = o match {
        case Opcodes.RETURN =>
          super.visitVarInsn(Opcodes.ALOAD, 1)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("postRenderEntityDelegate"), "(Lnet/minecraft/entity/Entity;)V", false)
          super.visitInsn(o)
        case _ => super.visitInsn(o)
      }
    }
  }

  hook("net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher", "func_192854_a" /* render */) {
    new MethodVisitor(Opcodes.ASM5, _) {
      override def visitCode() = {
        super.visitCode()
        super.visitVarInsn(Opcodes.ALOAD, 1)
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
          log("preRenderTileEntityDelegate"), "(Lnet/minecraft/tileentity/TileEntity;)V", false)
      }

      override def visitInsn(o: Int) = o match {
        case Opcodes.RETURN =>
          super.visitVarInsn(Opcodes.ALOAD, 1)
          super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/ustc_zzzz/timezone/asm/TimeZoneHooks",
            log("postRenderTileEntityDelegate"), "(Lnet/minecraft/tileentity/TileEntity;)V", false)
          super.visitInsn(o)
        case _ => super.visitInsn(o)
      }
    }
  }
}
