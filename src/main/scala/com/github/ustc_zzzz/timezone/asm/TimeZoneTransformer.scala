package com.github.ustc_zzzz.timezone.asm

import scala.collection.immutable.Map
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import scala.tools.asm._

import com.github.ustc_zzzz.timezone.TimeZone

import net.minecraft.launchwrapper.IClassTransformer
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper
import org.apache.logging.log4j.LogManager

object TimeZoneTransformer {
  private val classes: HashSet[String] = HashSet()
  
  private val classLoader = getClass.getClassLoader
  
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
      if (information == null) {
        TimeZoneTransformer.logger info "- method '" + currentMethod + "'"
      } else {
        TimeZoneTransformer.logger info "- method '" + currentMethod + "': " + information
      }
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
            if (TimeZoneTransformer.enableRuntimeObf) {
              hooks.get(currentMethod) match {
                case None => methodVisitor
                case Some(methodProvider) => methodProvider(methodVisitor)
              }
            } else {
              (hooks :\ methodVisitor) {
                case ((k, v), visitor) => {
                  val methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, k, d);
                  if (methodName == currentMethod) v(visitor) else visitor
                }
              }
            }
          }
        }
        TimeZone.logger info getClass.getSimpleName + ": Inject code into class '" + transformedName + "'"
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        classWriter.toByteArray
      }
    }
  }
}