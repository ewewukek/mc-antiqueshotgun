package ewewukek.antiqueshotgun.enchantment;

import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

public class BruteEnchantment extends Enchantment {
    public static float extraDamage;
    public static float knockbackForce;

    public BruteEnchantment(Rarity rarityIn, EquipmentSlotType... slots) {
        super(rarityIn, AntiqueShotgunMod.ENCHANTMENT_TYPE_SHOTGUN, slots);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        if (enchantmentLevel == 1) {
            return 1;
        } else {
            return 20 + 10 * enchantmentLevel;
        }
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 30 + 10 * enchantmentLevel;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
