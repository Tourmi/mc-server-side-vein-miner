package dev.tourmi.svmm;

import com.mojang.logging.LogUtils;
import dev.tourmi.svmm.commands.Commands;
import dev.tourmi.svmm.config.ClientConfigs;
import dev.tourmi.svmm.config.SVMMConfig;
import dev.tourmi.svmm.server.ItemTeleporter;
import dev.tourmi.svmm.server.Tunneler;
import dev.tourmi.svmm.server.VeinMiner;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(SVMM.MODID)
public class SVMM
{
    public static final String MODID = "svmm";
    private static final Logger LOGGER = LogUtils.getLogger();

    private VeinMiner veinMiner;

    public SVMM()
    {
        veinMiner = new VeinMiner();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SVMMConfig.SPEC, "svmm-config.toml");

        MinecraftForge.EVENT_BUS.register(this);
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
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        veinMiner.onBlockBroken(event);
    }

    @SubscribeEvent
    public void onPlayerMine(PlayerInteractEvent event) {
        if (event.getSide().isClient()) return;

        Tunneler.updateFaceMined(event.getEntity().getUUID(), event.getFace());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ClientConfigs.getClientConfig(event.getEntity().getUUID());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemSpawn(EntityEvent.EntityConstructing event) {
        ItemTeleporter.checkAndTeleport(event.getEntity());
    }
}
