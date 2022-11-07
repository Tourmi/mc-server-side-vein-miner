package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class DisableCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return net.minecraft.commands.Commands.literal("disable")
                .executes(DisableCommand::executes);
    }

    public static int executes(CommandContext<CommandSourceStack> commandContext) {
        if (!(commandContext.getSource().getEntity() instanceof Player)) {
            Commands.sendMessage(commandContext, "The disable command needs to be run by a player.");
            return 0;
        }

        ClientConfig cfg = ClientConfigs.getClientConfig(commandContext.getSource().getPlayer().getUUID());
        cfg.MOD_DISABLED.set(true);
        cfg.SPEC.save();

        Commands.sendMessage(commandContext, "Vein Miner is now disabled");

        return Command.SINGLE_SUCCESS;
    }
}
