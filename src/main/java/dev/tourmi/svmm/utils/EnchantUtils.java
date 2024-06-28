package dev.tourmi.svmm.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class EnchantUtils {
    public static int getExp(Level level, ItemStack heldItem, BlockPos pos, BlockState st) {
        return st.getExpDrop(level,
                level.random,
                pos,
                heldItem.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE),
                heldItem.getEnchantmentLevel(Enchantments.SILK_TOUCH));
    }
}
