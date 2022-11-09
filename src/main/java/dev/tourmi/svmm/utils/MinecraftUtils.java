package dev.tourmi.svmm.utils;

import dev.tourmi.svmm.config.SVMMConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;

public class MinecraftUtils {
    public static String getBlockName(BlockState bs) {
        String stateString = bs.toString();
        return stateString.substring(stateString.indexOf('{') + 1, stateString.indexOf('}'));
    }

    public static void mineBlock(Level level, Player player, BlockPos pos, ItemStack heldItem) {
        BlockState st = level.getBlockState(pos);
        heldItem.mineBlock(level, st, pos, player);
        st.getBlock().popExperience((ServerLevel) level, pos, st.getExpDrop(level, level.getRandom(), pos, heldItem.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE), heldItem.getEnchantmentLevel(Enchantments.SILK_TOUCH)));
        st.getBlock().playerDestroy(level, player, pos, st, level.getBlockEntity(pos), heldItem);
        level.removeBlock(pos, false);
    }

    public static void mineBlocks(Level level, Player player, ItemStack heldItem, Collection<BlockPos> blocks) {
        boolean letItemBreak = !SVMMConfig.STOP_WHEN_ABOUT_TO_BREAK.get();

        for (BlockPos pos : blocks) {
            if (shouldStopMining(letItemBreak, heldItem)) break;

            mineBlock(level, player, pos, heldItem);
        }
    }

    private static boolean shouldStopMining(boolean letItemBreak, ItemStack item) {
        if (!letItemBreak && item.getMaxDamage() - item.getDamageValue() <= 2) {
            return true;
        }

        return item.isEmpty();
    }
}
