package com.github.ustc_zzzz.timezone.common;

import com.github.ustc_zzzz.timezone.TimeZone;

import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        Property tickPerChunkOfAxisX = config.get(Configuration.CATEGORY_GENERAL, "tickPerChunkOfAxisX", 64D,
                "timezone.configgui.tickPerChunkOfAxisX.tooltip");
        tickPerChunkOfAxisX.setValue(MathHelper.clamp_double(tickPerChunkOfAxisX.getDouble(), -12000D, 12000D));
        Property tickPerChunkOfAxisZ = config.get(Configuration.CATEGORY_GENERAL, "tickPerChunkOfAxisZ", 0D,
                "timezone.configgui.tickPerChunkOfAxisZ.tooltip");
        tickPerChunkOfAxisZ.setValue(MathHelper.clamp_double(tickPerChunkOfAxisZ.getDouble(), -12000D, 12000D));
        APIDelegate.tickPMeterX = tickPerChunkOfAxisX.getDouble() / 16;
        APIDelegate.tickPMeterZ = tickPerChunkOfAxisZ.getDouble() / 16;
        config.save();
    }

    public void init(FMLInitializationEvent event)
    {
        // Only for DEBUG
        FMLCommonHandler.instance().bus().register(TimeZone.instance);
        FMLCommonHandler.instance().bus().register(EventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(EventHandler.INSTANCE);
        // nothing.
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        // nothing.
    }
}
