package com.github.ustc_zzzz.timezone.common

import java.util.Arrays
import java.util.concurrent.Callable

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI

import net.minecraft.world.World
import com.github.ustc_zzzz.timezone.api.TimeZoneAPI.Position
import net.minecraft.util.BlockPos
import net.minecraft.entity.Entity

object APIDelegate extends TimeZoneAPI.API {
  case class ILocationDelegate(x: Double, z: Double) extends TimeZoneAPI.Position {
    def getPosX: Int = Math.floor(x).asInstanceOf[Int]
    def getPosZ: Int = Math.floor(z).asInstanceOf[Int]
    def getX: Double = x
    def getZ: Double = z
  }

  case object ILocationRelative extends TimeZoneAPI.Position {
    def getPosX: Int = getPosLocationX
    def getPosZ: Int = getPosLocationZ
    def getX: Double = getLocationX
    def getZ: Double = getLocationZ
  }

  var tickPMeterX = 60D
  var tickPMeterZ = 0D

  protected var pointer = 0

  protected var xStack = new Array[Double](64)
  protected var zStack = new Array[Double](64)

  protected var thread = new Array[Long](64)

  protected def dt(dx: Double, dz: Double) = Math.round(APIDelegate.tickPMeterX * dx + APIDelegate.tickPMeterZ * dz)

  override def absolute() = ILocationDelegate(0D, 0D)

  override def relative() = ILocationRelative

  override def position(entity: Entity) = ILocationDelegate(entity.posX, entity.posZ)

  override def position(pos: BlockPos) = ILocationDelegate(pos.getX, pos.getZ)

  override def position(x: Double, z: Double) = ILocationDelegate(x, z)

  override def position(x: Int, z: Int) = ILocationDelegate(x + 0.5D, z + 0.5D)

  override def getRelativeTime(world: World) = world getWorldTime

  override def getAbsoluteTime(world: World) = world.getWorldTime - timeDiffFromRelativeToAbsolute

  override def getTime(location: TimeZoneAPI.Position, world: World) = world.getWorldTime - timeDiffFromRelative(location)

  override def setRelativeTime(world: World, relativeTime: Long) = world setWorldTime relativeTime

  override def setAbsoluteTime(world: World, absoluteTime: Long) = world setWorldTime (absoluteTime + timeDiffFromRelativeToAbsolute)

  override def setTime(location: TimeZoneAPI.Position, world: World, absoluteTime: Long) = world setWorldTime (absoluteTime + timeDiffFromRelative(location))

  override def timeDiffFromRelativeToAbsolute() = dt(getLocationX, getLocationZ)

  override def timeDiffFromRelative(locationBase: TimeZoneAPI.Position) = dt(getLocationX - locationBase.getX, getLocationZ - locationBase.getZ)

  override def timeDiffToAbsoulte(location: TimeZoneAPI.Position) = dt(location.getX, location.getZ)

  override def timeDiff(location: TimeZoneAPI.Position, locationBase: TimeZoneAPI.Position) = dt(location.getX - locationBase.getX, location.getZ - locationBase.getZ)

  override def doWithLocation(x: Double, z: Double, runnable: Runnable) = synchronized {
    pushLocation(x, z)
    runnable.run
    popLocation
  }

  override def doWithLocation[E](x: Double, z: Double, callable: Callable[E]) = synchronized {
    try {
      pushLocation(x, z)
      callable.call
    } finally popLocation
  }

  override def doWithPosLocation(x: Int, z: Int, runnable: Runnable) = synchronized {
    pushPosLocation(x, z)
    runnable.run
    popPosLocation
  }

  override def doWithPosLocation[E](x: Int, z: Int, callable: Callable[E]) = synchronized {
    try {
      pushPosLocation(x, z)
      callable.call
    } finally popPosLocation
  }

  override def doWithPositionLocation(location: TimeZoneAPI.Position, runnable: Runnable) = synchronized {
    pushPositionLocation(location)
    runnable.run
    popPositionLocation
  }

  override def doWithPositionLocation[E](location: TimeZoneAPI.Position, callable: Callable[E]) = synchronized {
    try {
      pushPositionLocation(location)
      callable.call
    } finally popPositionLocation
  }

  override def getLocationX() = synchronized {
    val id = Thread.currentThread.getId
    def getFromPointer(p: Int): Double = if (thread(p) == id || p == 0) xStack(p) else getFromPointer(p - 1)
    getFromPointer(pointer)
  }

  override def getLocationZ() = synchronized {
    val id = Thread.currentThread.getId
    def getFromPointer(p: Int): Double = if (thread(p) == id || p == 0) zStack(p) else getFromPointer(p - 1)
    getFromPointer(pointer)
  }

  override def pushLocation(x: Double, z: Double) = synchronized {
    val id = Thread.currentThread.getId
    pointer += 1
    if (pointer >= thread.length) {
      xStack = Arrays.copyOf(xStack, pointer + 32)
      zStack = Arrays.copyOf(zStack, pointer + 32)
      thread = Arrays.copyOf(thread, pointer + 32)
    }
    xStack(pointer) = x
    zStack(pointer) = z
    thread(pointer) = id
    ()
  }

  override def popLocation() = synchronized {
    val id = Thread.currentThread.getId
    def getPointer(p: Int): Int = if (thread(p) == id || p == 0) p else getPointer(p - 1)
    val p = getPointer(pointer)
    if (p > 0) {
      pointer -= 1
      for (i <- p to pointer) {
        xStack(i) = xStack(i + 1)
        zStack(i) = zStack(i + 1)
        thread(i) = thread(i + 1)
      }
    }
    ()
  }

  override def getPosLocationX() = synchronized(Math.floor(getLocationX).asInstanceOf[Int])

  override def getPosLocationZ() = synchronized(Math.floor(getLocationZ).asInstanceOf[Int])

  override def pushPosLocation(x: Int, z: Int) = synchronized(pushLocation(x + 0.5D, z + 0.5D))

  override def popPosLocation() = synchronized(popLocation)

  override def getPositionLocation() = ILocationRelative

  override def pushPositionLocation(location: TimeZoneAPI.Position) = synchronized(pushLocation(location.getX, location.getZ))

  override def popPositionLocation() = synchronized(popLocation)

  override def stackSize() = 1 + pointer
}