package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.server.command.EnumArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand implements ICommand {
    private enum ConfigKeys {
        block_white_list,
        block_black_list,
        giant_vein_starter_ore,
        giant_vein_whitelist,
        giant_vein_blacklist,
        tunneling_whitelist,
        tunneling_blacklist,
        force_blacklist
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("config")
                .requires(cs -> !SVMMConfig.RUNTIME_CONFIG_DISABLED.get())
                .requires(CommandUtils::isModerator)
                .then(Commands.literal("list")
                    .then(Commands.argument("configKey", EnumArgument.enumArgument(ConfigKeys.class))
                        .then(Commands.literal("add")
                                .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(this::executeAdd))) // /svmm config list {configKey} add {value}
                        .then(Commands.literal("remove")
                                .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(this::executeRemove))) // /svmm config list {configKey} remove {value}
                        .executes(this::executeConfigList)) // /svmm config list {configKey}
                    .executes(this::executeList)) // /svmm config list
                .executes(this::defaultExecute); // /svmm config
    }

    @Override
    public int defaultExecute(CommandContext<CommandSourceStack> cc) {

        return Command.SINGLE_SUCCESS;
    }

    @Override
    public String getHelpText(CommandContext<CommandSourceStack> cc) {
        if (!CommandUtils.isModerator(cc) || SVMMConfig.RUNTIME_CONFIG_DISABLED.get()) {
            return "";
        }

        return """
                - /svmm config list
                    Lists the available config keys that can be modified
                - /svmm config list {configKey}
                    Displays the items in the configKey list
                - /svmm config list {configKey} [add|remove] {value}
                    Adds or removes the given {value} to the given {configKey}
                """;
    }

    private int executeList(CommandContext<CommandSourceStack> cc) {
        CommandUtils.sendMessage(cc, "Available configuration keys");
        CommandUtils.sendMessage(cc, Arrays.stream(ConfigKeys.values()).map(Enum::name).collect(Collectors.joining(", ")));
        return Command.SINGLE_SUCCESS;
    }

    private int executeConfigList(CommandContext<CommandSourceStack> cc) {
        var key = cc.getArgument("configKey", ConfigKeys.class);
        var config = GetListConfig(key);
        CommandUtils.sendMessage(cc, "Values in " + key + ":\n" + String.join(", ", config.get()));

        return Command.SINGLE_SUCCESS;
    }

    private int executeAdd(CommandContext<CommandSourceStack> cc) {
        var key = cc.getArgument("configKey", ConfigKeys.class);
        var config = GetListConfig(key);

        var newList = new ArrayList<>(config.get().stream().map(String::toString).toList());
        var value = cc.getArgument("value", String.class);
        newList.add(value);
        config.set(newList);
        config.save();

        CommandUtils.sendMessage(cc, "Successfully added " + value + " to " + key.name() + " list");

        return Command.SINGLE_SUCCESS;
    }

    private int executeRemove(CommandContext<CommandSourceStack> cc) {
        var key = cc.getArgument("configKey", ConfigKeys.class);
        var config = GetListConfig(key);

        var newList = new ArrayList<>(config.get().stream().map(String::toString).toList());
        var value = cc.getArgument("value", String.class);
        newList.remove(value);
        config.set(newList);
        config.save();

        CommandUtils.sendMessage(cc, "Successfully removed " + value + " from " + key.name() + " list");

        return Command.SINGLE_SUCCESS;
    }

    private ForgeConfigSpec.ConfigValue<List<? extends String>> GetListConfig(ConfigKeys key) {
        return switch (key) {
            case block_white_list -> SVMMConfig.BLOCK_WHITELIST;
            case block_black_list -> SVMMConfig.BLOCK_BLACKLIST;
            case giant_vein_starter_ore -> SVMMConfig.GIANT_VEIN_STARTER_BLOCKS;
            case giant_vein_whitelist -> SVMMConfig.GIANT_VEIN_WHITELIST;
            case giant_vein_blacklist -> SVMMConfig.GIANT_VEIN_BLACKLIST;
            case tunneling_whitelist -> SVMMConfig.TUNNELING_WHITELIST;
            case tunneling_blacklist -> SVMMConfig.TUNNELING_BLACKLIST;
            case force_blacklist -> SVMMConfig.FORCE_BLACKLIST;
        };
    }
}
