package ewewukek.antiqueshotgun;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;

public class KeyState {
    private static final Map<UUID, Boolean> reloadKeyState = new HashMap<>();

    public static void setReloadKeyDown(PlayerEntity player, boolean isDown) {
        reloadKeyState.put(player.getUniqueID(), isDown);
    }

    public static boolean isReloadKeyDown(PlayerEntity player) {
        return reloadKeyState.getOrDefault(player.getUniqueID(), false);
    }
}
