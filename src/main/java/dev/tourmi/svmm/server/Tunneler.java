package dev.tourmi.svmm.server;

import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.MinecraftUtils;
import dev.tourmi.svmm.utils.Utils3D;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tunneler {
    public static class TunnelerStatus {
        public boolean tunnelNextBlock = false;
        public int width = 1;
        public int height = 2;
        public int deep = Integer.MAX_VALUE;
        public Direction faceMined;

        public String lastMessage = "";
        public int blocksMined = 0;
        public BlockPos tunnelPosition;
    }

    private static Map<UUID, TunnelerStatus> playerTunnelerStatuses = new HashMap<>();

    public static TunnelerStatus toggleTunneler(UUID playerUUID) {
        TunnelerStatus status = getTunnelerStatus(playerUUID);
        return toggleTunneler(playerUUID, status.width, status.height, status.deep);
    }

    public static TunnelerStatus toggleTunneler(UUID playerUUID, int width, int height) {
        return toggleTunneler(playerUUID, width, height, Integer.MAX_VALUE);
    }

    public static TunnelerStatus toggleTunneler(UUID playerUUID, int width, int height, int maxDepth) {
        TunnelerStatus status = getTunnelerStatus(playerUUID);

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

        status.width = width;
        status.height = height;
        status.deep = Integer.min(Integer.min( SVMMConfig.TUNNELING_MAX_BLOCKS.get() / (width * height), maxDepth), SVMMConfig.TUNNELING_MAX_DEPTH.get());
        status.lastMessage = String.format("The next block mined while not holding sneak will create a %dx%d tunnel that's maximum %d blocks deep.", width, height, status.deep);

        return status;
    }

    public static boolean canTunnel(UUID playerUUID) {
        return getTunnelerStatus(playerUUID).tunnelNextBlock;
    }

    public static void cancelTunnel(UUID playerUUID) {
        getTunnelerStatus(playerUUID).tunnelNextBlock = false;
    }

    public static void updateFaceMined(UUID playerUUID, Direction face) {
        getTunnelerStatus(playerUUID).faceMined = face;
    }

    public static TunnelerStatus doTunnel(Player player, Level level, ItemStack heldItem, BlockState blockState, BlockPos blockPos) {
        TunnelerStatus status = getTunnelerStatus(player.getUUID());
        status.tunnelNextBlock = false;

        Collection<BlockPos> blocks;
        if (status.faceMined.getAxis() == Direction.Axis.Y) {
            blocks = Utils3D.getVerticalTunnelBlocks(blockPos, blockState, level, player.getDirection(), status.faceMined, status.width, status.height, status.deep);
        } else {
            blocks = Utils3D.getTunnelBlocks(blockPos, blockState, level, status.faceMined, status.width, status.height, status.deep);
        }
        MinecraftUtils.mineBlocks(level, player, heldItem, blocks);

        status.blocksMined = blocks.size();
        status.tunnelPosition = blockPos;
        player.sendSystemMessage(Component.literal(String.format("Mined %d blocks", status.blocksMined)));
        return status;
    }

    private static TunnelerStatus getTunnelerStatus(UUID playerUUID) {
        if (!playerTunnelerStatuses.containsKey(playerUUID)) {
            playerTunnelerStatuses.put(playerUUID, new TunnelerStatus());
        }
        return playerTunnelerStatuses.get(playerUUID);
    }
}
