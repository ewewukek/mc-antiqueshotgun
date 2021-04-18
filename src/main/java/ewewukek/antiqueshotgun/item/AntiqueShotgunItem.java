package ewewukek.antiqueshotgun.item;

import net.minecraft.item.Item;

public class AntiqueShotgunItem extends ShotgunItem {
    public AntiqueShotgunItem(Item.Properties properties) {
        super(properties.defaultMaxDamage(2000));
    }

    public static int magazineCapacity = 7;
    public static int reloadDuration = 12;
    public static int shellInsertDuration = 10;

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
