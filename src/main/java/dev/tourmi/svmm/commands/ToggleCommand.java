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

public class ToggleCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand(boolean disable) {
        return Commands.literal(disable ? "disable" : "enable")
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(cs -> cs.hasPermission(Commands.LEVEL_MODERATORS))
                        .executes(cs -> ToggleCommand.toggleOther(cs, disable)))
                .executes(cs -> ToggleCommand.selfToggle(cs, disable));
    }

    public static int selfToggle(CommandContext<CommandSourceStack> commandContext, boolean disable) {
        if (!CommandUtils.isFromPlayer(commandContext)) {
            CommandUtils.sendMessage(commandContext, formatMessage("The %s command needs to target a player.", disable, "disable", "enable"));
            return 0;
        }

        ClientConfig cfg = CommandUtils.getSourceConfig(commandContext);
        boolean isModerator = CommandUtils.isModerator(commandContext);
        if (cfg.MOD_RESTRICTED.get() && !isModerator) {
            CommandUtils.sendMessage(commandContext, "You do not have access to this command.");
            return 0;
        }

        cfg.MOD_DISABLED.set(disable);
        if (isModerator) {
            cfg.MOD_RESTRICTED.set(false);
        }
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, formatMessage("Vein Miner is now %s", disable, "disabled", "enabled"));

        return Command.SINGLE_SUCCESS;
    }

    public static int toggleOther(CommandContext<CommandSourceStack> commandContext, boolean disable) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        cfg.MOD_RESTRICTED.set(disable);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, formatMessage("Vein Miner is now %s for " + player.getName().getString(), disable, "disabled", "enabled"));

        return Command.SINGLE_SUCCESS;
    }

    private static String formatMessage(String message, boolean disable, String choice1, String choice2) {
        return String.format(message, disable ? choice1 : choice2);
    }
}
