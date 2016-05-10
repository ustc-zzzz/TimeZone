package com.github.ustc_zzzz.timezone.asm;

import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private void loadClass(String name)
    {
        try
        {
            Class.forName(name);
        }
        catch (ClassNotFoundException e)
        {
            ;// nothing.
        }
    }

    @Override
    public Void call() throws Exception
    {
        Logger logger = LogManager.getLogger("TimeZone");
        logger.info("Coremod setup started. ");
        {
            this.loadClass("net.minecraft.world.storage.WorldInfo");
            this.loadClass("net.minecraft.network.play.server.S03PacketTimeUpdate");
            this.loadClass("net.minecraft.client.multiplayer.WorldClient");
            this.loadClass("net.minecraft.client.network.NetHandlerPlayClient");
            this.loadClass("net.minecraft.world.WorldProvider");
            this.loadClass("net.minecraft.command.CommandTime");
            this.loadClass("net.minecraft.client.gui.GuiOverlayDebug");
            this.loadClass("net.minecraft.world.World");
        }
        logger.info("Coremod setup finished. ");
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
