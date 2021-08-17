package ewewukek.antiqueshotgun.item;

public class BuckshotAmmoItem extends AmmoItem {
    public BuckshotAmmoItem(Properties properties) {
        super(properties);
    }

    public static int pelletCount;
    public static float spreadStdDev;
    public static float speed;
    public static float range;
    public static float damage;

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
    public int postFireDelay() {
        return 0;
    }
}
