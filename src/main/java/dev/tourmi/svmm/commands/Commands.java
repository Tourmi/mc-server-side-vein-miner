package dev.tourmi.svmm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public class Commands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> cs = net.minecraft.commands.Commands.literal("svmm")
                .then(HelpCommand.getCommand())
                .then(ToggleCommand.getCommand(true))
                .then(ToggleCommand.getCommand(false))
                .then(TunnelCommand.getCommand())
                .executes(HelpCommand::execute); //default when no additional arguments given

        dispatcher.register(cs);
    }
}
