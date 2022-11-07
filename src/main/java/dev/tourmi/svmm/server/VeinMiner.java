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
        if (ClientConfigs.getClientConfig(player.getUUID()).MOD_DISABLED.get()) return;

        BlockState state = event.getState();
        if (player.isCreative()) return;
        if (player.isShiftKeyDown()) return;

        String blockName = state.toString().substring(6, state.toString().length() - 1);
        if (!SVMMConfig.BLOCK_WHITELIST.get().contains(blockName)) {
            return;
        }

        ItemStack currItem = player.getMainHandItem();
        if (!currItem.isCorrectToolForDrops(state)) {
            return;
        }

        Level level = player.getLevel();

        boolean letItemBreak = !SVMMConfig.STOP_WHEN_ABOUT_TO_BREAK.get();

        Collection<BlockPos> blocks = Utils3D.getVeinBlocks(state, event.getPos(), level, SVMMConfig.MAXIMUM_BLOCKS_TO_BREAK.get());
        for (BlockPos pos : blocks) {
            if (!letItemBreak && currItem.getMaxDamage() - currItem.getDamageValue() <= 2) {
                break;
            }
            if (currItem.isEmpty()) {
                break;
            }

            BlockState st = level.getBlockState(pos);
            currItem.mineBlock(level, st, pos, player);
            st.getBlock().popExperience((ServerLevel) level, pos, st.getExpDrop(level, level.getRandom(), pos, currItem.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE), currItem.getEnchantmentLevel(Enchantments.SILK_TOUCH)));
            st.getBlock().playerDestroy(level, player, pos, st, level.getBlockEntity(pos), currItem);
            level.removeBlock(pos, false);
        }
    }
}
