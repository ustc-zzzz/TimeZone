package com.github.ustc_zzzz.timezone.api;

import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum TimeZoneAPI
{
    /* nothing. */;

    @Nullable
    public static final API INSTANCE;

    static
    {
        API api;
        try
        {
            Class<?> clz = Class.forName("com.github.ustc_zzzz.timezone.common.APIDelegate$");
            api = API.class.cast(clz.getField("MODULE$").get(null));
        }
        catch (Exception e)
        {
            api = null;
        }
        INSTANCE = api;
    }

    /**
     * An interface for position of X axis and Z axis.
     */
    public static interface Position
    {
        double getX();

        double getZ();

        int getPosX();

        int getPosZ();
    }

    /**
     * API interface, use field {@link TimeZoneAPI#INSTANCE} to get the
     * instance. There is a thread-specific stack which stores several
     * positions. When either {@link net.minecraft.world.World#getWorldTime}
     * method or {@link net.minecraft.world.World#setWorldTime} method is
     * invoked, it is related the local time of the position on the top of the
     * stack of the specific thread. Several methods is related to the
     * operations of push, and pop. Besides, the API also provides several
     * convenient methods which deal with time difference.
     */
    public static interface API
    {
        /**
         * Relative position
         * 
         * @return a position synchronized with the stack top
         */
        Position relative();

        /**
         * Absolute position
         * 
         * @return position(0, 0)
         */
        Position absolute();

        /**
         * Integer position, equivalent to position(x + 0.5D, z + 0.5D)
         * 
         * @return integer position(x, z), or position(x + 0.5D, z + 0.5D)
         */
        Position position(int x, int z);

        /**
         * Double-precision floating-point position
         * 
         * @return position(x, z)
         */
        Position position(double x, double z);

        /**
         * Position by {@link net.minecraft.util.math.BlockPos} class
         * 
         * @return position(pos.getX, pos.getZ)
         */
        Position position(BlockPos pos);

        /**
         * Position by {@link net.minecraft.entity.Entity} class
         * 
         * @return position(entity.posX, entity.posZ)
         */
        Position position(Entity entity);

        /**
         * Relative time from the specific world
         * 
         * @return relative time
         */
        long getRelativeTime(World world);

        /**
         * Absolute time from the specific world
         * 
         * @return absolute time
         */
        long getAbsoluteTime(World world);

        /**
         * Time from the specific world and the specific position
         * 
         * @return the specific time
         */
        long getTime(Position location, World world);

        /**
         * Relative time from the specific world
         */
        void setRelativeTime(World world, long relativeTime);

        /**
         * Absolute time from the specific world
         */
        void setAbsoluteTime(World world, long absoluteTime);

        /**
         * Time from the specific world and the specific position
         */
        void setTime(Position location, World world, long time);

        /**
         * the relative time subtract the absolute time
         * 
         * @return getRelativeTime(world) - getAbsoluteTime(world)
         */
        long timeDiffFromRelativeToAbsolute();

        /**
         * the relative time subtract the time from the specific position
         * 
         * @return getRelativeTime - getTime(locationBase)
         */
        long timeDiffFromRelative(Position locationBase);

        /**
         * the time from the specific position subtract the absolute time
         * 
         * @return getTime(location) - getAbsoluteTime()
         */
        long timeDiffToAbsoulte(Position location);

        /**
         * the time from the specific position subtract another
         * 
         * @return getTime(location) - getTime(locationBase)
         */
        long timeDiff(Position location, Position locationBase);

        /**
         * Equivalent to: pushLocation; runnable; popLocation
         */
        void doWithLocation(Position location, Runnable runnable);

        /**
         * Equivalent to: pushPosisionLocation; callable; popPosisionLocation
         * 
         * @return the return value of the function
         * @throws Exception exception from the callable
         */
        <E> E doWithLocation(Position location, Callable<E> callable) throws Exception;

        /**
         * Push a position to the stack, matched with {@link #popLocation}
         */
        void pushLocation(Position location);

        /**
         * Pop a position from the stack, matched with {@link #pushLocation}}
         */
        void popLocation();

        /**
         * Stack size of all threads
         * 
         * @return stack size
         */
        int stackSize();
    }
}
