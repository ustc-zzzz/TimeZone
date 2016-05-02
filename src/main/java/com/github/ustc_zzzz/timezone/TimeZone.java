package com.github.ustc_zzzz.timezone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TimeZone.MODID, version = TimeZone.VERSION, acceptedMinecraftVersions = "[1.8,1.9)", dependencies = "required-after:timezone-core@")
public class TimeZone
{
    public static final String MODID = "timezone";
    public static final String VERSION = "@version@";

    public static final Logger Logger = LogManager.getLogger("TimeZone");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        // nothing.
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        TimeZone.Logger.info("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        // nothing.
    }
}
