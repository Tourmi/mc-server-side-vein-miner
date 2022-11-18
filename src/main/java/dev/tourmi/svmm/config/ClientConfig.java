package dev.tourmi.svmm.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public final ForgeConfigSpec SPEC;

    public final ForgeConfigSpec.BooleanValue MOD_DISABLED;
    public final ForgeConfigSpec.BooleanValue MOD_RESTRICTED;

    public final ForgeConfigSpec.BooleanValue GIANT_VEIN_MINING_DISABLED;
    public final ForgeConfigSpec.BooleanValue GIANT_VEIN_MINING_RESTRICTED;

    public final ForgeConfigSpec.BooleanValue TUNNELING_RESTRICTED;

    public final ForgeConfigSpec.BooleanValue FORCE_RESTRICTED;

    public ClientConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        MOD_DISABLED = builder.comment("Whether or not the player disabled the mod for themselves")
                .define("mod_disabled", SVMMConfig.MOD_DEFAULT_DISABLED.get().booleanValue());
        MOD_RESTRICTED = builder.comment("Whether or not the mod was restricted by an operator")
                .define("mod_restricted", SVMMConfig.MOD_DEFAULT_RESTRICTED.get().booleanValue());
        GIANT_VEIN_MINING_DISABLED = builder.comment("Whether or not the player disabled giant vein mining for themselves")
                .define("giant_vein_mining_disabled", SVMMConfig.GIANT_VEIN_MINING_DEFAULT_DISABLED.get().booleanValue());
        GIANT_VEIN_MINING_RESTRICTED = builder.comment("Whether or not giant vein mining was restricted by an operator")
                .define("giant_vein_mining_restricted", SVMMConfig.GIANT_VEIN_MINING_DEFAULT_RESTRICTED.get().booleanValue());
        TUNNELING_RESTRICTED = builder.comment("Whether or not tunneling was restricted by an operator")
                .define("tunneling_restricted", SVMMConfig.TUNNELING_DEFAULT_RESTRICTED.get().booleanValue());
        FORCE_RESTRICTED = builder.comment("Whether or not /svmm force was restricted by an operator")
                .define("force_restricted", SVMMConfig.FORCE_DEFAULT_RESTRICTED.get().booleanValue());

        SPEC = builder.build();
    }
}
