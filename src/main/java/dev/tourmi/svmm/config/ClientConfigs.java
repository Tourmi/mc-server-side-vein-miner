package dev.tourmi.svmm.config;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.HashMap;
import java.util.UUID;

public class ClientConfigs {
    private static HashMap<UUID, ClientConfig> clientConfigs = new HashMap<>();

    public static ClientConfig getClientConfig(UUID playerUUID) {
        if (!clientConfigs.containsKey(playerUUID)) {
            loadOrCreateClientConfig(playerUUID);
        }
        return clientConfigs.get(playerUUID);
    }

    private static void loadOrCreateClientConfig(UUID playerUUID) {
        ClientConfig cfg = new ClientConfig();
        String configPath = "svmm-player-configs/" + playerUUID.toString() + ".toml";
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, cfg.SPEC, configPath);
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.relative());
        clientConfigs.put(playerUUID, cfg);
    }
}
