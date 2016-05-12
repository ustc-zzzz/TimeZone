package com.github.ustc_zzzz.timezone.asm

import scala.tools.asm.ClassReader
import scala.tools.asm.ClassVisitor
import scala.tools.asm.ClassWriter
import com.github.ustc_zzzz.timezone.TimeZone
import net.minecraft.launchwrapper.IClassTransformer
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import scala.tools.asm.Opcodes
import scala.tools.asm.MethodVisitor
import net.minecraftforge.fml.common.Mod

trait TimeZoneTransformer extends IClassTransformer {
  abstract class TimeZoneClassVisitor(name: String, cv: ClassWriter) extends ClassVisitor(Opcodes.ASM4, cv) {
    val className: String = FMLDeobfuscatingRemapper.INSTANCE unmap name.replace('.', '/')

    override def visitMethod(access: Int, name: String, desc: String, signature: String, excepetions: Array[String]) = {
      val methodVisitor = super.visitMethod(access, name, desc, signature, excepetions)

      val methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, name, desc)
      val methodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc)

      visitDeobfMethod(methodName, methodDesc)(methodVisitor)
    }

    def self(methodVisitor: MethodVisitor): MethodVisitor = methodVisitor

    def visitDeobfMethod(deObfName: String, deObfDesc: String): MethodVisitor => MethodVisitor
  }

  def getClassVisitor(transformedName: String, basicClass: Array[Byte]): ClassWriter => ClassVisitor

  override def transform(name: String, transformedName: String, basicClass: Array[Byte]) = {
    val classVisitor = getClassVisitor(transformedName, basicClass)

    if (classVisitor == null) basicClass else {
      val classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
      val classReader = new ClassReader(basicClass)

      TimeZone.logger info getClass.getSimpleName + ": Inject code into class '" + transformedName + "'. "

      classReader.accept(classVisitor(classWriter), ClassReader.EXPAND_FRAMES)
      classWriter.toByteArray
    }
  }
}