package dev.tourmi.svmm.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class EnchantUtils {
    public static int getExp(Level level, ItemStack heldItem, BlockPos pos, BlockState st) {
        var lookup = level.holderLookup(Registries.ENCHANTMENT);
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(Enchantments.FORTUNE), heldItem);
        int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(Enchantments.SILK_TOUCH), heldItem);
        return st.getExpDrop(level, level.random, pos, fortuneLevel, silkTouchLevel);
    }
}
