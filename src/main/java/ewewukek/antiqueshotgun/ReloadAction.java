package ewewukek.antiqueshotgun;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;

public class ReloadAction {
// client part
    public static boolean lastReloadKeyDown;
    public static boolean reloadKeyDown;

    public static void clientUpdate() {
        if (reloadKeyDown != lastReloadKeyDown) {
            AntiqueShotgunMod.NETWORK_CHANNEL.sendToServer(new AntiqueShotgunMod.ReloadStatePacket(reloadKeyDown));
            lastReloadKeyDown = reloadKeyDown;
        }
    }

// server part
    private static final Map<UUID, Boolean> reloadState = new HashMap<>();

    public static boolean isReloading(PlayerEntity player) {
        return reloadState.getOrDefault(player.getUniqueID(), false);
    }

    public static void setIsReloading(PlayerEntity player, boolean isReloading) {
        reloadState.put(player.getUniqueID(), isReloading);
    }
}
