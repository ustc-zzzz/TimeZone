package com.github.ustc_zzzz.timezone.common

import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.translation.I18n
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

class CommonProxy {
  def preInit(event: FMLPreInitializationEvent): Unit = {
    val config = new Configuration(event.getSuggestedConfigurationFile)

    config.load()

    val commentTickPerChunkX = I18n.translateToLocal("timezone.configgui.tickPerChunkX.tooltip")
    val commentTickPerChunkZ = I18n.translateToLocal("timezone.configgui.tickPerChunkZ.tooltip")

    val tickPerChunkX = config.get(Configuration.CATEGORY_GENERAL, "tickPerChunkX", 60D, commentTickPerChunkX)
    val tickPerChunkZ = config.get(Configuration.CATEGORY_GENERAL, "tickPerChunkZ", 0D, commentTickPerChunkZ)

    tickPerChunkX.setValue(MathHelper.clamp(tickPerChunkX.getDouble, -12000D, 12000D))
    tickPerChunkZ.setValue(MathHelper.clamp(tickPerChunkZ.getDouble, -12000D, 12000D))

    APIDelegate.tickPMeterX = tickPerChunkX.getDouble / 16
    APIDelegate.tickPMeterZ = tickPerChunkZ.getDouble / 16

    config.save()
  }

  def init(event: FMLInitializationEvent): Unit = {
    MinecraftForge.EVENT_BUS register EventHandler
  }

  def postInit(event: FMLPostInitializationEvent): Unit = {
    ()
  }
}