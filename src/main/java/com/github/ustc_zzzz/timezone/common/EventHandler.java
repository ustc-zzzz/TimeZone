package com.github.ustc_zzzz.timezone.common;

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

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
            event.left.add(String.format("RelativeTime: %d",
                    TimeZoneAPI.INSTANCE.getRelativeTime(Minecraft.getMinecraft().theWorld)));
            event.left.add(String.format("LocationX: %.5f", TimeZoneAPI.INSTANCE.getLocationX()));
            event.left.add(String.format("LocationZ: %.5f", TimeZoneAPI.INSTANCE.getLocationZ()));
            event.left.add(String.format("TimeDiff: %d",
                    TimeZoneAPI.INSTANCE.getTimeDiffFromBase(0, 0, Minecraft.getMinecraft().theWorld)));
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player != null)
        {
            switch (event.phase)
            {
            case START:
                TimeZoneAPI.INSTANCE.pushLocation(player.posX, player.posZ);
                break;
            case END:
                TimeZoneAPI.INSTANCE.popLocation();
                break;
            }
        }
    }
}
