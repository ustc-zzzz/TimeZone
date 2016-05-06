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
        long relativeTimeToAbsolute(long relativeTime);

        long absoluteTimeToRelative(long absoluteTime);

        long getRelativeTime(World world);

        long getAbsoluteTime(World world);

        void setRelativeTime(World world, long relativeTime);

        void setAbsoluteTime(World world, long absoluteTime);

        long getTimeDiffFromBase(double xBase, double zBase, World world);

        long relativeTimeToAbsoluteWithLocation(double x, double z, long relativeTime);

        long absoluteTimeToRelativeWithLocation(double x, double z, long absoluteTime);

        long getRelativeTimeWithLocation(double x, double z, World world);

        void setRelativeTimeWithLocation(double x, double z, World world, long relativeTime);

        long getTimeDiffFromBaseWithLocation(double x, double z, double xBase, double zBase, World world);

        void doWithLocation(double x, double z, Runnable runnable);

        <E> E doWithLocation(double x, double z, Callable<E> callable) throws Exception;

        void doWithPosLocation(int x, int z, Runnable runnable);

        <E> E doWithPosLocation(int x, int z, Callable<E> callable) throws Exception;

        double getLocationX();

        double getLocationZ();

        int getPosLocationX();

        int getPosLocationZ();

        void pushLocation(double x, double z);

        void pushPosLocation(int x, int z);

        void popLocation();

        void popPosLocation();

        int stackSize();
    }
}
