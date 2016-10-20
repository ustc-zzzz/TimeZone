package com.github.ustc_zzzz.timezone

import org.apache.logging.log4j.LogManager

import com.github.ustc_zzzz.timezone.common.CommonProxy

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.Instance
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.Mod.EventHandler

@Mod(modid = TimeZone.modid, version = TimeZone.version, modLanguage = "scala",
  acceptedMinecraftVersions = "1.10.2", dependencies = "required-after:timezone-core@")
object TimeZone {
  final val modid = "timezone"
  final val version = "@version@"

  final val logger = LogManager.getLogger("TimeZone")

  final val proxyServerSide = "com.github.ustc_zzzz.timezone.common.CommonProxy"
  final val proxyClientSide = "com.github.ustc_zzzz.timezone.client.ClientProxy"

  @SidedProxy(serverSide = TimeZone.proxyServerSide, clientSide = TimeZone.proxyClientSide)
  var proxy: CommonProxy = null

  @Instance(TimeZone.modid)
  var instance = null

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) = proxy preInit event

  @EventHandler
  def init(event: FMLInitializationEvent) = proxy init event

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) = proxy postInit event
}
