package ewewukek.antiqueshotgun.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AntiqueShotgunItem extends ShotgunItem {
    public AntiqueShotgunItem(Item.Properties properties) {
        super(properties.defaultDurability(2031));
    }

    public static boolean enableCrafting;

    public static int magazineCapacity;
    public static int reloadDuration;
    public static int shellInsertDuration;

    @Override
    public int getMagazineCapacity() {
        return magazineCapacity;
    }

    @Override
    public int getReloadDuration() {
        return reloadDuration;
    }

    @Override
    public int getShellInsertDuration() {
        return shellInsertDuration;
    }

    @Override
    public boolean preventBreaking() {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 50;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack other) {
        return !other.isEmpty() && other.getItem() == Items.NETHERITE_INGOT;
    }
}
