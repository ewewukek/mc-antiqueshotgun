package ewewukek.antiqueshotgun;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ewewukek.antiqueshotgun.item.ShotgunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ReloadAction {
// client part
    public static ItemStack activeStack;

    private static ItemStack activeStackPrev;
    private static boolean isReloadingPrev;
    private static boolean isReloading;

    public static void clientTick(boolean reloadKeyDown) {
        if (activeStack != null) {
            Minecraft mc = Minecraft.getInstance();
            ShotgunItem shotgun = (ShotgunItem)activeStack.getItem();

            int magazineCount = ShotgunItem.getAmmoInMagazineCount(activeStack);
            boolean canReload = magazineCount < shotgun.getMagazineCapacity() && !shotgun.findAmmo(mc.player).isEmpty();

            if (canReload) {
                if (reloadKeyDown) {
                    isReloading = true;
                }
            } else {
                isReloading = false;
            }
        }

        if (isReloading != isReloadingPrev) {
            AntiqueShotgunMod.NETWORK_CHANNEL.sendToServer(new AntiqueShotgunMod.ReloadStatePacket(isReloading));
            isReloadingPrev = isReloading;
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
