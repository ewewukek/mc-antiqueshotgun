package ewewukek.antiqueshotgun.item;

public class HandmadeAmmoItem extends AmmoItem {
    public HandmadeAmmoItem(Properties properties) {
        super(properties);
    }

    public static int pelletCount = 9;
    public static float spreadStdDev = (float)Math.toRadians(2);
    public static float speed = 20;
    public static float range = 15;
    public static float damage = 20;

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
