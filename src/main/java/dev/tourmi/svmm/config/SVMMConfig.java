package dev.tourmi.svmm.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Supplier;

public final class SVMMConfig {
    private SVMMConfig() {}

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue DEFAULT_RESTRICTED;
    public static final ForgeConfigSpec.IntValue MAXIMUM_BLOCKS_TO_BREAK;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_WHITELIST;
    private static final Supplier<List<String>> DEFAULT_BLOCK_WHITELIST = () -> List.of(
            "minecraft:coal_ore",
            "minecraft:copper_ore",
            "minecraft:diamond_ore",
            "minecraft:emerald_ore",
            "minecraft:gold_ore",
            "minecraft:iron_ore",
            "minecraft:lapis_ore",
            "minecraft:nether_gold_ore",
            "minecraft:nether_quartz_ore",
            "minecraft:redstone_ore",
            "minecraft:deepslate_coal_ore",
            "minecraft:deepslate_copper_ore",
            "minecraft:deepslate_diamond_ore",
            "minecraft:deepslate_emerald_ore",
            "minecraft:deepslate_gold_ore",
            "minecraft:deepslate_iron_ore",
            "minecraft:deepslate_lapis_ore",
            "minecraft:deepslate_redstone_ore"
    );
    public static final ForgeConfigSpec.BooleanValue STOP_WHEN_ABOUT_TO_BREAK;

    public static final ForgeConfigSpec.BooleanValue GIANT_VEIN_MINING_DISABLED;
    public static final ForgeConfigSpec.BooleanValue GIANT_VEIN_MINING_DEFAULT_RESTRICTED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GIANT_VEIN_STARTER_BLOCKS;
    private static final Supplier<List<String>> DEFAULT_GIANT_VEIN_STARTER_ORE = () -> List.of(
            "minecraft:copper_ore",
            "minecraft:iron_ore",
            "minecraft:deepslate_copper_ore",
            "minecraft:deepslate_iron_ore",
            "minecraft:raw_copper_block",
            "minecraft:raw_iron_block"
    );
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GIANT_VEIN_WHITELIST;
    private static final Supplier<List<String>> DEFAULT_GIANT_VEIN_WHITELIST = () -> List.of(
            "minecraft:copper_ore",
            "minecraft:iron_ore",
            "minecraft:deepslate_copper_ore",
            "minecraft:deepslate_iron_ore",
            "minecraft:raw_copper_block",
            "minecraft:raw_iron_block",
            "minecraft:tuff"
    );


    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Server-side Vein Miner configuration");
        DEFAULT_RESTRICTED = builder.comment("Whether or not the entire mod is restricted by default for new users").define("default_restricted", false);
        MAXIMUM_BLOCKS_TO_BREAK = builder.comment("The maximum amount of blocks the mod is allowed to break at once").defineInRange("maximum_blocks_to_break", 100, 1, 10_000);
        BLOCK_WHITELIST = builder.comment("The blocks that are allowed to be vein-mined").defineList("block_white_list", DEFAULT_BLOCK_WHITELIST.get(), String.class::isInstance);
        STOP_WHEN_ABOUT_TO_BREAK = builder.comment("Whether or not to stop the vein mining when the tool is about to break").define("stop_when_about_to_break_tool", true);

        builder.push("Giant vein mining configuration");
        GIANT_VEIN_MINING_DISABLED = builder.comment("Whether or not giant vein mining is disabled on the server").define("giant_vein_mining_disabled", false);
        GIANT_VEIN_MINING_DEFAULT_RESTRICTED = builder.comment("Whether or not giant vein mining is restricted by default for new players").define("giant_vein_mining_default_restricted", false);
        GIANT_VEIN_STARTER_BLOCKS = builder.comment("The blocks the player needs to mine before the vein miner is triggered").defineList("giant_vein_starter_ore", DEFAULT_GIANT_VEIN_STARTER_ORE.get(), String.class::isInstance);
        GIANT_VEIN_WHITELIST = builder.comment("All the blocks the Vein Miner is allowed to mine at once when triggered").defineList("giant_vein_whitelist", DEFAULT_GIANT_VEIN_WHITELIST.get(), String.class::isInstance);
        builder.pop();

        builder.pop();

        SPEC = builder.build();
    }
}
