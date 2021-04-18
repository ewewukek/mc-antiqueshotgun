package ewewukek.antiqueshotgun.item;

public class BuckshotAmmoItem extends AmmoItem {
    public BuckshotAmmoItem(Properties properties) {
        super(properties);
    }

    public static int pelletCount = 9;
    public static float spreadStdDev = 1.5f;
    public static float speed = 20;
    public static float range = 15;
    public static float damage = 25;

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
}
