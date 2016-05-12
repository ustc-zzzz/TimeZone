package com.github.ustc_zzzz.timezone.asm

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI
import net.minecraft.world.storage.WorldInfo
import com.github.ustc_zzzz.timezone.api.TimeZoneEvents
import net.minecraft.util.BlockPos
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommandSender
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import com.github.ustc_zzzz.timezone.common.APIDelegate

object TimeZoneHooks {
  def getSyncWorldTimeDelegate(time: Long) = {
    time + APIDelegate.timeDiffFromRelativeToAbsolute
  }

  def setSyncWorldTimeDelegate(time: Long) = {
    time - APIDelegate.timeDiffFromRelativeToAbsolute
  }

  def getWorldTimeDelegete(time: Long, worldInfo: WorldInfo) = {
    time + APIDelegate.timeDiffFromRelative(APIDelegate.position(worldInfo.getSpawnX, worldInfo.getSpawnZ))
  }

  def setWorldTimeDelegete(time: Long, worldInfo: WorldInfo) = {
    time - APIDelegate.timeDiffFromRelative(APIDelegate.position(worldInfo.getSpawnX, worldInfo.getSpawnZ))
  }

  def handleTimeUpdateDelegate(doDaylightCycle: Boolean) = {
    Minecraft.getMinecraft.theWorld.getGameRules.setOrCreateGameRule("doDaylightCycle", doDaylightCycle.toString)
  }

  def findChunksForSpawningDelegate(posX: Int, posZ: Int) = {
    APIDelegate.popPosLocation
    APIDelegate.pushPosLocation(posX, posZ)
  }

  def updateEntitiesDelegate(e: Entity) = {
    if (e != null) {
      APIDelegate.popLocation
      APIDelegate.pushLocation(e.posX, e.posZ)
    }
  }

  def updateTileEntitiesDelegate(te: TileEntity) = {
    if (te != null && te.getPos != null) {
      APIDelegate.popLocation
      APIDelegate.pushLocation(te.getPos.getX, te.getPos.getZ)
    }
  }

  def preBlockPosLightDelegate(world: World, pos: BlockPos) = {
    val event = new TimeZoneEvents.BlockPosLightEvent.Pre(pos)
    MinecraftForge.EVENT_BUS.post(event)
    world.calculateInitialSkylight
  }

  def postBlockPosLightDelegate(light: Int, pos: BlockPos) = {
    val event = new TimeZoneEvents.BlockPosLightEvent.Post(pos, light)
    MinecraftForge.EVENT_BUS.post(event)
    event.light
  }

  def getSkylightSubtractedDelegate(world: World) = {
    world.calculateInitialSkylight
  }

  def processCommandDelegate(sender: ICommandSender) = {
    APIDelegate.popPosLocation
    APIDelegate.pushPosLocation(sender.getPosition.getX, sender.getPosition.getZ)
  }
}