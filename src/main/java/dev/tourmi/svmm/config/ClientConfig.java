package dev.tourmi.svmm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public final ForgeConfigSpec SPEC;

    public final ForgeConfigSpec.BooleanValue MOD_DISABLED;
    public final ForgeConfigSpec.BooleanValue MOD_RESTRICTED;

    public ClientConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        MOD_DISABLED = builder.comment("Whether or not the player disabled the mod for themselves").define("mod_disabled", false);
        MOD_RESTRICTED = builder.comment("Whether or not the mod was restricted by an operator").define("mod_restricted", false);

        SPEC = builder.build();
    }
}
