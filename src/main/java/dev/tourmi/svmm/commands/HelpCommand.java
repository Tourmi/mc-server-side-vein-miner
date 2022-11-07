package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class HelpCommand {
    public static int execute(CommandContext<CommandSourceStack> commandContext) {
        Commands.sendMessage(commandContext, getHelpString());
        return Command.SINGLE_SUCCESS;
    }

    private static String getHelpString() {
        return "SVMM allows you to mine veins of ore instantly.\nIf you do not wish to";
    }
}
