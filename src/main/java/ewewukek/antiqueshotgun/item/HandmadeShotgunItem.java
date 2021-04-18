package ewewukek.antiqueshotgun.item;

import net.minecraft.item.Item;

public class HandmadeShotgunItem extends ShotgunItem {
    public HandmadeShotgunItem(Item.Properties properties) {
        super(properties.defaultMaxDamage(200));
    }

    public static int magazineCapacity = 4;
    public static int reloadDuration = 16;
    public static int shellInsertDuration = 14;

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
    }}
