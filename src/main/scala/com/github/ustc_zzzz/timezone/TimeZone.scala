package com.github.ustc_zzzz.timezone

import com.github.ustc_zzzz.timezone.common.CommonProxy
import net.minecraftforge.fml.common.Mod.{EventHandler, Instance}
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.{Mod, SidedProxy}

@Mod(modid = TimeZone.modid, version = TimeZone.version, modLanguage = "scala",
  acceptedMinecraftVersions = "1.10.2", dependencies = "required-after:timezonecore@")
object TimeZone {
  final val modid = "timezone"
  final val version = "@version@"

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
