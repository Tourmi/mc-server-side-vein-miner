package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;

public class ToggleCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand(boolean disable) {
        return Commands.literal(disable ? "disable" : "enable")
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(cs -> cs.hasPermission(Commands.LEVEL_MODERATORS))
                        .executes(cs -> ToggleCommand.toggleOther(cs, disable))) // /svmm disable|enable {player}
                .then(Commands.literal("giantVein")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(cs -> cs.hasPermission(Commands.LEVEL_MODERATORS))
                                .executes(cs -> toggleOtherGiantVein(cs, disable))) // /svmm disable|enable giantVein {player}
                        .executes(cs -> selfToggleGiantVein(cs, disable))) // /svmm disable|enable giantVein
                .then(Commands.literal("tunnel")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(cs -> cs.hasPermission(Commands.LEVEL_MODERATORS))
                                .executes(cs -> toggleOtherTunneling(cs, disable)))) // /svmm disable|enable tunnel {player}
                .executes(cs -> ToggleCommand.selfToggle(cs, disable)); // /svmm disable|enable
    }

    public static int selfToggle(CommandContext<CommandSourceStack> commandContext, boolean disable) {
        if (!checkIsPlayer(commandContext)) return 0;
        if (!selfToggle(commandContext, disable, null, false)) return 0;

        CommandUtils.sendMessage(commandContext, formatMessage("Vein Miner is now %s", disable, "disabled", "enabled"));

        return Command.SINGLE_SUCCESS;
    }

    public static int toggleOther(CommandContext<CommandSourceStack> commandContext, boolean disable) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        cfg.MOD_RESTRICTED.set(disable);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, formatMessage("Vein Miner is now %s for " + player.getName().getString(), disable, "restricted", "allowed"));

        return Command.SINGLE_SUCCESS;
    }

    public static int selfToggleGiantVein(CommandContext<CommandSourceStack> commandContext, boolean disable) {
        if (!checkIsPlayer(commandContext)) return 0;

        ClientConfig cfg = CommandUtils.getSourceConfig(commandContext);
        if (!selfToggle(commandContext, disable, cfg.GIANT_VEIN_MINING_DISABLED, cfg.GIANT_VEIN_MINING_RESTRICTED.get())) return 0;

        CommandUtils.sendMessage(commandContext, formatMessage("Vein Miner is now %s for giant veins", disable, "disabled", "enabled"));

        return Command.SINGLE_SUCCESS;
    }

    public static int toggleOtherGiantVein(CommandContext<CommandSourceStack> commandContext, boolean disable) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        cfg.GIANT_VEIN_MINING_RESTRICTED.set(disable);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, formatMessage("Giant Vein Miner is now %s for " + player.getName().getString(), disable, "restricted", "allowed"));

        return Command.SINGLE_SUCCESS;
    }

    public static int toggleOtherTunneling(CommandContext<CommandSourceStack> commandContext, boolean disable) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        cfg.TUNNELING_RESTRICTED.set(disable);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, formatMessage("Tunneling is now %s for " + player.getName().getString(), disable, "restricted", "allowed"));

        return Command.SINGLE_SUCCESS;
    }

    private static boolean selfToggle(CommandContext<CommandSourceStack> commandContext, boolean disable, @Nullable ForgeConfigSpec.BooleanValue toggleFeature, boolean restricted) {
        ClientConfig cfg = CommandUtils.getSourceConfig(commandContext);
        boolean isModerator = CommandUtils.isModerator(commandContext);
        if ((cfg.MOD_RESTRICTED.get() || restricted) && !isModerator) {
            CommandUtils.sendMessage(commandContext, "You do not have access to this command.");
            return false;
        }
        if (toggleFeature != null) toggleFeature.set(disable);
        else cfg.MOD_DISABLED.set(disable);
        if (isModerator) resetModeratorRestrictions(cfg);
        cfg.SPEC.save();

        return true;
    }

    private static boolean checkIsPlayer(CommandContext<CommandSourceStack> cs) {
        if (!CommandUtils.isFromPlayer(cs)) {
            CommandUtils.sendMessage(cs, "The command needs to target a player.");
            return false;
        }
        return true;
    }

    private static void resetModeratorRestrictions(ClientConfig cfg) {
        cfg.MOD_RESTRICTED.set(false);
        cfg.GIANT_VEIN_MINING_RESTRICTED.set(false);
    }

    private static String formatMessage(String message, boolean disable, String choice1, String choice2) {
        return String.format(message, disable ? choice1 : choice2);
    }
}
