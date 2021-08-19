package ewewukek.antiqueshotgun.enchantment;

import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

public class BruteEnchantment extends Enchantment {
    public BruteEnchantment(Rarity rarityIn, EquipmentSlotType... slots) {
        super(rarityIn, AntiqueShotgunMod.ENCHANTMENT_TYPE_SHOTGUN, slots);
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 1 + 20 * (enchantmentLevel - 1);
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) + 19;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
