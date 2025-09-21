package dev.tourmi.svmm.server;

import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.MinecraftUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public final class ItemTeleporter {
    public static void checkAndTeleport(Entity entity) {
        var server = entity.getServer();
        if (server == null) return;
        if (!SVMMConfig.TELEPORT_ITEMS_TO_PLAYER.get()) return;
        if (!(entity instanceof ItemEntity item)) return;

        server.execute(() -> {
            if (!item.isAlive()) return;

            BlockPos pos = new BlockPos(item.getBlockX(), item.getBlockY(), item.getBlockZ());
            Player player = MinecraftUtils.BLOCKS_MINED.getOrDefault(pos, null);
            if (player == null) return;
            player.getInventory().add(item.getItem());

            item.setPos(player.getPosition(1));
            MinecraftUtils.BLOCKS_MINED.remove(pos);
        });
    }
}
