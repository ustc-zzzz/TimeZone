package com.github.ustc_zzzz.timezone.asm

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fml.common.DummyModContainer
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.relauncher.IFMLCallHook
import net.minecraftforge.fml.common.ModMetadata

import org.apache.logging.log4j.LogManager

import com.google.common.eventbus.EventBus;

import java.util.Arrays

@IFMLLoadingPlugin.Name("TimeZone")
@IFMLLoadingPlugin.MCVersion("")
@IFMLLoadingPlugin.TransformerExclusions(Array("com.github.ustc_zzzz.timezone.asm."))
class TimeZoneCore extends IFMLLoadingPlugin {
  override def getASMTransformerClass: Array[String] = ForgeVersion mcVersion match {
    case "1.8.9" => Array(
      "com.github.ustc_zzzz.timezone.asm.transformer.TickUpdateTransformer",
      "com.github.ustc_zzzz.timezone.asm.transformer.ControlTransformer",
      "com.github.ustc_zzzz.timezone.asm.transformer.ViewTransformer",
      "com.github.ustc_zzzz.timezone.asm.transformer.TimeSyncTransformer",
      "com.github.ustc_zzzz.timezone.asm.transformer.TimeDelegateTransformer")
  }

  override def getAccessTransformerClass: String = null

  override def getModContainerClass: String = "com.github.ustc_zzzz.timezone.asm.TimeZoneModContainer"

  override def getSetupClass: String = getModContainerClass

  override def injectData(data: java.util.Map[String, Object]): Unit = ()
}

class TimeZoneModContainer extends DummyModContainer(new ModMetadata) with IFMLCallHook {
  final val metadata = getMetadata;

  metadata.modId = "timezone-core"
  metadata.name = "TimeZone Core"
  metadata.version = "@version@"
  metadata.authorList = Arrays asList "ustc_zzzz"
  metadata.description = "TimeZone mod core, as the pre-loading mod."
  metadata.credits = "Mojang AB, and the Forge and FML guys. "

  override def call: Void = {
    LogManager.getLogger("TimeZone") info "Coremod loaded, version " + metadata.version + ". "
    null
  }

  override def injectData(data: java.util.Map[String, Object]): Unit = ()

  override def registerBus(bus: EventBus, controller: LoadController): Boolean = true
}
