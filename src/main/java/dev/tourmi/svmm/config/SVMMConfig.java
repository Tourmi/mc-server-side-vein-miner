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
            "#forge:ores"
    );
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_BLACKLIST;
    private static final Supplier<List<String>> DEFAULT_BLOCK_BLACKLIST = () -> List.of(
            "minecraft:ancient_debris"
    );
    public static final ForgeConfigSpec.BooleanValue STOP_WHEN_ABOUT_TO_BREAK;
    public static final ForgeConfigSpec.BooleanValue TELEPORT_ITEMS_TO_PLAYER;

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
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GIANT_VEIN_BLACKLIST;
    private static final Supplier<List<String>> DEFAULT_GIANT_VEIN_BLACKLIST = List::of;

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
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TUNNELING_BLACKLIST;
    private static final Supplier<List<String>> DEFAULT_TUNNELING_BLACKLIST = () -> List.of(
            "minecraft:obsidian");

    public static final ForgeConfigSpec.BooleanValue FORCE_DISABLED;
    public static final ForgeConfigSpec.BooleanValue FORCE_DEFAULT_RESTRICTED;
    public static final ForgeConfigSpec.BooleanValue FORCE_LOG_USAGE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FORCE_BLACKLIST;
    private static final Supplier<List<String>> DEFAULT_FORCE_VEINMINE_BLACKLIST = () -> List.of(
            "minecraft:bedrock",
            "minecraft:reinforced_deepslate",
            "minecraft:chest",
            "minecraft:trapped_chest",
            "minecraft:barrel"
    );

    public static final ForgeConfigSpec.BooleanValue RUNTIME_CONFIG_DISABLED;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("svmm");
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
        BLOCK_BLACKLIST = builder.comment("The blocks that will not be veinmined, even if they're part of the whitelist")
                .defineList("block_black_list", DEFAULT_BLOCK_BLACKLIST.get(), String.class::isInstance);
        STOP_WHEN_ABOUT_TO_BREAK = builder.comment("Whether or not to stop the vein mining when the tool is about to break")
                .define("stop_when_about_to_break_tool", true);
        TELEPORT_ITEMS_TO_PLAYER = builder.comment("Whether or not to teleport the mined items to the player")
                .define("teleport_items_to_player", true);
        RUNTIME_CONFIG_DISABLED = builder.comment("Whether or not to allow moderators to modify the config at runtime")
                .define("runtime_config_edit_disabled", false);

        builder.push("giant-vein-mining");
        GIANT_VEIN_MINING_DISABLED = builder.comment("Whether or not giant vein mining is disabled on the server")
                .define("giant_vein_mining_disabled", false);
        GIANT_VEIN_MINING_DEFAULT_DISABLED = builder.comment("Whether or not giant vein mining is disabled by default for new players.\n" +
                        "They may enable it with /svmm enable giantVein")
                .define("giant_vein_mining_default_disabled", false);
        GIANT_VEIN_MINING_DEFAULT_RESTRICTED = builder.comment("Whether or not giant vein mining is restricted by default for new players")
                .define("giant_vein_mining_default_restricted", false);
        GIANT_VEIN_STARTER_BLOCKS = builder.comment("The blocks the player needs to mine before the giant vein miner is triggered")
                .defineList("giant_vein_starter_ore", DEFAULT_GIANT_VEIN_STARTER_ORE.get(), String.class::isInstance);
        GIANT_VEIN_WHITELIST = builder.comment("Blocks the giant vein miner is allowed to mine when triggered")
                .defineList("giant_vein_whitelist", DEFAULT_GIANT_VEIN_WHITELIST.get(), String.class::isInstance);
        GIANT_VEIN_BLACKLIST = builder.comment("Blocks the giant vein miner won't mine when triggered")
                .defineList("giant_vein_blacklist", DEFAULT_GIANT_VEIN_BLACKLIST.get(), String.class::isInstance);
        builder.pop();

        builder.push("tunneling");
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
        TUNNELING_WHITELIST = builder.comment("The blocks the player is allowed to tunnel through")
                .defineList("tunneling_whitelist", DEFAULT_TUNNELING_WHITELIST.get(), String.class::isInstance);
        TUNNELING_BLACKLIST = builder.comment("The blocks the player is not allowed to tunnel through")
                .defineList("tunneling_blacklist", DEFAULT_TUNNELING_BLACKLIST.get(), String.class::isInstance);
        builder.pop();

        builder.push("force-vein-mining");
        FORCE_DISABLED = builder.comment("Whether or not /svmm force is disabled on the server")
                        .define("force_disabled", false);
        FORCE_DEFAULT_RESTRICTED = builder.comment("Whether or not /svmm force is restricted by default")
                        .define("force_default_restricted", true);
        FORCE_LOG_USAGE = builder.comment("Whether or not to log to console whenever a player uses the force vein mine")
                .define("force_logging", true);
        FORCE_BLACKLIST = builder.comment("Blocks that aren't allowed to be force vein mined")
                        .defineList("force_blacklist", DEFAULT_FORCE_VEINMINE_BLACKLIST.get(), String.class::isInstance);
        builder.pop();

        builder.pop();

        SPEC = builder.build();
    }
}
