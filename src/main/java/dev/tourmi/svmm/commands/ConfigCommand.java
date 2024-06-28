package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.commands.subcommands.ConfigEnumValueCommand;
import dev.tourmi.svmm.config.TriggerActions;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ConfigCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("config")
                .requires(CommandPredicates::isPlayer)
                .executes(this::defaultExecute)
                .then(new ConfigEnumValueCommand<>("triggerWhen", TriggerActions.class, cc -> CommandUtils.getSourceConfig(cc).TRIGGER_WHEN).getCommand());
    }

    @Override
    public String getHelpText(CommandContext<CommandSourceStack> cc) {
        if (CommandUtils.isConsole(cc)) {
            return "";
        }

        return """
                - /svmm config
                    Lists the available config keys that can be modified
                - /svmm config {configKey}
                    Displays the value of the config key
                - /svmm config {configKey} {value}
                    Sets the {value} for the given {configKey}
                """;
    }

    private int defaultExecute(CommandContext<CommandSourceStack> cc) {
        if (CommandUtils.isConsole(cc)) {
            return 0;
        }

        CommandUtils.sendMessage(cc, "Available configuration keys:");
        CommandUtils.sendMessage(cc, "triggerWhen");

        return Command.SINGLE_SUCCESS;
    }
}
