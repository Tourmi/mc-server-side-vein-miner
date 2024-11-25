package dev.tourmi.svmm.utils;

import dev.tourmi.svmm.config.SVMMConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public final class MinecraftUtils {
    public static final HashMap<BlockPos, Player> BLOCKS_MINED = new HashMap<>();

    public static Stream<String> getColorNames() {
        return Stream.of(
                "white",
                "orange",
                "magenta",
                "light_blue",
                "yellow",
                "lime",
                "pink",
                "gray",
                "light_gray",
                "cyan",
                "purple",
                "blue",
                "brown",
                "green",
                "red",
                "black");
    }

    public static String getBlockName(BlockState bs) {
        String stateString = bs.toString();
        if (!stateString.contains("{") || !stateString.contains("}")) {
            return stateString;
        }
        return stateString.substring(stateString.indexOf('{') + 1, stateString.indexOf('}'));
    }

    public static boolean isBlockInList(BlockState bs, List<? extends String> list) {
        if (list.contains(MinecraftUtils.getBlockName(bs))) {
            return true;
        }
        var tags = list.stream().filter((e) -> e.startsWith("#")).map(e -> e.substring(1)).toList();
        return tags
                .stream()
                .map((t) -> ForgeRegistries.BLOCKS.tags().createTagKey(ResourceLocation.parse(t)))
                .anyMatch(bs::is);
    }

    public static void mineBlock(Level level, Player player, BlockPos blockPos, ItemStack heldItem) {
        if (level.isClientSide()) return;

        var blockState = level.getBlockState(blockPos);
        heldItem.mineBlock(level, blockState, blockPos, player);
        blockState.getBlock().popExperience((ServerLevel) level, blockPos, EnchantUtils.getExp(level, heldItem, blockPos, blockState));
        blockState.getBlock().playerDestroy(level, player, blockPos, blockState, level.getBlockEntity(blockPos), heldItem);
        level.removeBlock(blockPos, false);

		if (SVMMConfig.TELEPORT_ITEMS_TO_PLAYER.get()) {
			BLOCKS_MINED.put(blockPos, player);

			((ServerLevel) level).getServer().execute(() -> {
				BLOCKS_MINED.remove(blockPos);
			});
		}
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
