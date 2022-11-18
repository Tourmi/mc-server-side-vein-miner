package dev.tourmi.svmm.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.server.ClientStatus;
import dev.tourmi.svmm.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.awt.*;

public class ForceCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return net.minecraft.commands.Commands.literal("force")
                .executes(ForceCommand::execute); // /svmm force
    }

    public static int execute(CommandContext<CommandSourceStack> cc) {
        if (!checkIsAllowed(cc)) return 0;
        Player player = cc.getSource().getPlayer();
        ClientStatus status = ClientStatus.getClientStatus(player.getUUID());
        status.forceNext = !status.forceNext;
        String message = status.forceNext ? "Next block mined without holding shift will be vein mined" : "Cancelled vein mine";
        player.sendSystemMessage(Component.literal(message));
        return Command.SINGLE_SUCCESS;
    }

    private static boolean checkIsAllowed(CommandContext<CommandSourceStack> cc) {
        if (!cc.getSource().isPlayer())  {
            CommandUtils.sendMessage(cc, "Only players may use this command");
            return false;
        }

        ClientConfig cfg = CommandUtils.getSourceConfig(cc);
        boolean isModerator = CommandUtils.isModerator(cc);
        boolean isAllowed = isModerator || !(SVMMConfig.FORCE_DISABLED.get() || cfg.FORCE_RESTRICTED.get() || cfg.MOD_RESTRICTED.get());
        if (!isAllowed) {
            CommandUtils.sendMessage(cc, "You do not have access to this command.");
        }
        return isAllowed;
    }
}
