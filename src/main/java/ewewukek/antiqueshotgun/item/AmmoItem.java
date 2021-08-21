package ewewukek.antiqueshotgun.item;

import net.minecraft.item.Item;

public abstract class AmmoItem extends Item {
    public AmmoItem(Properties properties) {
        super(properties);
    }

    public abstract float spreadStdDev();
    public abstract float speed();
    public abstract float range();
    public abstract float damage();
    public abstract int durabilityDamage();

    public int pelletCount() {
        return 1;
    }

    public int postFireDelay() {
        return 0;
    }

    public float misfireChance() {
        return 0;
    }

    public float jamChance() {
        return 0;
    }
}
