package com.github.ustc_zzzz.timezone.common;

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler
{
    public static final EventHandler INSTANCE = new EventHandler();

    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        if (!event.left.isEmpty())
        {
            event.left.add("");
            event.left.add("[TimeZone]");
            event.left.add("LocationX: " + (float) TimeZoneAPI.INSTANCE.getLocationX());
            event.left.add("LocationZ: " + (float) TimeZoneAPI.INSTANCE.getLocationZ());
            event.left.add("TimeDiff: " + (TimeZoneAPI.INSTANCE.getRelativeTime(Minecraft.getMinecraft().theWorld)
                    - TimeZoneAPI.INSTANCE.getAbsoluteTime(Minecraft.getMinecraft().theWorld)));
        }
    }
}
