package dev.tourmi.svmm.server;

import com.mojang.logging.LogUtils;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.config.SVMMConfig;
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

        Player player = event.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        BlockState blockState = event.getState();
        if (!canVeinMine(player, heldItem, blockState)) return;

        doVeinMine(player, player.getLevel(), heldItem, blockState, event.getPos());
    }

    private boolean canVeinMine(Player player, ItemStack heldItem, BlockState minedBlockState) {
        if (ClientConfigs.getClientConfig(player.getUUID()).MOD_DISABLED.get()) return false;
        if (ClientConfigs.getClientConfig(player.getUUID()).MOD_RESTRICTED.get()) return false;
        if (player.isCreative()) return false;
        if (player.isShiftKeyDown()) return false;
        if (!heldItem.isCorrectToolForDrops(minedBlockState)) return false;

        String blockName = minedBlockState.toString().substring(6, minedBlockState.toString().length() - 1);
        if (!SVMMConfig.BLOCK_WHITELIST.get().contains(blockName)) return false;

        return true;
    }

    private void doVeinMine(Player player, Level level, ItemStack heldItem, BlockState blockState, BlockPos blockPos) {
        boolean letItemBreak = !SVMMConfig.STOP_WHEN_ABOUT_TO_BREAK.get();

        Collection<BlockPos> blocks = Utils3D.getVeinBlocks(blockState, blockPos, level, SVMMConfig.MAXIMUM_BLOCKS_TO_BREAK.get());
        for (BlockPos pos : blocks) {
            if (!letItemBreak && heldItem.getMaxDamage() - heldItem.getDamageValue() <= 2) {
                break;
            }
            if (heldItem.isEmpty()) {
                break;
            }

            BlockState st = level.getBlockState(pos);
            heldItem.mineBlock(level, st, pos, player);
            st.getBlock().popExperience((ServerLevel) level, pos, st.getExpDrop(level, level.getRandom(), pos, heldItem.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE), heldItem.getEnchantmentLevel(Enchantments.SILK_TOUCH)));
            st.getBlock().playerDestroy(level, player, pos, st, level.getBlockEntity(pos), heldItem);
            level.removeBlock(pos, false);
        }
    }
}
