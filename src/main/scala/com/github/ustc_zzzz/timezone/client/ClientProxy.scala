package com.github.ustc_zzzz.timezone.client

import com.github.ustc_zzzz.timezone.common.CommonProxy
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT)
class ClientProxy extends CommonProxy {
  // nothing
  override def preInit(event: FMLPreInitializationEvent): Unit = super.preInit(event)

  // nothing
  override def init(event: FMLInitializationEvent): Unit = super.init(event)

  // nothing
  override def postInit(event: FMLPostInitializationEvent): Unit = super.postInit(event)
}