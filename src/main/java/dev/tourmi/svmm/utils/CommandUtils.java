package dev.tourmi.svmm.utils;

import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CommandUtils {
    public static boolean isModerator(CommandSourceStack cs) {
        return !cs.isPlayer() || cs.hasPermission(Commands.LEVEL_MODERATORS);
    }

    public static boolean isModerator(CommandContext<CommandSourceStack> cc) {
        return isModerator(cc.getSource());
    }

    public static void sendMessage(CommandContext<CommandSourceStack> commandContext, String message) {
        if (commandContext.getSource().getEntity() instanceof Player player) {
            player.sendSystemMessage(Component.literal(message));
        } else {
            commandContext.getSource().getServer().sendSystemMessage(Component.literal(message));
        }
    }

    public static boolean isFromPlayer(CommandContext<CommandSourceStack> commandContext) {
        return commandContext.getSource().getEntity() instanceof Player;
    }

    public static ClientConfig getSourceConfig(CommandSourceStack cs) {
        return ClientConfigs.getClientConfig(cs.getPlayer().getUUID());
    }

    public static ClientConfig getSourceConfig(CommandContext<CommandSourceStack> commandContext) {
        return getSourceConfig(commandContext.getSource());
    }
}
