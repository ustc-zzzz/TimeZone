package com.github.ustc_zzzz.timezone.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("TimeZone")
@IFMLLoadingPlugin.MCVersion("")
@IFMLLoadingPlugin.TransformerExclusions("com.github.ustc_zzzz.timezone.asm.")
public class TimeZoneCore implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[]
        {
                "com.github.ustc_zzzz.timezone.asm.TimeZoneTimeDelegateTransformer"
        };
    }

    @Override
    public String getModContainerClass()
    {
        return "com.github.ustc_zzzz.timezone.asm.TimeZoneModContainer";
    }

    @Override
    public String getSetupClass()
    {
        return "com.github.ustc_zzzz.timezone.asm.TimeZoneModContainer";
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        // nothing.
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
