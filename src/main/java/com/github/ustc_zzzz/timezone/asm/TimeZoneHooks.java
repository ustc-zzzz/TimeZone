package com.github.ustc_zzzz.timezone.asm;

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI;

import net.minecraft.world.storage.WorldInfo;

public class TimeZoneHooks
{
    public static long getWorldTimeDelegete(long time, final WorldInfo worldInfo)
    {
        return time + TimeZoneAPI.INSTANCE.getTimeDiffFromBase(worldInfo.getSpawnX(), worldInfo.getSpawnZ(), null);
    }

    public static long setWorldTimeDelegete(long time, final WorldInfo worldInfo)
    {
        return time - TimeZoneAPI.INSTANCE.getTimeDiffFromBase(worldInfo.getSpawnX(), worldInfo.getSpawnZ(), null);
    }
}