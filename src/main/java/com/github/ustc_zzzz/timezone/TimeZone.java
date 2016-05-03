package com.github.ustc_zzzz.timezone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod(modid = TimeZone.MODID, version = TimeZone.VERSION, acceptedMinecraftVersions = "[1.8,1.9)", dependencies = "required-after:timezone-core@")
public class TimeZone
{
    public static final String MODID = "timezone";
    public static final String VERSION = "@version@";

    public static final Logger LOGGER = LogManager.getLogger("TimeZone");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // nothing.
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        // nothing.
    }

    // Only for DEBUG
    @SubscribeEvent
    public void move(PlayerTickEvent event)
    {
        TimeZoneAPI.INSTANCE.popLocation();
        TimeZoneAPI.INSTANCE.pushLocation(event.player.posX, event.player.posZ);
    }
}
