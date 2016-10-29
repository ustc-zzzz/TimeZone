package com.github.ustc_zzzz.timezone.asm

import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.tools.asm._

import org.apache.logging.log4j.LogManager

import net.minecraft.launchwrapper.IClassTransformer
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLConstructionEvent

object TimeZoneTransformer {
  private val classes: HashSet[String] = HashSet()
  
  private val classLoader = Launch.classLoader
  
  private[asm] var enableRuntimeObf = false;
  
  private[asm] def logger = LogManager.getLogger("TimeZone")
  
  private[asm] def loadClasses = classes foreach { Class.forName(_, false, classLoader) }
}

trait TimeZoneTransformer extends IClassTransformer {
  private val methods: HashMap[String, Map[String, MethodVisitor => MethodVisitor]] = HashMap()
  
  private var currentMethod: String = ""
  
  protected def hook(className: String, methodNames: String*)(methodProvider: MethodVisitor => MethodVisitor) = {
    val origin = methods.get(className).getOrElse(Map.empty[String, MethodVisitor => MethodVisitor])
    methods.put(className, (methodNames :\ origin) { (s, m) => m + ((s, methodProvider)) })
    TimeZoneTransformer.classes += className
    ()
  }
  
  protected def log(information: String) = {
    if (!currentMethod.isEmpty) {
      TimeZoneTransformer.logger.info(if (information == null) {
        "- method '%s'".format(currentMethod)
      } else {
        "- method '%s': %s".format(currentMethod, information)
      })
    }
    information
  }
  
  protected def log: String = log(null)
  
  override def transform(name: String, transformedName: String, basicClass: Array[Byte]) = {
    methods.get(transformedName) match {
      case None => basicClass
      case Some(hooks) => {
        val classReader = new ClassReader(basicClass)
        val classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val classVisitor = new ClassVisitor(Opcodes.ASM4, classWriter) {
          val className = FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/'))
          override def visitMethod(a: Int, n: String, d: String, s: String, e: Array[String]) = {
            val methodVisitor = super.visitMethod(a, n, d, s, e)
            currentMethod = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, n, d)
            if (TimeZoneTransformer.enableRuntimeObf) hooks.get(currentMethod) match {
              case None => methodVisitor
              case Some(methodProvider) => methodProvider(methodVisitor)
            } else (hooks :\ methodVisitor) {
              case ((name, provider), visitor) => {
                val methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, name, d)
                if (methodName == currentMethod) provider(visitor) else visitor
              }
            }
          }
        }
        TimeZoneTransformer.logger.info("%s: inject codes into class '%s'".format(getClass.getSimpleName, transformedName))
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        classWriter.toByteArray
      }
    }
  }
}