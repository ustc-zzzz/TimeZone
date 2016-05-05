package com.github.ustc_zzzz.timezone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ustc_zzzz.timezone.common.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
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

    @Instance(TimeZone.MODID)
    public static TimeZone instance;

    @SidedProxy(serverSide = "com.github.ustc_zzzz.timezone.common.CommonProxy", clientSide = "com.github.ustc_zzzz.timezone.client.ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    // Only for DEBUG
    @SubscribeEvent
    public void move(PlayerTickEvent event)
    {
        // TimeZoneAPI.INSTANCE.popLocation();
        // TimeZoneAPI.INSTANCE.pushLocation(event.player.posX,
        // event.player.posZ);
    }
}
