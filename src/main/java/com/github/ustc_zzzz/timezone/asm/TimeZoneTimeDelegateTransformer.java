package com.github.ustc_zzzz.timezone.asm;

import net.minecraft.launchwrapper.IClassTransformer;

public class TimeZoneTimeDelegateTransformer implements IClassTransformer
{

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if ("net.minecraft.world.storage.WorldInfo".equals(name))
        {
            // TODO Auto-generated method stub
        }
        return basicClass;
    }

}
