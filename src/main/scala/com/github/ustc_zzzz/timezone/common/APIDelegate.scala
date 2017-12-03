package com.github.ustc_zzzz.timezone.common

import java.util
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicInteger

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI._
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object APIDelegate extends API {
  var tickPerMeterX = 60D
  var tickPerMeterZ = 0D

  case class LocationDelegate(x: Double, z: Double) extends Position {
    def getPosX: Int = Math.floor(x).asInstanceOf[Int]

    def getPosZ: Int = Math.floor(z).asInstanceOf[Int]

    def getX: Double = x

    def getZ: Double = z
  }

  case object LocationRelative extends Position {
    def getPosX: Int = Math.floor(topX()).asInstanceOf[Int]

    def getPosZ: Int = Math.floor(topZ()).asInstanceOf[Int]

    def getX: Double = topX()

    def getZ: Double = topZ()

    override def toString = f"LocationRelative($getX,$getZ)"
  }

  private class LocationStack {
    var xStack = new Array[Double](64)
    var zStack = new Array[Double](64)

    var capacity = 64
    var pointer = 0
  }

  private var size = new AtomicInteger(1)

  private val locations = new ThreadLocal[LocationStack] {
    override def initialValue = new LocationStack
  }

  @inline
  private def dt(dx: Double, dz: Double): Long = Math.round(tickPerMeterX * dx + tickPerMeterZ * dz)

  @inline
  private def dtRelative(): Long = Math.round(tickPerMeterX * topX + tickPerMeterZ * topZ)

  @inline
  private def dtPosition(l: Position): Long = Math.round(tickPerMeterX * l.getX + tickPerMeterZ * l.getZ)

  @inline
  private def doWith[T](x: Double, z: Double, f: () => T): T = try {
    push(x, z)
    f()
  } finally pop()

  @inline
  private def pop(): Position = {
    val location = locations.get
    val locationPointer = location.pointer
    if (locationPointer > 0) {
      location.pointer = locationPointer - 1
      size.decrementAndGet
    }
    LocationDelegate(location.xStack(locationPointer), location.zStack(locationPointer))
  }

  @inline
  private def push(x: Double, z: Double): Unit = {
    val location = locations.get
    val locationPointer = location.pointer + 1
    if (locationPointer >= location.capacity) {
      location.xStack = util.Arrays.copyOf(location.xStack, locationPointer + 32)
      location.zStack = util.Arrays.copyOf(location.zStack, locationPointer + 32)
    }
    location.xStack(locationPointer) = x
    location.zStack(locationPointer) = z
    location.pointer = locationPointer
    size.incrementAndGet
  }

  @inline
  private def topX(): Double = {
    val location = locations.get
    location.xStack(location.pointer)
  }

  @inline
  private def topZ(): Double = {
    val location = locations.get
    location.zStack(location.pointer)
  }

  override def absolute(): Position = LocationDelegate(0D, 0D)

  override def relative(): Position = LocationRelative

  override def position(entity: Entity) = LocationDelegate(entity.posX, entity.posZ)

  override def position(pos: BlockPos) = LocationDelegate(pos.getX, pos.getZ)

  override def position(x: Double, z: Double) = LocationDelegate(x, z)

  override def position(x: Int, z: Int) = LocationDelegate(x + 0.5D, z + 0.5D)

  override def getRelativeTime(world: World) = world.getWorldTime

  override def getAbsoluteTime(world: World) = world.getWorldTime - dtRelative

  override def getTime(l: Position, world: World) = world.getWorldTime - dtRelative + dtPosition(l)

  override def setRelativeTime(world: World, relativeTime: Long) = world.setWorldTime(relativeTime)

  override def setAbsoluteTime(world: World, absoluteTime: Long) = world.setWorldTime(absoluteTime + dtRelative)

  override def setTime(l: Position, world: World, time: Long) = world.setWorldTime(time + dtRelative - dtPosition(l))

  override def timeDiffFromRelativeToAbsolute() = dtRelative()

  override def timeDiffFromRelative(lBase: Position) = dtRelative - dtPosition(lBase)

  override def timeDiffToAbsolute(l: Position) = dtPosition(l)

  override def timeDiff(l: Position, lBase: Position) = dtPosition(l) - dtPosition(lBase)

  override def doWithLocation(l: Position, r: Runnable) = doWith(l.getX, l.getZ, r.run)

  override def doWithLocation[E](l: Position, c: Callable[E]) = doWith(l.getX, l.getZ, c.call)

  override def pushLocation(l: Position) = push(l.getX, l.getZ)

  override def popLocation() = pop()

  override def stackSize() = size.get
}