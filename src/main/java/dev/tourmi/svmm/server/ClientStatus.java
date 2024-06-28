package dev.tourmi.svmm.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientStatus {
    public boolean tunnelNextBlock = false;
    public int tunnelWidth = 1;
    public int tunnelHeight = 2;
    public int tunnelDeep = Integer.MAX_VALUE;
    public Direction tunnelFaceMined;

    public boolean forceNext = false;

    public BlockPos lastPosition;
    public String lastMessage = "";
    public int lastBlocksMined = 0;

    private final static Map<UUID, ClientStatus> playerClientStatuses = new HashMap<>();

    public static ClientStatus getClientStatus(UUID playerUUID) {
        if (!playerClientStatuses.containsKey(playerUUID)) {
            playerClientStatuses.put(playerUUID, new ClientStatus());
        }
        return playerClientStatuses.get(playerUUID);
    }
}
