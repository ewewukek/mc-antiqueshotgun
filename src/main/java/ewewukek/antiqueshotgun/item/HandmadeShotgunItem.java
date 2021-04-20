package ewewukek.antiqueshotgun.item;

import net.minecraft.item.Item;

public class HandmadeShotgunItem extends ShotgunItem {
    public HandmadeShotgunItem(Item.Properties properties) {
        super(properties.defaultMaxDamage(200));
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
    }}
