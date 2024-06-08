package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand implements ICommand {
    private enum ConfigListKeys {
        block_white_list(SVMMConfig.BLOCK_WHITELIST),
        block_black_list(SVMMConfig.BLOCK_BLACKLIST),
        giant_vein_starter_ore(SVMMConfig.GIANT_VEIN_STARTER_BLOCKS),
        giant_vein_whitelist(SVMMConfig.GIANT_VEIN_WHITELIST),
        giant_vein_blacklist(SVMMConfig.GIANT_VEIN_BLACKLIST),
        tunneling_whitelist(SVMMConfig.TUNNELING_WHITELIST),
        tunneling_blacklist(SVMMConfig.TUNNELING_BLACKLIST),
        force_blacklist(SVMMConfig.FORCE_BLACKLIST);

        final ForgeConfigSpec.ConfigValue<List<? extends String>> config;

        ConfigListKeys(ForgeConfigSpec.ConfigValue<List<? extends String>> config) {
            this.config = config;
        }
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        var list = Commands.literal("list")
                .executes(this::executeList);

        for (var key : ConfigListKeys.values()) {
            list.then(Commands.literal(key.toString())
                    .then(Commands.literal("add")
                            .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(ctx -> this.executeAdd(ctx, key)))) // /svmm config list {configKey} add {value}
                    .then(Commands.literal("remove")
                            .then(Commands.argument("value", StringArgumentType.string())
                                    .executes(ctx -> this.executeRemove(ctx, key)))) // /svmm config list {configKey} remove {value}
                    .executes(ctx -> this.executeConfigList(ctx, key))); // /svmm config list {configKey}
        }

        return Commands.literal("config")
                .requires(cs -> !SVMMConfig.RUNTIME_CONFIG_DISABLED.get())
                .requires(CommandUtils::isModerator)
                .then(list) // /svmm config list
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

    private int executeConfigList(CommandContext<CommandSourceStack> cc, ConfigListKeys key) {
        CommandUtils.sendMessage(cc, "Values in " + key + ":\n" + String.join(", ", key.config.get()));

        return Command.SINGLE_SUCCESS;
    }

    private int executeAdd(CommandContext<CommandSourceStack> cc, ConfigListKeys key) {
        var newList = new ArrayList<>(key.config.get().stream().map(String::toString).toList());
        var value = cc.getArgument("value", String.class);
        newList.add(value);
        key.config.set(newList);
        key.config.save();

        CommandUtils.sendMessage(cc, "Successfully added " + value + " to " + key.name() + " list");

        return Command.SINGLE_SUCCESS;
    }

    private int executeRemove(CommandContext<CommandSourceStack> cc, ConfigListKeys key) {
        var newList = new ArrayList<>(key.config.get().stream().map(String::toString).toList());
        var value = cc.getArgument("value", String.class);
        newList.remove(value);
        key.config.set(newList);
        key.config.save();

        CommandUtils.sendMessage(cc, "Successfully removed " + value + " from " + key.name() + " list");

        return Command.SINGLE_SUCCESS;
    }
}
