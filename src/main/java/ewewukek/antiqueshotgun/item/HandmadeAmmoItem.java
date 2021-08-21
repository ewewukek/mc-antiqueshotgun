package ewewukek.antiqueshotgun.item;

public class HandmadeAmmoItem extends AmmoItem {
    public HandmadeAmmoItem(Properties properties) {
        super(properties);
    }

    public static int pelletCount;
    public static float spreadStdDev;
    public static float speed;
    public static float range;
    public static float damage;
    public static int durabilityDamage;
    public static float misfireChance;
    public static float jamChance;

    @Override
    public int pelletCount() {
        return pelletCount;
    }

    @Override
    public float spreadStdDev() {
        return spreadStdDev;
    }

    @Override
    public float speed() {
        return speed;
    }

    @Override
    public float range() {
        return range;
    }

    @Override
    public float damage() {
        return damage;
    }

    @Override
    public int durabilityDamage() {
        return durabilityDamage;
    }

    @Override
    public float misfireChance() {
        return misfireChance;
    }

    @Override
    public float jamChance() {
        return jamChance;
    }
}
