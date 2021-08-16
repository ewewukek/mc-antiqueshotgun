package ewewukek.antiqueshotgun.item;

import ewewukek.antiqueshotgun.AmmoType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SawdoffShotgunItem extends HandmadeShotgunItem {
    public SawdoffShotgunItem(Item.Properties properties) {
        super(properties.defaultMaxDamage(100));
    }

    public static int magazineCapacity;
    public static float spreadStdDevAdd;
    public static float damageMultiplier;

    @Override
    public boolean isAmmo(ItemStack stack) {
        AmmoType type = AmmoType.fromItem(stack.getItem());
        return type != AmmoType.NONE && type != AmmoType.SLUG && type != AmmoType.RUBBER;
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
}
