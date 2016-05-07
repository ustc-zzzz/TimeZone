package com.github.ustc_zzzz.timezone.asm;

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI;
import com.github.ustc_zzzz.timezone.api.TimeZoneEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;

public class TimeZoneHooks
{
    public static long getSyncWorldTimeDelegate(long time)
    {
        return TimeZoneAPI.INSTANCE.absoluteTimeToRelative(time);
    }

    public static long setSyncWorldTimeDelegate(long time)
    {
        return TimeZoneAPI.INSTANCE.relativeTimeToAbsolute(time);
    }

    public static long getWorldTimeDelegete(long time, final WorldInfo worldInfo)
    {
        return time + TimeZoneAPI.INSTANCE.getTimeDiffFromBase(worldInfo.getSpawnX(), worldInfo.getSpawnZ(), null);
    }

    public static long setWorldTimeDelegete(long time, final WorldInfo worldInfo)
    {
        return time - TimeZoneAPI.INSTANCE.getTimeDiffFromBase(worldInfo.getSpawnX(), worldInfo.getSpawnZ(), null);
    }

    public static void handleTimeUpdateDelegate(boolean doDaylightCycle)
    {
        Minecraft.getMinecraft().theWorld.getGameRules().setOrCreateGameRule("doDaylightCycle",
                Boolean.toString(doDaylightCycle));
    }

    public static void findChunksForSpawningDelegate(int posX, int posZ)
    {
        TimeZoneAPI.INSTANCE.popPosLocation();
        TimeZoneAPI.INSTANCE.pushPosLocation(posX, posZ);
    }

    public static void updateEntitiesDelegate(Entity e)
    {
        if (e != null)
        {
            TimeZoneAPI.INSTANCE.popLocation();
            TimeZoneAPI.INSTANCE.pushLocation(e.posX, e.posZ);
        }
    }

    public static void updateTileEntitiesDelegate(TileEntity te)
    {
        if (te != null && te.getPos() != null)
        {
            TimeZoneAPI.INSTANCE.popLocation();
            TimeZoneAPI.INSTANCE.pushLocation(te.getPos().getX(), te.getPos().getZ());
        }
    }

    public static void preBlockPosLightDelegate(World world, BlockPos pos)
    {
        TimeZoneAPI.INSTANCE.pushLocation(pos.getX(), pos.getZ());
        world.calculateInitialSkylight();
        TimeZoneEvents.BlockPosLightEvent.Pre e = new TimeZoneEvents.BlockPosLightEvent.Pre(pos);
        MinecraftForge.EVENT_BUS.post(e);
    }

    public static int postBlockPosLightDelegate(int light, BlockPos pos)
    {
        TimeZoneEvents.BlockPosLightEvent.Post e = new TimeZoneEvents.BlockPosLightEvent.Post(pos, light);
        MinecraftForge.EVENT_BUS.post(e);
        TimeZoneAPI.INSTANCE.popLocation();
        return e.light;
    }

    public static void getSkylightSubtractedDelegate(World world)
    {
        world.calculateInitialSkylight();
    }

    public static void preCommand(ICommandSender sender)
    {
        TimeZoneAPI.INSTANCE.pushPosLocation(sender.getPosition().getX(), sender.getPosition().getZ());
    }

    public static void postCommand()
    {
        TimeZoneAPI.INSTANCE.popPosLocation();
    }
}
