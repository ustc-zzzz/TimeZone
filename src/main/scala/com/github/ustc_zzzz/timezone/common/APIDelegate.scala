package com.github.ustc_zzzz.timezone.common

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI

import net.minecraft.world.World
import java.util.concurrent.Callable
import java.util.Arrays

class APIDelegate extends TimeZoneAPI.API {
  var pointer = 0

  var xStack = new Array[Double](64)
  var zStack = new Array[Double](64)

  var thread = new Array[Long](64)

  private def dt(dx: Double, dz: Double) = Math.round(APIDelegate.tickPMeterX * dx + APIDelegate.tickPMeterZ * dz)

  override def relativeTimeToAbsolute(relativeTime: Long) = relativeTime - dt(getLocationX, getLocationZ)

  override def absoluteTimeToRelative(absoluteTime: Long) = absoluteTime + dt(getLocationX, getLocationZ)

  override def getRelativeTime(world: World) = world getWorldTime

  override def getAbsoluteTime(world: World) = relativeTimeToAbsolute(world getWorldTime)

  override def setRelativeTime(world: World, relativeTime: Long) = world setWorldTime relativeTime

  override def setAbsoluteTime(world: World, absoluteTime: Long) = world setWorldTime absoluteTimeToRelative(absoluteTime)

  override def getTimeDiffFromBase(xBase: Double, zBase: Double, world: World) = dt(getLocationX - xBase, getLocationZ - zBase)

  override def relativeTimeToAbsoluteWithLocation(x: Double, z: Double, relativeTime: Long) = relativeTime - dt(x, z)

  override def absoluteTimeToRelativeWithLocation(x: Double, z: Double, absoluteTime: Long) = absoluteTime + dt(x, z)

  override def getRelativeTimeWithLocation(x: Double, z: Double, world: World) = getRelativeTime(world) - getTimeDiffFromBase(x, z, world)

  override def setRelativeTimeWithLocation(x: Double, z: Double, world: World, relativeTime: Long) = setRelativeTime(world, relativeTime + getTimeDiffFromBase(x, z, world))

  override def getTimeDiffFromBaseWithLocation(x: Double, z: Double, xBase: Double, zBase: Double, world: World) = dt(x - xBase, z - zBase)

  override def doWithLocation(x: Double, z: Double, runnable: Runnable) = { pushLocation(x, z); runnable.run; popLocation }

  override def doWithLocation[E](x: Double, z: Double, callable: Callable[E]) = try { pushLocation(x, z); callable.call } finally popLocation

  override def doWithPosLocation(x: Int, z: Int, runnable: Runnable) = { pushPosLocation(x, z); runnable.run; popPosLocation }

  override def doWithPosLocation[E](x: Int, z: Int, callable: Callable[E]) = try { pushPosLocation(x, z); callable.call } finally popPosLocation

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

  override def stackSize() = 1 + pointer
}

object APIDelegate {
  var tickPMeterX = 60D
  var tickPMeterZ = 0D
}