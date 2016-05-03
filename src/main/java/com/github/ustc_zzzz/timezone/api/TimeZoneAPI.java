package com.github.ustc_zzzz.timezone.api;

import java.util.concurrent.Callable;

import net.minecraft.world.World;

public enum TimeZoneAPI
{
    /* nothing. */;
    
    public static final API INSTANCE;
    
    static
    {
        try
        {
            INSTANCE = API.class.cast(Class.forName("com.github.ustc_zzzz.timezone.common.APIDelegate").newInstance());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static interface API
    {
        public long relativeTimeToAbsolute(long relativeTime);

        public long absoluteTimeToRelative(long absoluteTime);

        public long getRelativeTime(World world);

        public long getAbsoluteTime(World world);

        public void setRelativeTime(World world, long relativeTime);

        public void setAbsoluteTime(World world, long absoluteTime);

        public long getTimeDiffFromBase(double xBase, double zBase, World world);

        public long relativeTimeToAbsoluteWithLocation(double x, double z, long relativeTime);

        public long absoluteTimeToRelativeWithLocation(double x, double z, long absoluteTime);

        public long getRelativeTimeWithLocation(double x, double z, World world);

        public void setRelativeTimeWithLocation(double x, double z, World world, long relativeTime);

        public long getTimeDifferenceFromBaseWithLocation(double x, double z, double xBase, double zBase, World world);

        public void doWithLocation(double x, double z, Runnable runnable);

        public <E> E doWithLocation(double x, double z, Callable<E> callable) throws Exception;

        public void doWithPosLocation(int x, int z, Runnable runnable);

        public <E> E doWithPosLocation(int x, int z, Callable<E> callable) throws Exception;

        public double getLocationX();

        public double getLocationZ();

        public int getPosLocationX();

        public int getPosLocationZ();

        public void pushLocation(double x, double z);

        public void pushPosLocation(int x, int z);

        public void popLocation();

        public void popPosLocation();
    }
}
