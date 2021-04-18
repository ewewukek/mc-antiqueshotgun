package ewewukek.antiqueshotgun.item;

public class RubberAmmoItem extends AmmoItem {
    public RubberAmmoItem(Properties properties) {
        super(properties);
    }

    public static float spreadStdDev = (float)Math.toRadians(0.5);
    public static float speed = 10;
    public static float range = 20;
    public static float damage = 1;

    @Override
    public int pelletCount() {
        return 1;
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
