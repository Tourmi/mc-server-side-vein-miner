package dev.tourmi.svmm.commands;

import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public final class CommandPredicates {
    private CommandPredicates() {}

    public static boolean isPlayer(CommandSourceStack cs) {
        return cs.isPlayer();
    }

    public static boolean isConsole(CommandSourceStack cs) {
        return !isPlayer(cs);
    }

    public static boolean isModerator(CommandSourceStack cs) {
        return cs.hasPermission(Commands.LEVEL_GAMEMASTERS);
    }

    public static boolean isModEnabled(CommandSourceStack cs) {
        return isFeatureEnabled(SVMMConfig.MOD_DISABLED);
    }

    public static boolean isGiantVeinMiningEnabled(CommandSourceStack cs) {
        return isFeatureEnabled(SVMMConfig.GIANT_VEIN_MINING_DISABLED);
    }

    public static boolean isTunnelingEnabled(CommandSourceStack cs) {
        return isFeatureEnabled(SVMMConfig.TUNNELING_DISABLED);
    }

    public static boolean isForceVeinMiningEnabled(CommandSourceStack cs) {
        return isFeatureEnabled(SVMMConfig.FORCE_DISABLED);
    }

    public static boolean isRuntimeConfigEnabled(CommandSourceStack cs) {
        return isFeatureEnabled(SVMMConfig.RUNTIME_CONFIG_DISABLED);
    }

    public static boolean canPlayerAccessMod(CommandSourceStack cs) {
        return canPlayerAccessFeature(cs, () -> CommandUtils.getSourceConfig(cs).MOD_RESTRICTED);
    }

    public static boolean canPlayerAccessGiantVeinMining(CommandSourceStack cs) {
        return canPlayerAccessFeature(cs, () -> CommandUtils.getSourceConfig(cs).GIANT_VEIN_MINING_RESTRICTED);
    }

    public static boolean canPlayerAccessTunneling(CommandSourceStack cs) {
        return canPlayerAccessFeature(cs, () -> CommandUtils.getSourceConfig(cs).TUNNELING_RESTRICTED);
    }

    public static boolean canPlayerAccessForceVeinMining(CommandSourceStack cs) {
        return canPlayerAccessFeature(cs, () -> CommandUtils.getSourceConfig(cs).FORCE_RESTRICTED);
    }

    private static boolean isFeatureEnabled(ForgeConfigSpec.BooleanValue isDisabled) {
        return !isDisabled.get();
    }

    private static boolean canPlayerAccessFeature(CommandSourceStack cs, Supplier<ForgeConfigSpec.BooleanValue> isRestricted) {
        return isModerator(cs) || !isRestricted.get().get();
    }
}
