package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

public class HelpCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("help")
                .executes(HelpCommand::execute);
    }

    public static int execute(CommandContext<CommandSourceStack> commandContext) {
        CommandUtils.sendMessage(commandContext, getHelpString());
        return Command.SINGLE_SUCCESS;
    }

    private static String getHelpString() {
        return """
        SVMM allows you to mine veins of ore instantly. Hold down shift if you do not wish to trigger the mod
        - /svmm help
            shows this help message
        - /svmm disable
            disables the mod for yourself
        - /svmm enable
            enables the mod for yourself""";
    }
}
