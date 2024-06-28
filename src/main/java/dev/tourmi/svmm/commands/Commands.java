package dev.tourmi.svmm.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class Commands {
    private static final List<ICommand> commands = new ArrayList<>() {{
        add(new ToggleFeaturesCommand(true));
        add(new ToggleFeaturesCommand(false));
        add(new RestrictOrAllowFeaturesCommand(true));
        add(new RestrictOrAllowFeaturesCommand(false));
        add(new ForceCommand());
        add(new TunnelCommand());
        add(new ConfigCommand());

        add(new HelpCommand(this));
    }};

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var helpCommand = commands.stream()
                .filter(c -> c instanceof HelpCommand)
                .map(c -> (HelpCommand)c)
                .reduce((c1, c2) -> {throw new IllegalStateException("Only one HelpCommand should exist");})
                .orElseThrow(() -> new NoSuchElementException("A HelpCommand is required"));

        LiteralArgumentBuilder<CommandSourceStack> command = net.minecraft.commands.Commands.literal("svmm")
                .requires(CommandPredicates::isModEnabled)
                .requires(CommandPredicates::canPlayerAccessMod)
                .executes(helpCommand::execute); // /svmm

        for (ICommand c : commands) {
            command = command.then(c.getCommand());
        }

        dispatcher.register(command);
    }
}
