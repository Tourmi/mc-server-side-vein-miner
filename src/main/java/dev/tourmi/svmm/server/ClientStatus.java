package dev.tourmi.svmm.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientStatus {
    public boolean tunnelNextBlock;
    public int tunnelWidth;
    public int tunnelHeight;
    public int tunnelDeep;
    public Direction tunnelFaceMined;

    public boolean forceNext;

    public BlockPos lastPosition;
    public String lastMessage;
    public int lastBlocksMined;

    private final static Map<UUID, ClientStatus> playerClientStatuses = new HashMap<>();

    private ClientStatus() {
        reset();
    }

    public static ClientStatus getClientStatus(UUID playerUUID) {
        if (!playerClientStatuses.containsKey(playerUUID)) {
            playerClientStatuses.put(playerUUID, new ClientStatus());
        }

        return playerClientStatuses.get(playerUUID);
    }

    public void reset() {
        tunnelNextBlock = false;
        tunnelWidth = 1;
        tunnelHeight = 2;
        tunnelDeep = Integer.MAX_VALUE;
        tunnelFaceMined = null;

        forceNext = false;
        lastPosition = null;
        lastMessage = "";
        lastBlocksMined = 0;
    }
}
