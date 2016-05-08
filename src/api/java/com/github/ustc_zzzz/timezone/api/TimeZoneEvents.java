package com.github.ustc_zzzz.timezone.api;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public enum TimeZoneEvents
{
    /* nothing. */;

    public static class BlockPosLightEvent extends Event
    {
        public static class Pre extends BlockPosLightEvent
        {
            public Pre(BlockPos pos)
            {
                super(pos, Phase.START);
            }
        }

        public static class Post extends BlockPosLightEvent
        {
            public int light;

            public Post(BlockPos pos, int light)
            {
                super(pos, Phase.END);
                this.light = light;
            }
        }

        public enum Phase
        {
            START, END;
        }

        public final BlockPos pos;

        public final Phase phase;

        public BlockPosLightEvent(BlockPos pos, Phase phase)
        {
            this.pos = pos;
            this.phase = phase;
        }
    }
}
