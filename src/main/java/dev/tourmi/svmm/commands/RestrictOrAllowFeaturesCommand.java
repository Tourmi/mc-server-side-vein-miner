package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;

import java.text.MessageFormat;

public final class RestrictOrAllowFeaturesCommand implements ICommand {
    private final boolean restrict;
    private final String action;
    private final String status;
    private final String helpText;

    public RestrictOrAllowFeaturesCommand(boolean restrict) {
        this.restrict = restrict;
        this.action = restrict ? "restrict" : "allow";
        this.status = restrict ? "restricted" : "allowed";

        helpText = MessageFormat.format("""
                - /svmm {0} '{player}'
                - /svmm {0} [giantVein|tunnel|force] '{player}'
                    {0}s the mod or specific features for the given player
                """, action);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal(action)
                .requires(CommandPredicates::isModerator)
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(this::setModRestriction)) // /svmm restrict|allow {player}
                .then(Commands.literal("giantVein")
                        .requires(CommandPredicates::isGiantVeinMiningEnabled)
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::setGiantVeinRestriction)) // /svmm restrict|allow giantVein {player}
                .then(Commands.literal("tunnel")
                        .requires(CommandPredicates::isTunnelingEnabled)
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::setTunnelingRestriction))) // /svmm restrict|allow tunnel {player}
                .then(Commands.literal("force")
                        .requires(CommandPredicates::isForceVeinMiningEnabled)
                        .requires(CommandPredicates::canPlayerAccessForceVeinMining)
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(this::setForceRestriction)))); // /svmm restrict|allow force {player}
    }

    @Override
    public String getHelpText(CommandContext<CommandSourceStack> cc) {
        if (!CommandUtils.isModerator(cc)) {
            return "";
        }

        return helpText;
    }

    private int setModRestriction(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player);
        cfg.MOD_RESTRICTED.set(restrict);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, MessageFormat.format("Vein Miner is now {0} for {1}.", status, player.getName().getString()));

        return Command.SINGLE_SUCCESS;
    }

    private int setGiantVeinRestriction(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player);
        cfg.GIANT_VEIN_MINING_RESTRICTED.set(restrict);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, MessageFormat.format("Giant Vein Mining is now {0} for {1}.", status, player.getName().getString()));

        return Command.SINGLE_SUCCESS;
    }

    private int setTunnelingRestriction(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player);
        cfg.TUNNELING_RESTRICTED.set(restrict);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, MessageFormat.format("Tunneling is now {0} for {1}.", status, player.getName().getString()));

        return Command.SINGLE_SUCCESS;
    }

    private int setForceRestriction(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Entity player = EntityArgument.getEntity(commandContext, "player");

        ClientConfig cfg = ClientConfigs.getClientConfig(player);
        cfg.FORCE_RESTRICTED.set(restrict);
        cfg.SPEC.save();

        CommandUtils.sendMessage(commandContext, MessageFormat.format("Force Vein Mining is now {0} for {1}.", status, player.getName().getString()));

        return Command.SINGLE_SUCCESS;
    }
}
