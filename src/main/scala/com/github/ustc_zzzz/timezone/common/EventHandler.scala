package com.github.ustc_zzzz.timezone.common

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI
import com.github.ustc_zzzz.timezone.api.TimeZoneEvents

import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.fml.relauncher.Side

object EventHandler {
    final val instance = new EventHandler  
}

class EventHandler {
  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def onRenderGameOverlayText(event: RenderGameOverlayEvent.Text) = {
    val info = event.left
    if (!info.isEmpty) {
      val diff = APIDelegate.timeDiffFromRelativeToAbsolute
      val diffD = Math.round(diff / 24000D)
      val diffT = (diff - diffD * 24000L).asInstanceOf[Int]
      val diffS = if (diffT > 0) '+' else '-'
      val diffH = Math.abs((diffT / 1000D).asInstanceOf[Int])
      val diffM = Math.abs((diffT % 1000) * 60 / 1000)
      val stackSize = TimeZoneAPI.INSTANCE.stackSize
      val locationX = TimeZoneAPI.INSTANCE.getLocationX
      val locationZ = TimeZoneAPI.INSTANCE.getLocationZ
      info add f""
      info add f"[TimeZone]"
      info add f"StackSize: $stackSize%d"
      info add f"LocationX: $locationX%.5f"
      info add f"LocationZ: $locationZ%.5f"
      info add f"TimeDiff: $diff%d, $diffD%d Day(s), $diffS%c$diffH%02d$diffM%02d"
    }
    ()
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def onRenderTick(event: TickEvent.RenderTickEvent) = {
    val player = Minecraft.getMinecraft.thePlayer
    if (player != null) {
      event.phase match {
        case TickEvent.Phase.START => TimeZoneAPI.INSTANCE.pushLocation(player.posX, player.posZ)
        case TickEvent.Phase.END => TimeZoneAPI.INSTANCE.popLocation
      }
    }
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def onClientTick(event: TickEvent.ClientTickEvent) = {
    val player = Minecraft.getMinecraft.thePlayer
    if (player != null) {
      event.phase match {
        case TickEvent.Phase.START => TimeZoneAPI.INSTANCE.pushLocation(player.posX, player.posZ)
        case TickEvent.Phase.END => TimeZoneAPI.INSTANCE.popLocation
      }
    }
  }
  
  @SubscribeEvent
  def onWorldTick(event: TickEvent.WorldTickEvent) = {
    // For protection. 
    event.phase match {
      case TickEvent.Phase.START => TimeZoneAPI.INSTANCE.pushLocation(0D, 0D)
      case TickEvent.Phase.END => TimeZoneAPI.INSTANCE.popLocation
    }
  }

  @SubscribeEvent
  def onBlockLight(event: TimeZoneEvents.BlockPosLightEvent) = {
    event.phase match {
      case TimeZoneEvents.BlockPosLightEvent.Phase.START => TimeZoneAPI.INSTANCE.pushPosLocation(event.pos.getX, event.pos.getZ)
      case TimeZoneEvents.BlockPosLightEvent.Phase.END => TimeZoneAPI.INSTANCE.popPosLocation
    }
  }
}