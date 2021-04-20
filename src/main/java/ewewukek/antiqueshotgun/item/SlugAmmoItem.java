package ewewukek.antiqueshotgun.item;

public class SlugAmmoItem extends AmmoItem {
    public SlugAmmoItem(Properties properties) {
        super(properties);
    }

    public static float spreadStdDev;
    public static float speed;
    public static float range;
    public static float damage;

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
