package dev.tourmi.svmm.config;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

public final class ClientConfigs {
    private final static HashMap<UUID, ClientConfig> clientConfigs = new HashMap<>();

    public static ClientConfig getClientConfig(Entity player) {
        if (!clientConfigs.containsKey(player.getUUID())) {
            loadOrCreateClientConfig(player);
        }

        return clientConfigs.get(player.getUUID());
    }

    private static void loadOrCreateClientConfig(Entity player) {
        UUID playerUUID = player.getUUID();
        ClientConfig cfg = new ClientConfig(playerUUID);
        String configPath = "svmm-player-configs/" + playerUUID.toString() + ".toml";
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, cfg.SPEC, configPath);
        Path serverConfig = player.getServer().getWorldPath(new LevelResource("serverconfig"));
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, serverConfig);
        clientConfigs.put(playerUUID, cfg);
    }
}
