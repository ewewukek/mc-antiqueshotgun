package ewewukek.antiqueshotgun;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class DamageQueue {

    public static void add(Entity target, DamageSource damageSource, float damage) {
        Entry entry = queue.get(target);
        if (entry == null) {
            entry = new Entry();
            queue.put(target, entry);
        }
        entry.damageSource = damageSource;
        entry.damage += damage;
    }

    public static void apply() {
        queue.forEach((target, entry) -> {
            target.hurt(entry.damageSource, entry.damage);
        });
        queue.clear();
    }

    private static Map<Entity, Entry> queue = new HashMap<>();

    private static class Entry {
        DamageSource damageSource;
        float damage;
    }
}
