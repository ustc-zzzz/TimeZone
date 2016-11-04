package com.github.ustc_zzzz.timezone.common

import java.util.Arrays
import java.util.concurrent.Callable

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI._

import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object APIDelegate extends API {
  var tickPMeterX = 60D
  var tickPMeterZ = 0D

  protected case class LocationDelegate(x: Double, z: Double) extends Position {
    def getPosX: Int = Math.floor(x).asInstanceOf[Int]
    def getPosZ: Int = Math.floor(z).asInstanceOf[Int]
    def getX: Double = x
    def getZ: Double = z
  }

  protected case object LocationRelative extends Position {
    def getPosX: Int = Math.floor(topX).asInstanceOf[Int]
    def getPosZ: Int = Math.floor(topZ).asInstanceOf[Int]
    def getX: Double = topX
    def getZ: Double = topZ
    override def toString = f"LocationRelative($getX,$getZ)"
  }

  protected var pointer = 0

  protected var xStack = new Array[Double](64)
  protected var zStack = new Array[Double](64)

  protected var thread = new Array[Long](64)

  protected def doWith[T](x: Double, z: Double, f: () => T): T = try { push(x, z); f() } finally pop

  protected def dt(dx: Double, dz: Double): Long = Math.round(tickPMeterX * dx + tickPMeterZ * dz)

  protected def dtr(): Long = Math.round(tickPMeterX * topX + tickPMeterZ * topZ)

  protected def dtp(l: Position): Long = Math.round(tickPMeterX * l.getX + tickPMeterZ * l.getZ)

  protected def pop(): Position = synchronized {
    val id = Thread.currentThread.getId
    def getPointer(p: Int): Int = if (thread(p) == id || p == 0) p else getPointer(p - 1)
    val p = getPointer(pointer)
    val position = LocationDelegate(xStack(p), zStack(p))
    if (p > 0) {
      pointer -= 1
      for (i <- p to pointer) {
        xStack(i) = xStack(i + 1)
        zStack(i) = zStack(i + 1)
        thread(i) = thread(i + 1)
      }
    }
    position
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

  protected def topX(): Double = {
    val id = Thread.currentThread.getId
    def getFromPointer(p: Int): Double = if (thread(p) == id || p == 0) xStack(p) else getFromPointer(p - 1)
    getFromPointer(pointer)
  }

  protected def topZ(): Double = {
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

  override def getTime(l: Position, world: World) = world.getWorldTime - dtr + dtp(l)

  override def setRelativeTime(world: World, relativeTime: Long) = world.setWorldTime(relativeTime)

  override def setAbsoluteTime(world: World, absoluteTime: Long) = world.setWorldTime(absoluteTime + dtr)

  override def setTime(l: Position, world: World, time: Long) = world.setWorldTime(time + dtr - dtp(l))

  override def timeDiffFromRelativeToAbsolute() = dtr

  override def timeDiffFromRelative(lBase: Position) = dtr - dtp(lBase)

  override def timeDiffToAbsoulte(l: Position) = dtp(l)

  override def timeDiff(l: Position, lBase: Position) = dtp(l) - dtp(lBase)

  override def doWithLocation(l: Position, r: Runnable) = doWith(l.getX, l.getZ, r.run)

  override def doWithLocation[E](l: Position, c: Callable[E]) = doWith(l.getX, l.getZ, c.call)

  override def pushLocation(l: Position) = push(l.getX, l.getZ)

  override def popLocation() = pop

  override def stackSize() = 1 + pointer
}