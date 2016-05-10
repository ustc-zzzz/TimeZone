package com.github.ustc_zzzz.timezone.common;

import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        Property tickPerChunkX = config.get(Configuration.CATEGORY_GENERAL, "tickPerChunkX", 60D,
                StatCollector.translateToLocal("timezone.configgui.tickPerChunkX.tooltip"));
        tickPerChunkX.setValue(MathHelper.clamp_double(tickPerChunkX.getDouble(), -12000D, 12000D));
        Property tickPerChunkZ = config.get(Configuration.CATEGORY_GENERAL, "tickPerChunkZ", 0D,
                StatCollector.translateToLocal("timezone.configgui.tickPerChunkZ.tooltip"));
        tickPerChunkZ.setValue(MathHelper.clamp_double(tickPerChunkZ.getDouble(), -12000D, 12000D));
        APIDelegate.tickPMeterX = tickPerChunkX.getDouble() / 16;
        APIDelegate.tickPMeterZ = tickPerChunkZ.getDouble() / 16;
        config.save();
    }

    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        // nothing.
    }
}
