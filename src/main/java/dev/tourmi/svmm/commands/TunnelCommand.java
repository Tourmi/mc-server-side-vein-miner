package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.server.ClientStatus;
import dev.tourmi.svmm.server.Tunneler;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TunnelCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand(){
        return Commands.literal("tunnel")
                .then(Commands.literal("cancel")
                        .executes(TunnelCommand::cancelTunnel)) // /svmm tunnel cancel
                .then(Commands.argument("width", IntegerArgumentType.integer(1))
                        .then(Commands.argument("height", IntegerArgumentType.integer(1))
                                .then(Commands.argument("maxDepth", IntegerArgumentType.integer(1))
                                        .executes(TunnelCommand::tunnelWidthHeightMaxDepth)) // /svmm tunnel {width} {height} {maxDepth}
                                .executes(TunnelCommand::tunnelWidthHeight))) // /svmm tunnel {width} {height}
                .executes(TunnelCommand::tunnel); // /svmm tunnel
    }

    private static int tunnel(CommandContext<CommandSourceStack> cc) {
        if (!checkIsAllowed(cc)) return 0;

        ClientStatus status = Tunneler.toggleTunneler(cc.getSource().getEntity().getUUID());
        CommandUtils.sendMessage(cc, status.lastMessage);

        return Command.SINGLE_SUCCESS;
    }

    private static int tunnelWidthHeight(CommandContext<CommandSourceStack> cc) {
        if (!checkIsAllowed(cc)) return 0;

        ClientStatus status = Tunneler.toggleTunneler(cc.getSource().getEntity().getUUID(),
                cc.getArgument("width", Integer.class),
                cc.getArgument("height", Integer.class));
        CommandUtils.sendMessage(cc, status.lastMessage);

        return Command.SINGLE_SUCCESS;
    }

    private static int tunnelWidthHeightMaxDepth(CommandContext<CommandSourceStack> cc) {
        if (!checkIsAllowed(cc)) return 0;

        ClientStatus status = Tunneler.toggleTunneler(cc.getSource().getEntity().getUUID(),
                cc.getArgument("width", Integer.class),
                cc.getArgument("height", Integer.class),
                cc.getArgument("maxDepth", Integer.class));
        CommandUtils.sendMessage(cc, status.lastMessage);

        return Command.SINGLE_SUCCESS;
    }

    private static int cancelTunnel(CommandContext<CommandSourceStack> cc) {
        if (!checkIsAllowed(cc)) return 0;

        Tunneler.cancelTunnel(cc.getSource().getEntity().getUUID());

        return Command.SINGLE_SUCCESS;
    }

    private static boolean checkIsAllowed(CommandContext<CommandSourceStack> cc) {
        if (!cc.getSource().isPlayer())  {
            CommandUtils.sendMessage(cc, "Only players may use this command");
            return false;
        }

        ClientConfig cfg = CommandUtils.getSourceConfig(cc);
        boolean isModerator = CommandUtils.isModerator(cc);
        boolean isAllowed = isModerator || !(SVMMConfig.TUNNELING_DISABLED.get() || cfg.TUNNELING_RESTRICTED.get() || cfg.MOD_RESTRICTED.get());
        if (!isAllowed) {
            CommandUtils.sendMessage(cc, "You do not have access to this command.");
        }
        return isAllowed;
    }
}
