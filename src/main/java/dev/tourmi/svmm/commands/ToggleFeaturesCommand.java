package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.ForgeConfigSpec;

import java.text.MessageFormat;

public final class ToggleFeaturesCommand implements ICommand {
    private final boolean disable;
    private final String action;
    private final String status;
    private final String helpText;

    public ToggleFeaturesCommand(boolean disable) {
        this.disable = disable;
        this.action = disable ? "disable" : "enable";
        this.status = disable ? "disabled" : "enabled";

        helpText = MessageFormat.format("""
                - /svmm {0} [giantVein]
                    {0}s the mod or specific features for yourself
                """, action);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal(action)
                .requires(CommandPredicates::isPlayer)
                .requires(CommandPredicates::canPlayerAccessMod)
                .then(Commands.literal("giantVein")
                        .requires(CommandPredicates::isGiantVeinMiningEnabled)
                        .requires(CommandPredicates::canPlayerAccessGiantVeinMining)
                        .executes(this::toggleGiantVein)) // /svmm disable|enable
                .executes(this::toggle); // /svmm disable|enable giantVein
    }

    @Override
    public String getHelpText(CommandContext<CommandSourceStack> cc) {
        if (CommandUtils.isConsole(cc)) {
            return "";
        }

        if (CommandUtils.getSourceConfig(cc).MOD_RESTRICTED.get()) {
            return "";
        }

        return helpText;
    }

    private int toggle(CommandContext<CommandSourceStack> commandContext) {
        if (CommandUtils.isConsole(commandContext)) return 0;

        ClientConfig cfg = CommandUtils.getSourceConfig(commandContext);
        toggle(commandContext, cfg, cfg.MOD_DISABLED);

        CommandUtils.sendMessage(commandContext, MessageFormat.format("Vein Mining is now {0}", status));
        return Command.SINGLE_SUCCESS;
    }

    private int toggleGiantVein(CommandContext<CommandSourceStack> commandContext) {
        if (CommandUtils.isConsole(commandContext)) return 0;

        ClientConfig cfg = CommandUtils.getSourceConfig(commandContext);
        toggle(commandContext, cfg, cfg.GIANT_VEIN_MINING_DISABLED);

        CommandUtils.sendMessage(commandContext, MessageFormat.format("Giant Vein Mining is now {0}", status));
        return Command.SINGLE_SUCCESS;
    }

    private void toggle(CommandContext<CommandSourceStack> cc, ClientConfig cfg, ForgeConfigSpec.BooleanValue toggleFeature) {
        toggleFeature.set(disable);

        if (CommandUtils.isModerator(cc)) {
            resetModeratorRestrictions(cc, cfg);
        }

        toggleFeature.save();
    }

    private void resetModeratorRestrictions(CommandContext<CommandSourceStack> cc, ClientConfig cfg) {
        cfg.MOD_RESTRICTED.set(false);
        cfg.GIANT_VEIN_MINING_RESTRICTED.set(false);

        cfg.SPEC.save();
    }
}
