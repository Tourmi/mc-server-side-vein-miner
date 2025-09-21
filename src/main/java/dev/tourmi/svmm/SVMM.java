package dev.tourmi.svmm;

import com.mojang.logging.LogUtils;
import dev.tourmi.svmm.commands.Commands;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.server.ClientStatus;
import dev.tourmi.svmm.server.ItemTeleporter;
import dev.tourmi.svmm.server.Tunneler;
import dev.tourmi.svmm.server.VeinMiner;
import dev.tourmi.svmm.utils.ModUtils;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(SVMM.MOD_ID)
public final class SVMM
{
    public static final String MOD_ID = "svmm";

    private static final Logger LOGGER = LogUtils.getLogger();

    private final VeinMiner veinMiner = new VeinMiner();

    public SVMM()
    {
        ModUtils.RegisterMod(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("Server-side Vein Mining successfully started");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        Commands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public boolean onBlockBroken(BlockEvent.BreakEvent event) {
        return veinMiner.onBlockBroken(event);
    }

    @SubscribeEvent
    public void onPlayerMine(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getSide().isClient()) return;

        Tunneler.updateFaceMined(event.getEntity().getUUID(), event.getFace());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Ensures a player's config is created as they join.
        ClientConfigs.getClientConfig((event.getEntity()));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ClientStatus.getClientStatus(event.getEntity().getUUID()).reset();
    }

    @SubscribeEvent
    public void onItemSpawn(EntityEvent.EntityConstructing event) {
        ItemTeleporter.checkAndTeleport(event.getEntity());
    }
}
