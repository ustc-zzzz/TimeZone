package com.github.ustc_zzzz.timezone.api;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public enum TimeZoneAPI
{
    /* nothing. */;

    public static final API INSTANCE;

    static
    {
        try
        {
            INSTANCE = API.class.cast(
                    Class.forName("com.github.ustc_zzzz.timezone.common.APIDelegate$").getField("MODULE$").get(null));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static interface Position
    {
        double getX();

        double getZ();

        int getPosX();

        int getPosZ();
    }

    public static interface API
    {
        Position relative();

        Position absolute();

        Position position(int x, int z);

        Position position(double x, double z);

        Position position(BlockPos pos);

        Position position(Entity entity);

        long getRelativeTime(World world);

        long getAbsoluteTime(World world);

        long getTime(Position location, World world);

        void setRelativeTime(World world, long relativeTime);

        void setAbsoluteTime(World world, long absoluteTime);

        void setTime(Position location, World world, long time);

        long timeDiffFromRelativeToAbsolute();

        long timeDiffFromRelative(Position locationBase);

        long timeDiffToAbsoulte(Position location);

        long timeDiff(Position location, Position locationBase);

        void doWithLocation(double x, double z, Runnable runnable);

        <E> E doWithLocation(double x, double z, Callable<E> callable) throws Exception;

        void doWithPosLocation(int x, int z, Runnable runnable);

        <E> E doWithPosLocation(int x, int z, Callable<E> callable) throws Exception;

        void doWithPositionLocation(Position location, Runnable runnable);

        <E> E doWithPositionLocation(Position location, Callable<E> callable) throws Exception;

        double getLocationX();

        double getLocationZ();

        void pushLocation(double x, double z);

        void popLocation();

        int getPosLocationX();

        int getPosLocationZ();

        void pushPosLocation(int x, int z);

        void popPosLocation();

        Position getPositionLocation();

        void pushPositionLocation(Position location);

        void popPositionLocation();

        int stackSize();
    }
}
