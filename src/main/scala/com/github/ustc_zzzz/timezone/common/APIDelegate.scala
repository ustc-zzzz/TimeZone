package com.github.ustc_zzzz.timezone.common

import java.util.Arrays
import java.util.concurrent.Callable

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI

import net.minecraft.entity.Entity
import net.minecraft.util.BlockPos
import net.minecraft.world.World

object APIDelegate extends TimeZoneAPI.API {
  case class LocationDelegate(x: Double, z: Double) extends TimeZoneAPI.Position {
    def getPosX: Int = Math.floor(x).asInstanceOf[Int]
    def getPosZ: Int = Math.floor(z).asInstanceOf[Int]
    def getX: Double = x
    def getZ: Double = z
  }

  case object LocationRelative extends TimeZoneAPI.Position {
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

  protected def doWith[T](x: Double, z: Double, f: () => T): T = try { push(x, z); f() } finally pop

  protected def dt(dx: Double, dz: Double): Long = Math.round(tickPMeterX * dx + tickPMeterZ * dz)

  protected def dtr(): Long = Math.round(tickPMeterX * topX + tickPMeterZ * topZ)

  protected def dtp(l: TimeZoneAPI.Position): Long = Math.round(tickPMeterX * l.getX + tickPMeterZ * l.getZ)

  protected def pop(): Unit = synchronized {
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

  protected def push(x: Double, z: Double): Unit = synchronized {
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

  protected def topX(): Double = synchronized {
    val id = Thread.currentThread.getId
    def getFromPointer(p: Int): Double = if (thread(p) == id || p == 0) xStack(p) else getFromPointer(p - 1)
    getFromPointer(pointer)
  }

  protected def topZ(): Double = synchronized {
    val id = Thread.currentThread.getId
    def getFromPointer(p: Int): Double = if (thread(p) == id || p == 0) zStack(p) else getFromPointer(p - 1)
    getFromPointer(pointer)
  }

  override def absolute() = LocationDelegate(0D, 0D)

  override def relative() = LocationRelative

  override def position(entity: Entity) = LocationDelegate(entity.posX, entity.posZ)

  override def position(pos: BlockPos) = LocationDelegate(pos.getX, pos.getZ)

  override def position(x: Double, z: Double) = LocationDelegate(x, z)

  override def position(x: Int, z: Int) = LocationDelegate(x + 0.5D, z + 0.5D)

  override def getRelativeTime(world: World) = world.getWorldTime

  override def getAbsoluteTime(world: World) = world.getWorldTime - dtr

  override def getTime(l: TimeZoneAPI.Position, world: World) = world.getWorldTime - dtr + dtp(l)

  override def setRelativeTime(world: World, relativeTime: Long) = world.setWorldTime(relativeTime)

  override def setAbsoluteTime(world: World, absoluteTime: Long) = world.setWorldTime(absoluteTime + dtr)

  override def setTime(l: TimeZoneAPI.Position, world: World, time: Long) = world.setWorldTime(time + dtr - dtp(l))

  override def timeDiffFromRelativeToAbsolute() = dtr

  override def timeDiffFromRelative(lBase: TimeZoneAPI.Position) = dtr - dtp(lBase)

  override def timeDiffToAbsoulte(l: TimeZoneAPI.Position) = dtp(l)

  override def timeDiff(l: TimeZoneAPI.Position, lBase: TimeZoneAPI.Position) = dtp(l) - dtp(lBase)

  override def doWithLocation(x: Double, z: Double, r: Runnable) = doWith(x, z, r.run)

  override def doWithLocation[E](x: Double, z: Double, c: Callable[E]) = doWith(x, z, c.call)

  override def doWithPosLocation(x: Int, z: Int, r: Runnable) = doWith(x + 0.5D, z + 0.5D, r.run)

  override def doWithPosLocation[E](x: Int, z: Int, c: Callable[E]) = doWith(x + 0.5D, z + 0.5D, c.call)

  override def doWithPositionLocation(l: TimeZoneAPI.Position, r: Runnable) = doWith(l.getX, l.getZ, r.run)

  override def doWithPositionLocation[E](l: TimeZoneAPI.Position, c: Callable[E]) = doWith(l.getX, l.getZ, c.call)

  override def getLocationX() = topX()

  override def getLocationZ() = topZ()

  override def pushLocation(x: Double, z: Double) = push(x, z)

  override def popLocation() = pop()

  override def getPosLocationX() = Math.floor(topX()).asInstanceOf[Int]

  override def getPosLocationZ() = Math.floor(topZ()).asInstanceOf[Int]

  override def pushPosLocation(x: Int, z: Int) = push(x + 0.5D, z + 0.5D)

  override def popPosLocation() = pop()

  override def getPositionLocation() = LocationRelative

  override def pushPositionLocation(l: TimeZoneAPI.Position) = push(l.getX, l.getZ)

  override def popPositionLocation() = pop()

  override def stackSize() = 1 + pointer
}