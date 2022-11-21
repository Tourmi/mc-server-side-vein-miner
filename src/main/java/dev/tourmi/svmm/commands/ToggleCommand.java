package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;

public class ToggleCommand implements ICommand {
    private final boolean disable;

    private final String moderatorHelpText;
    private final String regularHelpText;

    public ToggleCommand(boolean disable) {
        this.disable = disable;

        moderatorHelpText = String.format("""
                - /svmm %s {player} [giantVein|tunnel|force]
                    %s the mod or specific features to be used by the specified player
                """, disable ? "disable" : "enable", disable ? "restricts" : "allows");
        regularHelpText = String.format("""
                - /svmm %s [giantVein]
                    %s the mod or specific features for yourself
                """, disable ? "disable" : "enable", disable ? "disables" : "enables");
    }

    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal(disable ? "disable" : "enable")
                .requires(cs -> CommandUtils.isModerator(cs) || !CommandUtils.getSourceConfig(cs).MOD_RESTRICTED.get())
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(CommandUtils::isModerator)
                        .executes(this::toggleOther)) // /svmm disable|enable {player}
                .then(Commands.literal("giantVein")
                        .requires(cs -> !SVMMConfig.GIANT_VEIN_MINING_DISABLED.get())
                        .requires(cs -> CommandUtils.isModerator(cs) || !CommandUtils.getSourceConfig(cs).GIANT_VEIN_MINING_RESTRICTED.get())
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(CommandUtils::isModerator)
                                .executes(this::toggleOtherGiantVein)) // /svmm disable|enable giantVein {player}
                        .executes(this::selfToggleGiantVein)) // /svmm disable|enable giantVein
                .then(Commands.literal("tunnel")
                        .requires(cs -> !SVMMConfig.TUNNELING_DISABLED.get())
                        .requires(cs -> CommandUtils.isModerator(cs) || !CommandUtils.getSourceConfig(cs).TUNNELING_RESTRICTED.get())
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(CommandUtils::isModerator)
                                .executes(this::toggleOtherTunneling))) // /svmm disable|enable tunnel {player}
                .executes(this::selfToggle); // /svmm disable|enable
    }

    public int defaultExecute(CommandContext<CommandSourceStack> commandContext) {
        return selfToggle(commandContext);
    }

    public String getHelpText(CommandContext<CommandSourceStack> cc) {
        if (CommandUtils.getSourceConfig(cc).MOD_RESTRICTED.get() && !CommandUtils.isModerator(cc)) {
            return "";
        }

        return regularHelpText + (CommandUtils.isModerator(cc) ? moderatorHelpText : "");
    }

    private int selfToggle(CommandContext<CommandSourceStack> commandContext) {
        if (!checkIsPlayer(commandContext)) return 0;
        if (!selfToggle(commandContext, null, false)) return 0;

        CommandUtils.sendMessage(commandContext, formatMessage("Vein Miner is now %s", disable, "disabled", "enabled"));

        return Command.SINGLE_SUCCESS;
    }

    private int toggleOther(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        cfg.MOD_RESTRICTED.set(disable);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, formatMessage("Vein Miner is now %s for " + player.getName().getString(), disable, "restricted", "allowed"));

        return Command.SINGLE_SUCCESS;
    }

    private int selfToggleGiantVein(CommandContext<CommandSourceStack> commandContext) {
        if (!checkIsPlayer(commandContext)) return 0;

        ClientConfig cfg = CommandUtils.getSourceConfig(commandContext);
        if (!selfToggle(commandContext, cfg.GIANT_VEIN_MINING_DISABLED, cfg.GIANT_VEIN_MINING_RESTRICTED.get())) return 0;

        CommandUtils.sendMessage(commandContext, formatMessage("Vein Miner is now %s for giant veins", disable, "disabled", "enabled"));

        return Command.SINGLE_SUCCESS;
    }

    private int toggleOtherGiantVein(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        cfg.GIANT_VEIN_MINING_RESTRICTED.set(disable);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, formatMessage("Giant Vein Miner is now %s for " + player.getName().getString(), disable, "restricted", "allowed"));

        return Command.SINGLE_SUCCESS;
    }

    private int toggleOtherTunneling(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        cfg.TUNNELING_RESTRICTED.set(disable);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, formatMessage("Tunneling is now %s for " + player.getName().getString(), disable, "restricted", "allowed"));

        return Command.SINGLE_SUCCESS;
    }

    private boolean selfToggle(CommandContext<CommandSourceStack> commandContext, @Nullable ForgeConfigSpec.BooleanValue toggleFeature, boolean restricted) {
        ClientConfig cfg = CommandUtils.getSourceConfig(commandContext);
        boolean isModerator = CommandUtils.isModerator(commandContext);
        if ((cfg.MOD_RESTRICTED.get() || restricted) && !isModerator) {
            CommandUtils.sendMessage(commandContext, "You do not have access to this command.");
            return false;
        }
        if (toggleFeature != null) toggleFeature.set(disable);
        else cfg.MOD_DISABLED.set(disable);
        if (isModerator) resetModeratorRestrictions(cfg);
        cfg.SPEC.save();

        return true;
    }

    private boolean checkIsPlayer(CommandContext<CommandSourceStack> cs) {
        if (!CommandUtils.isFromPlayer(cs)) {
            CommandUtils.sendMessage(cs, "The command needs to target a player.");
            return false;
        }
        return true;
    }

    private void resetModeratorRestrictions(ClientConfig cfg) {
        cfg.MOD_RESTRICTED.set(false);
        cfg.GIANT_VEIN_MINING_RESTRICTED.set(false);
    }

    private String formatMessage(String message, boolean disable, String choice1, String choice2) {
        return String.format(message, disable ? choice1 : choice2);
    }
}
