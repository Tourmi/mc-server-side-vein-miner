package dev.tourmi.svmm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class Commands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> cs = net.minecraft.commands.Commands.literal("svmm")
                .then(net.minecraft.commands.Commands.literal("help").executes(HelpCommand::execute))
                .executes(HelpCommand::execute);

        dispatcher.register(cs);
    }

    public static void sendMessage(CommandContext<CommandSourceStack> commandContext, String message) {
        if (commandContext.getSource().getEntity() instanceof Player) {
            Player player = (Player) commandContext.getSource().getEntity();
            player.sendSystemMessage(Component.literal(message));
        } else {
            commandContext.getSource().getServer().sendSystemMessage(Component.literal(message));
        }
    }
}
