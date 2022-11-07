package dev.tourmi.svmm.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.UUID;

public class ClientConfig {
    public final ForgeConfigSpec SPEC;

    public final ForgeConfigSpec.BooleanValue MOD_DISABLED;

    public ClientConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        MOD_DISABLED = builder.comment("Whether or not the player disabled the mod for themselves").define("mod_disabled", false);

        SPEC = builder.build();
    }
}
