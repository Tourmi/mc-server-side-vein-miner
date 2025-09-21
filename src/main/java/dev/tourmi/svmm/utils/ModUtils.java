package dev.tourmi.svmm.utils;

import dev.tourmi.svmm.SVMM;
import dev.tourmi.svmm.config.SVMMConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Extract mod-related functions, to avoid breaking changes in Forge 56+
 */
public final class ModUtils {
    public static void RegisterConfig(ForgeConfigSpec spec, String path) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, spec, path);
    }

    public static void RegisterMod(SVMM mod) {
        RegisterConfig(SVMMConfig.SPEC, "svmm-config.toml");
        MinecraftForge.EVENT_BUS.register(mod);
    }
}
