package dev.tourmi.svmm.server;

import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.config.TriggerActions;
import dev.tourmi.svmm.utils.MinecraftUtils;
import dev.tourmi.svmm.utils.Utils3D;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Collection;
import java.util.UUID;

public final class Tunneler {
    public static ClientStatus toggleTunneler(UUID playerUUID) {
        ClientStatus status = ClientStatus.getClientStatus(playerUUID);
        return toggleTunneler(playerUUID, status.tunnelWidth, status.tunnelHeight, status.tunnelDeep);
    }

    public static ClientStatus toggleTunneler(UUID playerUUID, int width, int height) {
        return toggleTunneler(playerUUID, width, height, Integer.MAX_VALUE);
    }

    public static ClientStatus toggleTunneler(UUID playerUUID, int width, int height, int maxDepth) {
        ClientStatus status = ClientStatus.getClientStatus(playerUUID);

        status.tunnelNextBlock = !status.tunnelNextBlock;
        if (!status.tunnelNextBlock) {
            status.lastMessage = "Cancelled tunneling";
            return status;
        }
        int maximumDimensions = SVMMConfig.TUNNELING_MAX_DIMENSION.get();
        if (width > maximumDimensions || height > maximumDimensions) {
            status.lastMessage = String.format("%dx%d dimensions are invalid for a tunnel. The maximum amount of blocks per dimension is %d", width, height, maximumDimensions);
            status.tunnelNextBlock = false;
            return status;
        }

        status.tunnelWidth = width;
        status.tunnelHeight = height;
        status.tunnelDeep = Integer.min(Integer.min( SVMMConfig.TUNNELING_MAX_BLOCKS.get() / (width * height), maxDepth), SVMMConfig.TUNNELING_MAX_DEPTH.get());
        status.lastMessage = String.format("The next block mined will create a %dx%d tunnel that's maximum %d blocks deep.", width, height, status.tunnelDeep);
        status.forceNext = false;

        return status;
    }

    public static boolean canTunnel(UUID playerUUID) {
        return ClientStatus.getClientStatus(playerUUID).tunnelNextBlock;
    }

    public static void cancelTunnel(UUID playerUUID) {
        ClientStatus.getClientStatus(playerUUID).tunnelNextBlock = false;
    }

    public static void updateFaceMined(UUID playerUUID, Direction face) {
        ClientStatus.getClientStatus(playerUUID).tunnelFaceMined = face;
    }

    public static ClientStatus doTunnel(ServerPlayer player, ServerLevel level, ItemStack heldItem, BlockState blockState, BlockPos blockPos) {
        ClientStatus status = ClientStatus.getClientStatus(player.getUUID());
        status.tunnelNextBlock = false;

        Collection<BlockPos> blocks;
        if (status.tunnelFaceMined.getAxis() == Direction.Axis.Y) {
            blocks = Utils3D.getVerticalTunnelBlocks(blockPos, blockState, level, player.getDirection(), status.tunnelFaceMined, status.tunnelWidth, status.tunnelHeight, status.tunnelDeep);
        } else {
            blocks = Utils3D.getTunnelBlocks(blockPos, blockState, level, status.tunnelFaceMined, status.tunnelWidth, status.tunnelHeight, status.tunnelDeep);
        }
        MinecraftUtils.mineBlocks(level, player, heldItem, blocks);

        status.lastBlocksMined = blocks.size();
        status.lastPosition = blockPos;
        player.sendSystemMessage(Component.literal(String.format("Mined %d blocks", status.lastBlocksMined)));

        return status;
    }

}
