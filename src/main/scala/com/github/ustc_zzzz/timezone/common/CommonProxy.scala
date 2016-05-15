package com.github.ustc_zzzz.timezone.common

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraft.util.StatCollector
import net.minecraft.util.MathHelper

class CommonProxy {
  def preInit(event: FMLPreInitializationEvent): Unit = {
    val config = new Configuration(event.getSuggestedConfigurationFile)
    config.load
    val commentTickPerChunkX = StatCollector.translateToLocal("timezone.configgui.tickPerChunkX.tooltip")
    val commentTickPerChunkZ = StatCollector.translateToLocal("timezone.configgui.tickPerChunkZ.tooltip")
    val tickPerChunkX = config.get(Configuration.CATEGORY_GENERAL, "tickPerChunkX", 60D, commentTickPerChunkX)
    val tickPerChunkZ = config.get(Configuration.CATEGORY_GENERAL, "tickPerChunkZ", 0D, commentTickPerChunkZ)
    tickPerChunkX setValue MathHelper.clamp_double(tickPerChunkX.getDouble, -12000D, 12000D)
    tickPerChunkZ setValue MathHelper.clamp_double(tickPerChunkZ.getDouble, -12000D, 12000D)
    APIDelegate.tickPMeterX = tickPerChunkX.getDouble / 16
    APIDelegate.tickPMeterZ = tickPerChunkZ.getDouble / 16
    config.save
  }

  def init(event: FMLInitializationEvent): Unit = {
    MinecraftForge.EVENT_BUS register EventHandler.instance
  }

  def postInit(event: FMLPostInitializationEvent): Unit = {
    ()
  }
}