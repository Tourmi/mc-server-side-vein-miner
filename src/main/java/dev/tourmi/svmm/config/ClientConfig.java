package dev.tourmi.svmm.config;

import dev.tourmi.svmm.utils.PredicateUtils;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Objects;
import java.util.UUID;

public final class ClientConfig {
    public final ForgeConfigSpec SPEC;

    public final ForgeConfigSpec.BooleanValue MOD_DISABLED;
    public final ForgeConfigSpec.BooleanValue MOD_RESTRICTED;

    public final ForgeConfigSpec.BooleanValue GIANT_VEIN_MINING_DISABLED;
    public final ForgeConfigSpec.BooleanValue GIANT_VEIN_MINING_RESTRICTED;

    public final ForgeConfigSpec.BooleanValue TUNNELING_RESTRICTED;

    public final ForgeConfigSpec.BooleanValue FORCE_RESTRICTED;

    public final ForgeConfigSpec.EnumValue<TriggerActions> TRIGGER_WHEN;

    public ClientConfig(UUID playerUUID) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("svmm-player-" + playerUUID.toString());

        builder.push("self-config");
        TRIGGER_WHEN = builder.comment("Condition for when the mod is triggered")
                .defineEnum("trigger_when", SVMMConfig.TRIGGER_WHEN_DEFAULT, o -> o instanceof TriggerActions, TriggerActions.class);
        builder.pop();

        builder.push("toggles");
        MOD_DISABLED = builder.comment("Whether or not the player disabled the mod for themselves")
                .define("mod_disabled", SVMMConfig.MOD_DEFAULT_DISABLED);

        GIANT_VEIN_MINING_DISABLED = builder.comment("Whether or not the player disabled giant vein mining for themselves")
                .define("giant_vein_mining_disabled", SVMMConfig.GIANT_VEIN_MINING_DEFAULT_DISABLED);
        builder.pop();

        builder.push("restrictions");
        MOD_RESTRICTED = builder.comment("Whether or not the mod was restricted by an operator")
                .define("mod_restricted", SVMMConfig.MOD_DEFAULT_RESTRICTED);

        GIANT_VEIN_MINING_RESTRICTED = builder.comment("Whether or not giant vein mining was restricted by an operator")
                .define("giant_vein_mining_restricted", SVMMConfig.GIANT_VEIN_MINING_DEFAULT_RESTRICTED);

        TUNNELING_RESTRICTED = builder.comment("Whether or not tunneling was restricted by an operator")
                .define("tunneling_restricted", SVMMConfig.TUNNELING_DEFAULT_RESTRICTED);

        FORCE_RESTRICTED = builder.comment("Whether or not /svmm force was restricted by an operator")
                .define("force_restricted", SVMMConfig.FORCE_DEFAULT_RESTRICTED);
        builder.pop();

        builder.pop();
        SPEC = builder.build();
    }
}
