package dev.tourmi.svmm.config;

import dev.tourmi.svmm.utils.MinecraftUtils;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class SVMMConfig {
    private SVMMConfig() {}

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue MOD_DISABLED;
    public static final ForgeConfigSpec.BooleanValue MOD_DEFAULT_DISABLED;
    public static final ForgeConfigSpec.BooleanValue MOD_DEFAULT_RESTRICTED;
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
            "minecraft:gilded_blackstone",
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
    public static final ForgeConfigSpec.BooleanValue GIANT_VEIN_MINING_DEFAULT_DISABLED;
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

    public static final ForgeConfigSpec.BooleanValue TUNNELING_DISABLED;
    public static final ForgeConfigSpec.BooleanValue TUNNELING_DEFAULT_RESTRICTED;
    public static final ForgeConfigSpec.IntValue TUNNELING_MAX_DIMENSION;
    public static final ForgeConfigSpec.IntValue TUNNELING_MAX_DEPTH;
    public static final ForgeConfigSpec.IntValue TUNNELING_MAX_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue TUNNELING_SAME_TYPE;
    public static final ForgeConfigSpec.BooleanValue TUNNELING_LOG_USAGE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TUNNELING_WHITELIST;
    private static final Supplier<List<String>> DEFAULT_TUNNELING_WHITELIST = () -> Stream.concat(Stream.of(
            "minecraft:stone",
            "minecraft:deepslate",
            "minecraft:tuff",
            "minecraft:andesite",
            "minecraft:granite",
            "minecraft:diorite",
            "minecraft:calcite",
            "minecraft:dirt",
            "minecraft:sand",
            "minecraft:red_sand",
            "minecraft:gravel",
            "minecraft:sandstone",
            "minecraft:red_sandstone",
            "minecraft:grass",
            "minecraft:dripstone_block",
            "minecraft:podzol",
            "minecraft:mud",
            "minecraft:crimson_nylium",
            "minecraft:warped_nylium",
            "minecraft:amethyst_block",
            "minecraft:obsidian",
            "minecraft:ice",
            "minecraft:packed_ice",
            "minecraft:blue_ice",
            "minecraft:snow_block",
            "minecraft:clay",
            "minecraft:netherrack",
            "minecraft:soul_sand",
            "minecraft:soul_soil",
            "minecraft:basalt",
            "minecraft:blackstone",
            "minecraft:glowstone",
            "minecraft:end_stone",
            "minecraft:terracotta"
    ), MinecraftUtils.getColorNames().map(color -> "minecraft:" + color + "_terracotta")).toList();


    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("Server-side Vein Miner configuration");
        MOD_DISABLED = builder.comment("Whether or not the entire mod is disabled")
                .define("mod_disabled", false);
        MOD_DEFAULT_DISABLED = builder.comment("Whether or not the mod is disabled by default for new players.\n" +
                "They may enable it with /svmm enable").define("mod_disabled_default", false);
        MOD_DEFAULT_RESTRICTED = builder.comment("Whether or not the entire mod is restricted by default for new users")
                .define("default_restricted", false);
        MAXIMUM_BLOCKS_TO_BREAK = builder.comment("The maximum amount of blocks the mod is allowed to break at once")
                .defineInRange("maximum_blocks_to_break", 100, 1, Integer.MAX_VALUE);
        BLOCK_WHITELIST = builder.comment("The blocks that are allowed to be vein-mined")
                .defineList("block_white_list", DEFAULT_BLOCK_WHITELIST.get(), String.class::isInstance);
        STOP_WHEN_ABOUT_TO_BREAK = builder.comment("Whether or not to stop the vein mining when the tool is about to break")
                .define("stop_when_about_to_break_tool", true);

        builder.push("Giant vein mining configuration");
        GIANT_VEIN_MINING_DISABLED = builder.comment("Whether or not giant vein mining is disabled on the server")
                .define("giant_vein_mining_disabled", false);
        GIANT_VEIN_MINING_DEFAULT_DISABLED = builder.comment("Whether or not giant vein mining is disabled by default for new players.\n" +
                        "They may enable it with /svmm enable giantVein")
                .define("giant_vein_mining_default_disabled", false);
        GIANT_VEIN_MINING_DEFAULT_RESTRICTED = builder.comment("Whether or not giant vein mining is restricted by default for new players")
                .define("giant_vein_mining_default_restricted", false);
        GIANT_VEIN_STARTER_BLOCKS = builder.comment("The blocks the player needs to mine before the vein miner is triggered")
                .defineList("giant_vein_starter_ore", DEFAULT_GIANT_VEIN_STARTER_ORE.get(), String.class::isInstance);
        GIANT_VEIN_WHITELIST = builder.comment("All the blocks the Vein Miner is allowed to mine at once when triggered")
                .defineList("giant_vein_whitelist", DEFAULT_GIANT_VEIN_WHITELIST.get(), String.class::isInstance);
        builder.pop();

        builder.push("Tunneling configuration");
        TUNNELING_DISABLED = builder.comment("Whether or not tunneling is disabled on the server")
                .define("tunneling_disabled", false);
        TUNNELING_DEFAULT_RESTRICTED = builder.comment("Whether or not tunneling is restricted by default for new players.\n" +
                        "Moderators may allow it for players with /svmm enable {player_name} tunneling")
                .define("tunneling_default_restricted", false);
        TUNNELING_LOG_USAGE = builder.comment("Whether or not to log to console whenever a player tunnels")
                .define("tunneling_logging", true);
        TUNNELING_MAX_DIMENSION = builder.comment("The maximum width & height for a tunnel")
                .defineInRange("tunneling_max_dimensions", 5, 1, Integer.MAX_VALUE);
        TUNNELING_MAX_DEPTH = builder.comment("How deep a tunnel is allowed to go at once")
                .defineInRange("tunneling_max_depth", 50, 1, Integer.MAX_VALUE);
        TUNNELING_MAX_BLOCKS = builder.comment("How many blocks a tunnel may break at once. Has priority on dimensions and depth")
                .defineInRange("tunneling_max_blocks", 100, 1, Integer.MAX_VALUE);
        TUNNELING_SAME_TYPE = builder.comment("Whether or not tunneling should stop when it encounters a different block from the starting block")
                .define("tunneling_same_type", false);
        TUNNELING_WHITELIST = builder.comment("The blocks the player is allowed to mine through while tunneling")
                .defineList("tunneling_whitelist", DEFAULT_TUNNELING_WHITELIST.get(), String.class::isInstance);
        builder.pop();

        builder.pop();

        SPEC = builder.build();
    }
}
