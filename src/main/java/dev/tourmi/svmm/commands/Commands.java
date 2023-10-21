package dev.tourmi.svmm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    private static final List<ICommand> commands = new ArrayList<>() {{
        add(new ToggleCommand(true));
        add(new ToggleCommand(false));
        add(new ForceCommand());
        add(new TunnelCommand());
        add(new ConfigCommand());

        add(new HelpCommand(this));
    }};

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> cs = net.minecraft.commands.Commands.literal("svmm")
                .requires(c -> !SVMMConfig.MOD_DISABLED.get())
                .requires(c -> CommandUtils.isModerator(c) || !CommandUtils.getSourceConfig(c).MOD_RESTRICTED.get());
        for (ICommand command : commands) {
            cs = cs.then(command.getCommand());
        }

        cs = cs.executes(commands.get(commands.size() - 1)::defaultExecute); //default when no additional arguments given
        dispatcher.register(cs);
    }
}
