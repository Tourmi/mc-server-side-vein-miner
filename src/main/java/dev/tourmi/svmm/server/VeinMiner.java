package dev.tourmi.svmm.server;

import com.mojang.logging.LogUtils;
import dev.tourmi.svmm.config.ClientConfig;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.MinecraftUtils;
import dev.tourmi.svmm.utils.Utils3D;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import org.slf4j.Logger;

import java.util.Collection;

public class VeinMiner {
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (SVMMConfig.MOD_DISABLED.get()) return;

        Player player = event.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        BlockState blockState = event.getState();
        if (!canUseMod(player, heldItem, blockState)) return;

        Level level = player.getLevel();
        BlockPos blockPos = event.getPos();

        if (canGiantVeinMine(player, blockState)) {
            doGiantVeinMine(player, level, heldItem, blockPos);
        }
        else if (canVeinMine(blockState)) {
            doVeinMine(player, level, heldItem, blockState, blockPos);
        }
    }

    private boolean canUseMod(Player player, ItemStack heldItem, BlockState minedBlockState) {
        ClientConfig cfg = ClientConfigs.getClientConfig(player.getUUID());

        if (cfg.MOD_DISABLED.get()) return false;
        if (cfg.MOD_RESTRICTED.get()) return false;
        if (player.isCreative()) return false;
        if (player.isShiftKeyDown()) return false;
        return heldItem.isCorrectToolForDrops(minedBlockState);
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
