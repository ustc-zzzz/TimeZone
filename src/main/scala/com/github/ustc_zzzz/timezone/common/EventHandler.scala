package com.github.ustc_zzzz.timezone.common

import com.github.ustc_zzzz.timezone.api.{TimeZoneAPI, TimeZoneEvents}
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object EventHandler {
  private val api = TimeZoneAPI.INSTANCE

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def onRenderGameOverlayText(event: RenderGameOverlayEvent.Text) = {
    val info = event.getLeft
    if (!info.isEmpty) {
      val diff = APIDelegate.timeDiffFromRelativeToAbsolute()
      val diffD = Math.round(diff / 24000D)
      val diffT = (diff - diffD * 24000L).asInstanceOf[Int]
      val diffS = if (diffT > 0) '+' else '-'
      val diffH = Math.abs((diffT / 1000D).asInstanceOf[Int])
      val diffM = Math.abs((diffT % 1000) * 60 / 1000)
      val stackSize = api.stackSize
      val location = api.relative
      val locationX = location.getX
      val locationZ = location.getZ
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
    val player = Minecraft.getMinecraft.player
    if (player != null) {
      event.phase match {
        case TickEvent.Phase.START => api.pushLocation(api.position(player.posX, player.posZ))
        case TickEvent.Phase.END => api.popLocation
      }
    }
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def onClientTick(event: TickEvent.ClientTickEvent) = {
    val player = Minecraft.getMinecraft.player
    if (player != null) {
      event.phase match {
        case TickEvent.Phase.START => api.pushLocation(api.position(player.posX, player.posZ))
        case TickEvent.Phase.END => api.popLocation
      }
    }
  }

  @SubscribeEvent
  def onWorldTick(event: TickEvent.WorldTickEvent) = {
    // For protection. 
    event.phase match {
      case TickEvent.Phase.START => api.pushLocation(api.absolute())
      case TickEvent.Phase.END => api.popLocation
    }
  }

  @SubscribeEvent
  def onBlockLight(event: TimeZoneEvents.BlockPosLightEvent) = {
    event.phase match {
      case TimeZoneEvents.BlockPosLightEvent.Phase.START => api.pushLocation(api.position(event.pos))
      case TimeZoneEvents.BlockPosLightEvent.Phase.END => api.popLocation
    }
  }
}