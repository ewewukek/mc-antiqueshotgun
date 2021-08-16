package ewewukek.antiqueshotgun.item;

import net.minecraft.item.Item;

public class SawdoffShotgunItem extends HandmadeShotgunItem {
    public SawdoffShotgunItem(Item.Properties properties) {
        super(properties.defaultMaxDamage(100));
    }

    public static int magazineCapacity;
    public static float spreadStdDevAdd;

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
}
