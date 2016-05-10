package com.github.ustc_zzzz.timezone.common;

import java.util.Arrays;
import java.util.concurrent.Callable;

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI;

import net.minecraft.world.World;

public class APIDelegate implements TimeZoneAPI.API
{
    private double[] xStack = new double[64];
    private double[] zStack = new double[64];

    private long[] thread = new long[64];

    private int pointer = 0;

    static double tickPMeterX = 500, tickPMeterZ = 500;

    public APIDelegate()
    {
        xStack[0] = zStack[0] = 0;
        thread[0] = 0;
    }

    @Override
    public long relativeTimeToAbsolute(long relativeTime)
    {
        return relativeTime - Math.round(tickPMeterX * this.getLocationX() + tickPMeterZ * this.getLocationZ());
    }

    @Override
    public long absoluteTimeToRelative(long absoluteTime)
    {
        return absoluteTime + Math.round(tickPMeterX * this.getLocationX() + tickPMeterZ * this.getLocationZ());
    }

    @Override
    public long getRelativeTime(World world)
    {
        return world.getWorldTime();
    }

    @Override
    public long getAbsoluteTime(World world)
    {
        return this.relativeTimeToAbsolute(world.getWorldTime());
    }

    @Override
    public void setRelativeTime(World world, long relativeTime)
    {
        world.setWorldTime(relativeTime);
    }

    @Override
    public void setAbsoluteTime(World world, long absoluteTime)
    {
        world.setWorldTime(this.absoluteTimeToRelative(absoluteTime));
    }

    @Override
    public long getTimeDiffFromBase(double xBase, double zBase, World world)
    {
        return Math.round(tickPMeterX * (this.getLocationX() - xBase) + tickPMeterZ * (this.getLocationZ() - zBase));
    }

    @Override
    public long relativeTimeToAbsoluteWithLocation(double x, double z, long relativeTime)
    {
        return relativeTime - Math.round(tickPMeterX * x) - Math.round(tickPMeterZ * z);
    }

    @Override
    public long absoluteTimeToRelativeWithLocation(double x, double z, long absoluteTime)
    {
        return absoluteTime + Math.round(tickPMeterX * x) + Math.round(tickPMeterZ * z);
    }

    @Override
    public long getRelativeTimeWithLocation(double x, double z, World world)
    {
        return this.getRelativeTime(world)
                - Math.round(tickPMeterX * (this.getLocationX() - x) + tickPMeterZ * (this.getLocationZ() - x));
    }

    @Override
    public void setRelativeTimeWithLocation(double x, double z, World world, long relativeTime)
    {
        this.setRelativeTime(
                world,
                relativeTime
                        + Math.round(tickPMeterX * (this.getLocationX() - x) + tickPMeterZ * (this.getLocationZ() - x)));
    }

    @Override
    public long getTimeDiffFromBaseWithLocation(double x, double z, double xBase, double zBase, World world)
    {
        return Math.round(tickPMeterX * (x - xBase) + tickPMeterZ * (z - zBase));
    }

    @Override
    public void doWithLocation(double x, double z, Runnable runnable)
    {
        this.pushLocation(x, z);
        runnable.run();
        this.popLocation();
    }

    @Override
    public <E> E doWithLocation(double x, double z, Callable<E> callable) throws Exception
    {
        try
        {
            this.pushLocation(x, z);
            return callable.call();
        }
        finally
        {
            this.popLocation();
        }
    }

    @Override
    public void doWithPosLocation(int x, int z, Runnable runnable)
    {
        this.pushPosLocation(x, z);
        runnable.run();
        this.popPosLocation();
    }

    @Override
    public <E> E doWithPosLocation(int x, int z, Callable<E> callable) throws Exception
    {
        try
        {
            this.pushPosLocation(x, z);
            return callable.call();
        }
        finally
        {
            this.popPosLocation();
        }
    }

    @Override
    public synchronized double getLocationX()
    {
        long id = Thread.currentThread().getId();
        for (int i = pointer; i > 0; --i)
        {
            if (thread[i] == id)
            {
                return xStack[i];
            }
        }
        return xStack[0];
    }

    @Override
    public synchronized double getLocationZ()
    {
        long id = Thread.currentThread().getId();
        for (int i = pointer; i > 0; --i)
        {
            if (thread[i] == id)
            {
                return zStack[i];
            }
        }
        return zStack[0];
    }

    @Override
    public synchronized int getPosLocationX()
    {
        return (int) Math.floor(this.getLocationX());
    }

    @Override
    public synchronized int getPosLocationZ()
    {
        return (int) Math.floor(this.getLocationZ());
    }

    @Override
    public synchronized void pushLocation(double x, double z)
    {
        if (++pointer >= thread.length)
        {
            xStack = Arrays.copyOf(xStack, pointer + 32);
            zStack = Arrays.copyOf(zStack, pointer + 32);
            thread = Arrays.copyOf(thread, pointer + 32);
        }
        xStack[pointer] = x;
        zStack[pointer] = z;
        thread[pointer] = Thread.currentThread().getId();
    }

    @Override
    public synchronized void pushPosLocation(int x, int z)
    {
        this.pushLocation(x + 0.5, z + 0.5);
    }

    @Override
    public synchronized void popLocation()
    {
        long id = Thread.currentThread().getId();
        for (int i = pointer; i > 0; --i)
        {
            if (thread[i] == id)
            {
                while (++i <= pointer)
                {
                    xStack[i - 1] = xStack[i];
                    zStack[i - 1] = zStack[i];
                    thread[i - 1] = thread[i];
                }
                --pointer;
                return;
            }
        }
        return;
    }

    @Override
    public synchronized void popPosLocation()
    {
        this.popLocation();
    }

    @Override
    public synchronized int stackSize()
    {
        return pointer + 1;
    }
}
