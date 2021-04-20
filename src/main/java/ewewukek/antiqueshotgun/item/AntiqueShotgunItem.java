package ewewukek.antiqueshotgun.item;

import net.minecraft.item.Item;

public class AntiqueShotgunItem extends ShotgunItem {
    public AntiqueShotgunItem(Item.Properties properties) {
        super(properties.defaultMaxDamage(2000));
    }

    public static int magazineCapacity;
    public static int reloadDuration;
    public static int shellInsertDuration;

    @Override
    public boolean canBeUsedFromOffhand() {
        return false;
    }

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
}
