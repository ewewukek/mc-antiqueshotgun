package ewewukek.antiqueshotgun.item.crafting;

import ewewukek.antiqueshotgun.AmmoType;
import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.item.HandmadeShotgunItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SawdoffShotgunRecipe extends StonecuttingRecipe {
    public static final SpecialRecipeSerializer<SawdoffShotgunRecipe> SERIALIZER = new SpecialRecipeSerializer<>(SawdoffShotgunRecipe::new);

    public SawdoffShotgunRecipe(ResourceLocation id) {
        super(id, "",
            Ingredient.of(new ItemStack(AntiqueShotgunMod.HANDMADE_SHOTGUN)),
            new ItemStack(AntiqueShotgunMod.SAWD_OFF_SHOTGUN));
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        ItemStack stack = inv.getItem(0);
        return stack != null && !stack.isEmpty()
            && stack.getItem() == AntiqueShotgunMod.HANDMADE_SHOTGUN
            && HandmadeShotgunItem.getAmmoInChamber(stack) == AmmoType.NONE
            && HandmadeShotgunItem.getAmmoInMagazineCount(stack) == 0;
    }

    @Override
    public ItemStack assemble(IInventory inv) {
        ItemStack input = inv.getItem(0);
        ItemStack result = this.result.copy();
        double fraction = (float)input.getDamageValue() / input.getMaxDamage();
        result.setDamageValue((int)Math.ceil(fraction * result.getMaxDamage()));
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
