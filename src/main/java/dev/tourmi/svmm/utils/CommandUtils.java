package dev.tourmi.svmm.utils;

import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.commands.CommandPredicates;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class CommandUtils {
    public static boolean isModerator(CommandContext<CommandSourceStack> cc) {
        return CommandPredicates.isModerator(cc.getSource());
    }

    public static boolean isPlayer(CommandContext<CommandSourceStack> cc) {
        return CommandPredicates.isPlayer(cc.getSource());
    }

    public static boolean isConsole(CommandContext<CommandSourceStack> cc) {
        return CommandPredicates.isConsole(cc.getSource());
    }

    public static void sendMessage(CommandContext<CommandSourceStack> commandContext, String message) {
        if (commandContext.getSource().getEntity() instanceof Player player) {
            player.sendSystemMessage(Component.literal(message));
        } else {
            commandContext.getSource().getServer().sendSystemMessage(Component.literal(message));
        }
    }

    public static ClientConfig getSourceConfig(CommandSourceStack cs) {
        return ClientConfigs.getClientConfig(cs.getPlayer());
    }

    public static ClientConfig getSourceConfig(CommandContext<CommandSourceStack> commandContext) {
        return getSourceConfig(commandContext.getSource());
    }
}
