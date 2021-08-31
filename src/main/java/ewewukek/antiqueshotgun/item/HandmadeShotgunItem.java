package ewewukek.antiqueshotgun.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HandmadeShotgunItem extends ShotgunItem {
    public HandmadeShotgunItem(Item.Properties properties) {
        super(properties.defaultDurability(200));
    }

    public static int magazineCapacity;
    public static int reloadDuration;
    public static int shellInsertDuration;
    public static float misfireChance;
    public static float spreadStdDevAdd;
    public static float damageMultiplier;

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
    public float getMisfireChance() {
        return misfireChance;
    }

    @Override
    public float getSpreadStdDevAdd() {
        return spreadStdDevAdd;
    }

    @Override
    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack other) {
        return !other.isEmpty() && other.getItem() == Items.IRON_INGOT;
    }
}
