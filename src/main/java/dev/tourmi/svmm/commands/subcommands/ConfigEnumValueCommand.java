package dev.tourmi.svmm.commands.subcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.commands.ICommand;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Function;

public class ConfigEnumValueCommand<T extends Enum<T>> implements ICommand {
    private final String name;
    private final Class<T> clazz;
    private final Function<CommandSourceStack, ForgeConfigSpec.EnumValue<T>> configGetter;

    public ConfigEnumValueCommand(String name, Class<T> clazz, Function<CommandSourceStack, ForgeConfigSpec.EnumValue<T>> configGetter) {
        this.name = name;
        this.clazz = clazz;
        this.configGetter = configGetter;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        var command = Commands.literal(name)
                .executes(this::defaultExecute);

        for (T value : clazz.getEnumConstants()) {
            command = command.then(Commands.literal(value.toString())
                    .executes(cc -> setValue(cc, value)));
        }

        return command;
    }

    @Override
    public String getHelpText(CommandContext<CommandSourceStack> commandContext) {
        return "";
    }

    private int defaultExecute(CommandContext<CommandSourceStack> cc) {
        CommandUtils.sendMessage(cc, String.format("'%s' value: '%s'", name, configGetter.apply(cc.getSource()).get().toString()));

        return Command.SINGLE_SUCCESS;
    }

    private int setValue(CommandContext<CommandSourceStack> cc, T value) {
        ForgeConfigSpec.EnumValue<T> config = this.configGetter.apply(cc.getSource());
        config.set(value);
        config.save();

        CommandUtils.sendMessage(cc, String.format("'%s' value is now '%s'", name, configGetter.apply(cc.getSource()).get().toString()));

        return Command.SINGLE_SUCCESS;
    }
}
