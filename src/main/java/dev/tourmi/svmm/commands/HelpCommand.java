package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;

public class HelpCommand implements ICommand {
    private final Collection<ICommand> commands;

    public HelpCommand(Collection<ICommand> commands) {
        this.commands = commands;
    }

    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("help")
                .then(Commands.literal("tunnel")
                        .requires(cs -> !SVMMConfig.TUNNELING_DISABLED.get())
                        .requires(cs -> CommandUtils.isModerator(cs) || !CommandUtils.getSourceConfig(cs).TUNNELING_RESTRICTED.get())
                        .executes(this::executeTunnel)) // /svmm help tunnel
                .executes(this::defaultExecute); // /svmm help
    }

    public int defaultExecute(CommandContext<CommandSourceStack> commandContext) {
        CommandUtils.sendMessage(commandContext, getHelpMessage(commandContext));
        return Command.SINGLE_SUCCESS;
    }

    public String getHelpText(CommandContext<CommandSourceStack> cc) {
        return """
                - /svmm help
                    shows this help message
                """;
    }

    private String getHelpMessage(CommandContext<CommandSourceStack> cc) {
        StringBuilder str = new StringBuilder("SVMM allows you to mine veins of ore instantly. Hold shift if you do not wish to trigger the mod\n");

        for (ICommand command : commands) {
            str.append(command.getHelpText(cc));
        }

        return str.toString().trim();
    }

    public int executeTunnel(CommandContext<CommandSourceStack> commandContext) {
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
}
