package dev.tourmi.svmm.server;

import com.mojang.logging.LogUtils;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.MinecraftUtils;
import dev.tourmi.svmm.utils.Utils3D;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import org.slf4j.Logger;


public class VeinMiner {
    private static final Logger LOGGER = LogUtils.getLogger();

    public void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (SVMMConfig.MOD_DISABLED.get()) return;

        Player player = event.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        BlockState blockState = event.getState();
        if (!canUseMod(player, heldItem, blockState)) return;

        Level level = player.getLevel();
        BlockPos blockPos = event.getPos();

        if (canTunnel(player, blockState)) {
            doTunnel(player, level, heldItem, blockState, blockPos);
        }
        else if (canGiantVeinMine(player, blockState)) {
            doGiantVeinMine(player, level, heldItem, blockPos);
        }
        else if (canVeinMine(blockState)) {
            doVeinMine(player, level, heldItem, blockState, blockPos);
        } else {
            return;
        }

        event.setCanceled(true);
    }

    private boolean canUseMod(Player player, ItemStack heldItem, BlockState minedBlockState) {
        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());

        if (cfg.MOD_DISABLED.get()) return false;
        if (cfg.MOD_RESTRICTED.get()) return false;
        if (player.isCreative()) return false;
        if (player.isShiftKeyDown()) return false;
        return heldItem.isCorrectToolForDrops(minedBlockState);
    }

    private boolean canTunnel(Player player, BlockState blockState) {
        if (SVMMConfig.TUNNELING_DISABLED.get()) return false;

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        if (cfg.TUNNELING_RESTRICTED.get()) return false;

        if (!Tunneler.canTunnel(player.getUUID())) return false;

        if (!SVMMConfig.TUNNELING_WHITELIST.get().contains(MinecraftUtils.getBlockName(blockState))) {
            Tunneler.cancelTunnel(player.getUUID());
            player.sendSystemMessage(Component.literal("Cancelled tunneling since the block that was mined is not part of the allowed list of blocks"));
            return false;
        }

        return true;
    }

    private void doTunnel(Player player, Level level, ItemStack heldItem, BlockState blockState, BlockPos blockPos) {
        Tunneler.TunnelerStatus status = Tunneler.doTunnel(player, level, heldItem, blockState, blockPos);

        if (SVMMConfig.TUNNELING_LOG_USAGE.get()) {
            LOGGER.info("Player {} tunneled at position {} {} {}, mining {} blocks.", player.getName(), status.tunnelPosition.getX(), status.tunnelPosition.getY(), status.tunnelPosition.getZ(), status.blocksMined);
        }
    }

    private boolean canVeinMine(BlockState minedBlockState) {
        return SVMMConfig.BLOCK_WHITELIST.get().contains(MinecraftUtils.getBlockName(minedBlockState));
    }

    private void doVeinMine(Player player, Level level, ItemStack heldItem, BlockState blockState, BlockPos blockPos) {
        MinecraftUtils.mineBlocks(level, player, heldItem, Utils3D.getVeinBlocks(blockState, blockPos, level, SVMMConfig.MAXIMUM_BLOCKS_TO_BREAK.get()));
    }

    private boolean canGiantVeinMine(Player player, BlockState minedBlockState) {
        if (SVMMConfig.GIANT_VEIN_MINING_DISABLED.get()) return false;

        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());
        if (cfg.GIANT_VEIN_MINING_DISABLED.get()) return false;
        if (cfg.GIANT_VEIN_MINING_RESTRICTED.get()) return false;

        return SVMMConfig.GIANT_VEIN_STARTER_BLOCKS.get().contains(MinecraftUtils.getBlockName(minedBlockState));
    }

    private void doGiantVeinMine(Player player, Level level, ItemStack heldItem, BlockPos blockPos) {
        MinecraftUtils.mineBlocks(level, player, heldItem, Utils3D.getGiantVeinBlocks(blockPos, level, SVMMConfig.MAXIMUM_BLOCKS_TO_BREAK.get()));
    }
}
