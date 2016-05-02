package com.github.ustc_zzzz.timezone.asm;

import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

public class TimeZoneModContainer extends DummyModContainer implements IFMLCallHook
{
    public TimeZoneModContainer()
    {
        super(TimeZoneModContainer.newModMetadata());
    }

    public static ModMetadata newModMetadata()
    {
        ModMetadata meta = new ModMetadata();
        meta.modId = "timezone-core";
        meta.name = "TimeZone Core";
        meta.version = "@version@";
        meta.authorList = Arrays.asList("ustc_zzzz");
        meta.description = "TimeZone mod core, as the pre-loading mod.";
        meta.credits = "Mojang AB, and the Forge and FML guys. ";
        return meta;
    }

    @Override
    public Void call() throws Exception
    {
        LogManager.getLogger("TimeZone").info("Processing coremod setup. ");
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // nothing.
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        return true;
    }
}
