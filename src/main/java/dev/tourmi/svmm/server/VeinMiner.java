package dev.tourmi.svmm.server;

import com.mojang.logging.LogUtils;

import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.MinecraftUtils;
import dev.tourmi.svmm.utils.Utils3D;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import org.slf4j.Logger;

import java.util.List;


public final class VeinMiner {
    private static final Logger LOGGER = LogUtils.getLogger();

    public boolean onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return false;
        if (SVMMConfig.MOD_DISABLED.get()) return false;

        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        BlockState blockState = event.getState();
        if (!canUseMod(player, heldItem, blockState)) return false;

        ServerLevel level = player.level();
        BlockPos blockPos = event.getPos();

        if (canTunnel(player, blockState)) {
            doTunnel(player, level, heldItem, blockState, blockPos);
        }
        else if (canGiantVeinMine(player, blockState)) {
            doGiantVeinMine(player, level, heldItem, blockPos);
        }
        else if (canVeinMine(player, blockState)) {
            doVeinMine(player, level, heldItem, blockState, blockPos);
        } else {
            return false;
        }

        return true;
    }

    private boolean canUseMod(Player player, ItemStack heldItem, BlockState minedBlockState) {
        ClientConfig cfg = ClientConfigs.getClientConfig(player);

        if (cfg.MOD_DISABLED.get()) return false;
        if (cfg.MOD_RESTRICTED.get()) return false;
        if (player.isCreative()) return false;
        if (!cfg.TRIGGER_WHEN.get().shouldTrigger(player)) return false;

        return heldItem.isCorrectToolForDrops(minedBlockState);
    }

    private boolean canTunnel(ServerPlayer player, BlockState blockState) {
        if (SVMMConfig.TUNNELING_DISABLED.get()) return false;

        ClientConfig cfg = ClientConfigs.getClientConfig(player);
        if (cfg.TUNNELING_RESTRICTED.get()) return false;

        if (!Tunneler.canTunnel(player.getUUID())) return false;

        if (!canMine(blockState, SVMMConfig.TUNNELING_WHITELIST.get(), SVMMConfig.TUNNELING_BLACKLIST.get())) {
            Tunneler.cancelTunnel(player.getUUID());
            player.sendSystemMessage(Component.literal("Cancelled tunneling since the block that was mined is not part of the allowed list of blocks"));
            return false;
        }

        return true;
    }

    private void doTunnel(ServerPlayer player, ServerLevel level, ItemStack heldItem, BlockState blockState, BlockPos blockPos) {
        ClientStatus status = Tunneler.doTunnel(player, level, heldItem, blockState, blockPos);

        if (SVMMConfig.TUNNELING_LOG_USAGE.get()) {
            LOGGER.info("Player {} tunneled at position {} {} {}, mining {} blocks.", player.getName(), status.lastPosition.getX(), status.lastPosition.getY(), status.lastPosition.getZ(), status.lastBlocksMined);
        }
    }

    private boolean canVeinMine(ServerPlayer player, BlockState minedBlockState ) {
        ClientStatus status = ClientStatus.getClientStatus(player.getUUID());
        if (status.forceNext) {
            boolean canForceMine = !MinecraftUtils.isBlockInList(minedBlockState, SVMMConfig.FORCE_BLACKLIST.get());
            if (!canForceMine) {
                player.sendSystemMessage(Component.literal("You may not force vein mine this block"));
                status.forceNext = false;
            }
            return canForceMine;
        }

        return canMine(minedBlockState, SVMMConfig.BLOCK_WHITELIST.get(), SVMMConfig.BLOCK_BLACKLIST.get());
    }

    private boolean canMine(BlockState blockState, List<? extends String> whitelist, List<? extends String> blacklist) {
        if (MinecraftUtils.isBlockInList(blockState, whitelist)) {
            return !MinecraftUtils.isBlockInList(blockState, blacklist);
        }

        return false;
    }

    private void doVeinMine(ServerPlayer player, ServerLevel level, ItemStack heldItem, BlockState blockState, BlockPos blockPos) {
        MinecraftUtils.mineBlocks(level, player, heldItem, Utils3D.getVeinBlocks(blockState, blockPos, level, SVMMConfig.MAXIMUM_BLOCKS_TO_BREAK.get()));

        ClientStatus status = ClientStatus.getClientStatus(player.getUUID());
        if (status.forceNext) {
            status.forceNext = false;

            player.sendSystemMessage(Component.literal("Force vein mine completed"));

            if (SVMMConfig.FORCE_LOG_USAGE.get()) {
                LOGGER.info(player.getName().getString() + " force mined block " + MinecraftUtils.getBlockName(blockState) + " at position " + blockPos.toShortString());
            }
        }
    }

    private boolean canGiantVeinMine(ServerPlayer player, BlockState minedBlockState) {
        if (SVMMConfig.GIANT_VEIN_MINING_DISABLED.get()) return false;

        ClientConfig cfg = ClientConfigs.getClientConfig(player);
        if (cfg.GIANT_VEIN_MINING_DISABLED.get()) return false;
        if (cfg.GIANT_VEIN_MINING_RESTRICTED.get()) return false;

        if (ClientStatus.getClientStatus(player.getUUID()).forceNext) return false;

        return canMine(minedBlockState, SVMMConfig.GIANT_VEIN_STARTER_BLOCKS.get(), SVMMConfig.GIANT_VEIN_BLACKLIST.get());
    }

    private void doGiantVeinMine(ServerPlayer player, ServerLevel level, ItemStack heldItem, BlockPos blockPos) {
        MinecraftUtils.mineBlocks(level, player, heldItem, Utils3D.getGiantVeinBlocks(blockPos, level, SVMMConfig.MAXIMUM_BLOCKS_TO_BREAK.get()));
    }
}
