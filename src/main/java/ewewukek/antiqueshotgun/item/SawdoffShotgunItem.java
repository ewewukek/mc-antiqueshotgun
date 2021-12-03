package ewewukek.antiqueshotgun.item;

import ewewukek.antiqueshotgun.AmmoType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SawdoffShotgunItem extends HandmadeShotgunItem {
    public SawdoffShotgunItem(Item.Properties properties) {
        super(properties.defaultDurability(125));
    }

    public static int magazineCapacity;
    public static float spreadStdDevAdd;
    public static float damageMultiplier;

    @Override
    public boolean isAmmo(ItemStack stack) {
        AmmoType type = ammoTypeFromStack(stack);
        return type == AmmoType.HANDMADE || type == AmmoType.BUCKSHOT;
    }

    @Override
    public boolean canBeUsedFromOffhand() {
        return true;
    }

    @Override
    public int getMagazineCapacity() {
        return magazineCapacity;
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
        return 0;
    }
}
