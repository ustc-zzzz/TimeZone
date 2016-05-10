package com.github.ustc_zzzz.timezone.asm;

import java.util.Arrays;
import java.util.Map;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

import org.apache.logging.log4j.LogManager;

import com.google.common.eventbus.EventBus;

public class TimeZoneModContainer extends DummyModContainer implements IFMLCallHook
{
    private static final ModMetadata METADATA = new ModMetadata();

    static
    {
        METADATA.modId = "timezone-core";
        METADATA.name = "TimeZone Core";
        METADATA.version = "@version@";
        METADATA.authorList = Arrays.asList("ustc_zzzz");
        METADATA.description = "TimeZone mod core, as the pre-loading mod.";
        METADATA.credits = "Mojang AB, and the Forge and FML guys. ";
    }

    public TimeZoneModContainer()
    {
        super(TimeZoneModContainer.METADATA);
    }

    @Override
    public Void call() throws Exception
    {
        LogManager.getLogger("TimeZone").info("Coremod loaded, version " + METADATA.version + ". ");
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
