package com.github.ustc_zzzz.timezone.asm;

import java.util.Map;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("TimeZone")
@IFMLLoadingPlugin.MCVersion("")
@IFMLLoadingPlugin.TransformerExclusions("com.github.ustc_zzzz.timezone.asm.")
public class TimeZoneCore implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        if (ForgeVersion.mcVersion.equals("1.8.9"))
        {
            return new String[]
            {
                    "com.github.ustc_zzzz.timezone.asm.transformer.TimeDelegateTransformer",
                    "com.github.ustc_zzzz.timezone.asm.transformer.TimeSyncTransformer",
                    "com.github.ustc_zzzz.timezone.asm.transformer.ViewTransformer",
                    "com.github.ustc_zzzz.timezone.asm.transformer.ControlTransformer",
                    "com.github.ustc_zzzz.timezone.asm.transformer.TickUpdateTransformer"
            };
        }
        throw new RuntimeException("TimeZone: Invalid minecraft version: " + ForgeVersion.mcVersion);
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
