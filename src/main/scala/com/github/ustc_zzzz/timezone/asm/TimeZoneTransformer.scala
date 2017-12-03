package com.github.ustc_zzzz.timezone.asm

import net.minecraft.launchwrapper.{IClassTransformer, Launch, LaunchClassLoader}
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.apache.logging.log4j.LogManager
import org.objectweb.asm._

import scala.collection.mutable

object TimeZoneTransformer {
  private val classes: mutable.TreeSet[String] = mutable.TreeSet()

  private val classLoader: LaunchClassLoader = Launch.classLoader

  private def loadClass(c: String): Unit = try classLoader.findClass(c) catch {
    case e: ClassNotFoundException =>
      TimeZoneTransformer.logger.info("{}: skip class '{}'", Seq("TimeZoneTransformer", c): _*)
      TimeZoneTransformer.logger.debug("TimeZoneTransformer: ", e)
  }

  private[asm] var enableRuntimeObf = false

  private[asm] def logger = LogManager.getLogger("TimeZone")

  private[asm] def loadClasses = classes foreach loadClass
}

trait TimeZoneTransformer extends IClassTransformer {
  private val methods: mutable.HashMap[String, Map[String, MethodVisitor => MethodVisitor]] = mutable.HashMap()

  private var currentMethod: String = ""

  protected def hook(className: String, methodNames: String*)(methodProvider: MethodVisitor => MethodVisitor) = {
    val origin = methods.getOrElse(className, Map.empty[String, MethodVisitor => MethodVisitor])
    methods.put(className, (methodNames :\ origin) { (s, m) => m + ((s, methodProvider)) })
    TimeZoneTransformer.classes += className
    ()
  }

  protected def log(information: String) = {
    if (!currentMethod.isEmpty && information != null) {
        TimeZoneTransformer.logger.debug("- method '{}': {}", Seq(currentMethod, information): _*)
    }
    information
  }

  protected def log = {
    if (!currentMethod.isEmpty) {
        TimeZoneTransformer.logger.debug("- method '{}'", Seq(currentMethod): _*)
    }
    ()
  }

  override def transform(name: String, transformedName: String, basicClass: Array[Byte]) = {
    def generateVisitor(hooks: Map[String, MethodVisitor => MethodVisitor])(classWriter: ClassWriter): ClassVisitor = {
      new ClassVisitor(Opcodes.ASM4, classWriter) {
        val className = FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/'))

        override def visitMethod(a: Int, n: String, d: String, s: String, e: Array[String]) = {
          val methodVisitor = super.visitMethod(a, n, d, s, e)
          currentMethod = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, n, d)
          if (TimeZoneTransformer.enableRuntimeObf) hooks.get(currentMethod) match {
            case None => methodVisitor
            case Some(methodProvider) => methodProvider(methodVisitor)
          } else (hooks :\ methodVisitor) {
            case ((methodName, methodProvider), visitor) =>
              val mappedName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, methodName, d)
              if (mappedName == currentMethod) methodProvider(visitor) else visitor
          }
        }
      }
    }

    methods.get(transformedName) match {
      case None => basicClass
      case Some(hooks) =>
        val classReader = new ClassReader(basicClass)
        val classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES)
        TimeZoneTransformer.logger.info("{}: inject codes into class '{}'", Seq(getClass.getSimpleName, transformedName): _*)
        classReader.accept(generateVisitor(hooks)(classWriter), ClassReader.EXPAND_FRAMES)
        classWriter.toByteArray
    }
  }
}