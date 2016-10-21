package com.github.ustc_zzzz.timezone.asm

import org.apache.logging.log4j.LogManager

import com.google.common.eventbus.EventBus

import net.minecraftforge.fml.common.DummyModContainer
import net.minecraftforge.fml.common.LoadController
import net.minecraftforge.fml.common.ModMetadata
import net.minecraftforge.fml.relauncher.IFMLCallHook
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin._

@Name("TimeZone")
@MCVersion("1.10.2")
@TransformerExclusions(Array("com.github.ustc_zzzz.timezone.asm."))
class TimeZoneCore extends IFMLLoadingPlugin {
  override def getASMTransformerClass = Array(
      "com.github.ustc_zzzz.timezone.asm.transformer.ControlTransformer",
      "com.github.ustc_zzzz.timezone.asm.transformer.TickUpdateTransformer",
      "com.github.ustc_zzzz.timezone.asm.transformer.TimeDelegateTransformer",
      "com.github.ustc_zzzz.timezone.asm.transformer.TimeSyncTransformer",
      "com.github.ustc_zzzz.timezone.asm.transformer.ViewTransformer")

  override def getAccessTransformerClass = null

  override def getModContainerClass = "com.github.ustc_zzzz.timezone.asm.TimeZoneModContainer"

  override def getSetupClass = getModContainerClass

  override def injectData(data: java.util.Map[String, Object]) = ()
}

class TimeZoneModContainer extends DummyModContainer(new ModMetadata) with IFMLCallHook {
  final val metadata = getMetadata

  metadata.modId = "timezone-core"
  metadata.name = "TimeZone Core"
  metadata.version = "@version@"
  metadata.authorList = java.util.Arrays asList "ustc_zzzz"
  metadata.description = "TimeZone mod core, as the pre-loading mod. "
  metadata.credits = "Mojang AB, and the Forge and FML guys. "

  override def call = {
    TimeZoneTransformer.logger.info("Coremod loaded, version " + metadata.version)
    TimeZoneTransformer.loadClasses
    null
  }

  override def injectData(data: java.util.Map[String, Object]) = {
    TimeZoneTransformer.enableRuntimeObf = data.get("runtimeDeobfuscationEnabled").asInstanceOf[Boolean]
  }

  override def registerBus(bus: EventBus, controller: LoadController) = true
}
