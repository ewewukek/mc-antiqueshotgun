package ewewukek.antiqueshotgun.item;

public class SlugAmmoItem extends AmmoItem {
    public SlugAmmoItem(Properties properties) {
        super(properties);
    }

    public static float spreadStdDev = 0;
    public static float speed = 20;
    public static float range = 25;
    public static float damage = 25;

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
