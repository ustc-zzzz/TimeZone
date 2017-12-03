package com.github.ustc_zzzz.timezone.asm

import com.github.ustc_zzzz.timezone.api.TimeZoneEvents
import com.github.ustc_zzzz.timezone.common.APIDelegate
import net.minecraft.command.ICommandSender
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.storage.WorldInfo
import net.minecraftforge.common.MinecraftForge

object TimeZoneHooks {
  def getSyncWorldTimeDelegate(time: Long) = {
    time + APIDelegate.timeDiffFromRelativeToAbsolute
  }

  def setSyncWorldTimeDelegate(time: Long) = {
    time - APIDelegate.timeDiffFromRelativeToAbsolute
  }

  def packPacketTimeUpdateDelegate(time: Long) = {
    if (time <= 0) {
      val days = time / 24000
      (time + 1 + (1 - 2 * days) * 24000) & -2L
    } else {
      (time - 1) | 1L
    }
  }

  def unpackPacketTimeUpdateDelegate(time: Long) = {
    if ((time & 1L) == 0) {
      val days = (time - 1) / 24000
      if (Math.random < 0.5) time - 1 - (1 + 2 * days) * 24000 else time - (1 + 2 * days) * 24000
    } else {
      if (Math.random < 0.5) time else time + 1
    }
  }

  def getWorldTimeDelegate(time: Long, worldInfo: WorldInfo) = {
    time + APIDelegate.timeDiffFromRelative(APIDelegate.position(worldInfo.getSpawnX, worldInfo.getSpawnZ)) + 1000
  }

  def setWorldTimeDelegate(time: Long, worldInfo: WorldInfo) = {
    time - APIDelegate.timeDiffFromRelative(APIDelegate.position(worldInfo.getSpawnX, worldInfo.getSpawnZ)) - 1000
  }

  def findChunksForSpawningDelegate(posX: Int, posZ: Int) = {
    APIDelegate.popLocation()
    APIDelegate.pushLocation(APIDelegate.position(posX, posZ))
  }

  def updateEntitiesDelegate(e: Entity) = {
    if (e != null) {
      APIDelegate.popLocation()
      APIDelegate.pushLocation(APIDelegate.position(e))
    }
  }

  def updateTileEntitiesDelegate(te: TileEntity) = {
    if (te != null && te.getPos != null) {
      APIDelegate.popLocation()
      APIDelegate.pushLocation(APIDelegate.position(te.getPos))
    }
  }

  def getClockAngleDiffDelegate(world: World) = {
    val relativeAngle = world.getCelestialAngle(1F)
    APIDelegate.pushLocation(APIDelegate.absolute())
    val absoluteAngle = world.getCelestialAngle(1F)
    APIDelegate.popLocation()
    (relativeAngle - absoluteAngle).asInstanceOf[Double]
  }

  def preRenderEntityDelegate(e: Entity) = {
    if (e != null) APIDelegate.pushLocation(APIDelegate.position(e))
    ()
  }

  def postRenderEntityDelegate(e: Entity) = {
    if (e != null) APIDelegate.popLocation()
    ()
  }

  def preRenderTileEntityDelegate(te: TileEntity) = {
    if (te != null && te.getPos != null) APIDelegate.pushLocation(APIDelegate.position(te.getPos))
    ()
  }

  def postRenderTileEntityDelegate(te: TileEntity) = {
    if (te != null && te.getPos != null) APIDelegate.popLocation()
    ()
  }

  def preBlockPosLightDelegate(world: World, pos: BlockPos) = {
    val event = new TimeZoneEvents.BlockPosLightEvent.Pre(pos)
    MinecraftForge.EVENT_BUS.post(event)
    world.calculateInitialSkylight()
  }

  def postBlockPosLightDelegate(light: Int, pos: BlockPos) = {
    val event = new TimeZoneEvents.BlockPosLightEvent.Post(pos, light)
    MinecraftForge.EVENT_BUS.post(event)
    event.light
  }

  def getSkylightSubtractedDelegate(world: World) = {
    world.calculateInitialSkylight()
  }

  def processCommandDelegate(sender: ICommandSender) = {
    APIDelegate.popLocation()
    APIDelegate.pushLocation(APIDelegate.position(sender.getPosition))
  }
}
