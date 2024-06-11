package dev.tourmi.svmm.server;

import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.utils.MinecraftUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ItemTeleporter {
    public static void checkAndTeleport(Entity entity) {
        if (!SVMMConfig.TELEPORT_ITEMS_TO_PLAYER.get()) return;
        if (!(entity instanceof ItemEntity item)) return;
        Level l = entity.getCommandSenderWorld();
        if (l.isClientSide) return;
        l.getServer().execute(() -> {
            if (!item.isAlive()) return;
            BlockPos pos = new BlockPos(item.getBlockX(), item.getBlockY(), item.getBlockZ());
            Player p = MinecraftUtils.BLOCKS_MINED.getOrDefault(pos, null);
            if (p == null) return;

            item.setPos(p.getPosition(1));
            MinecraftUtils.BLOCKS_MINED.remove(pos);
        });
    }
}
