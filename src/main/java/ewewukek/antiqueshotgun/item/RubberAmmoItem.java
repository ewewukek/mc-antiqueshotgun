package ewewukek.antiqueshotgun.item;

public class RubberAmmoItem extends AmmoItem {
    public RubberAmmoItem(Properties properties) {
        super(properties);
    }

    public static float spreadStdDev;
    public static float speed;
    public static float range;
    public static float damage;

    public static float knockbackForce;

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
