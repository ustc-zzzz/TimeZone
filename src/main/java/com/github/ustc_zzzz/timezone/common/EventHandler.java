package com.github.ustc_zzzz.timezone.common;

import com.github.ustc_zzzz.timezone.api.TimeZoneAPI;
import com.github.ustc_zzzz.timezone.api.TimeZoneEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler
{
    public static final EventHandler INSTANCE = new EventHandler();

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        if (!event.left.isEmpty())
        {
            long diff = TimeZoneAPI.INSTANCE.getTimeDiffFromBase(0, 0, Minecraft.getMinecraft().theWorld);
            long diffD = Math.round(diff / 24000D);
            int diffT = (int) (diff - diffD * 24000L);
            char diffS = diffT > 0 ? '+' : '-';
            int diffH = Math.abs((int) (diffT / 1000D));
            int diffM = Math.abs((diffT % 1000) * 60 / 1000);
            event.left.add("");
            event.left.add("[TimeZone]");
            event.left.add(String.format("StackSize: %d", TimeZoneAPI.INSTANCE.stackSize()));
            event.left.add(String.format("LocationX: %.5f", TimeZoneAPI.INSTANCE.getLocationX()));
            event.left.add(String.format("LocationZ: %.5f", TimeZoneAPI.INSTANCE.getLocationZ()));
            event.left.add(String.format("TimeDiff: %d, %d Day(s), %c%02d%02d", diff, diffD, diffS, diffH, diffM));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
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

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        // For protection
        switch (event.phase)
        {
        case START:
            TimeZoneAPI.INSTANCE.pushLocation(0D, 0D);
            break;
        case END:
            TimeZoneAPI.INSTANCE.popLocation();
            break;
        }
    }

    @SubscribeEvent
    public void onBlockLight(TimeZoneEvents.BlockPosLightEvent event)
    {
        switch (event.phase)
        {
        case START:
            TimeZoneAPI.INSTANCE.pushPosLocation(event.pos.getX(), event.pos.getZ());
            break;
        case END:
            TimeZoneAPI.INSTANCE.popPosLocation();
            break;
        }
    }
}
