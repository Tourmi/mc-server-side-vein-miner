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
                .then(Commands.literal("tunnel").executes(HelpCommand::executeTunnel)) // /svmm help tunnel
                .executes(HelpCommand::execute); // /svmm help
    }

    public static int execute(CommandContext<CommandSourceStack> commandContext) {
        CommandUtils.sendMessage(commandContext, getHelpString(commandContext));
        return Command.SINGLE_SUCCESS;
    }

    public static int executeTunnel(CommandContext<CommandSourceStack> commandContext) {
        CommandUtils.sendMessage(commandContext, """
                This command allows to automatically dig tunnels. Holding the sneak key allows to delay the creation of the tunnel.
                When using an even number for one of the dimensions of the tunnel, the tunnel center will be rounded towards the top left block
                - /svmm tunnel cancel
                    Cancels the creation of a tunnel on the next block mined.
                - /svmm tunnel {width} {height}
                    The tunnel will have the specified dimensions, and will be as deep as possible
                - /svmm tunnel {width} {height} {maxDepth}
                    The tunnel will have the specified dimensions, and will have the maximum depth specified
                - /svmm tunnel
                    Will reuse the previously specified dimensions for tunneling."""
        );
        return Command.SINGLE_SUCCESS;
    }

    private static String getHelpString(CommandContext<CommandSourceStack> commandContext) {
        return CommandUtils.isModerator(commandContext) ?
                """
                SVMM allows you to mine veins of ore instantly. Hold down shift if you do not wish to trigger the mod
                - /svmm help
                    shows this help message
                - /svmm disable [giantVein]
                    disables the mod or specific features for yourself
                - /svmm enable [giantVein]
                    enables the mod or specific features for yourself
                - /svmm disable {player} [giantVein|tunnel|force]
                    restricts the mod or specific features to be used by the specified player
                - /svmm enable {player} [giantVein|tunnel|force]
                    allows the mod or specific features to be used by the specified player
                - /svmm tunnel
                    run /svmm help tunnel for more details about this command
                - /svmm force
                    Forces a vein mine on the next block mined""" :
                """
                SVMM allows you to mine veins of ore instantly. Hold down shift if you do not wish to trigger the mod
                - /svmm help
                    shows this help message
                - /svmm disable [giantVein]
                    disables the mod or specific features for yourself
                - /svmm enable [giantVein]
                    enables the mod or specific features for yourself
                - /svmm tunnel
                    run /svmm help tunnel for more details about this command
                - /svmm force
                    Forces a vein mine on the next block mined""";
    }
}
