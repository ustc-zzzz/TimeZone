package com.github.ustc_zzzz.timezone.asm

import com.google.common.eventbus.EventBus
import net.minecraft.launchwrapper.IClassTransformer
import net.minecraftforge.fml.common.{DummyModContainer, LoadController, ModMetadata}
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin

@IFMLLoadingPlugin.Name("TimeZone")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions(Array("com.github.ustc_zzzz.timezone.asm."))
class TimeZoneCore extends IFMLLoadingPlugin {
  override def getASMTransformerClass = Array(
    "com.github.ustc_zzzz.timezone.asm.transformer.ControlTransformer",
    "com.github.ustc_zzzz.timezone.asm.transformer.DoRenderTransformer",
    "com.github.ustc_zzzz.timezone.asm.transformer.ItemClockTransformer",
    "com.github.ustc_zzzz.timezone.asm.transformer.TickUpdateTransformer",
    "com.github.ustc_zzzz.timezone.asm.transformer.TimeDelegateTransformer",
    "com.github.ustc_zzzz.timezone.asm.transformer.TimeSyncTransformer",
    "com.github.ustc_zzzz.timezone.asm.transformer.ViewTransformer")

  override def getAccessTransformerClass = "com.github.ustc_zzzz.timezone.asm.TimeZoneAccessTransformer"

  override def getModContainerClass = "com.github.ustc_zzzz.timezone.asm.TimeZoneModContainer"

  override def getSetupClass = null

  override def injectData(data: java.util.Map[String, Object]) = {
    TimeZoneTransformer.enableRuntimeObf = data.get("runtimeDeobfuscationEnabled").asInstanceOf[Boolean]
  }
}

class TimeZoneModContainer extends DummyModContainer(new ModMetadata) {
  final val metadata = getMetadata

  metadata.modId = "timezonecore"
  metadata.name = "TimeZone Core"
  metadata.version = "@version@"
  metadata.authorList = java.util.Arrays asList "ustc_zzzz"
  metadata.description = "TimeZone mod core, as the pre-loading mod. "
  metadata.credits = "Mojang AB, and the Forge and FML guys. "

  override def registerBus(bus: EventBus, controller: LoadController) = true
}

class TimeZoneAccessTransformer extends IClassTransformer {
  TimeZoneTransformer.logger.info("Coremod version @version@")
  TimeZoneTransformer.loadClasses()

  override def transform(name: String, transformedName: String, basicClass: Array[Byte]) = basicClass
}