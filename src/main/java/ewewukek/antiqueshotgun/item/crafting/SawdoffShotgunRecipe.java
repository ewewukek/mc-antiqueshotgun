package ewewukek.antiqueshotgun.item.crafting;

import ewewukek.antiqueshotgun.AmmoType;
import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.item.HandmadeShotgunItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SawdoffShotgunRecipe extends StonecuttingRecipe {
    public static final SpecialRecipeSerializer<SawdoffShotgunRecipe> SERIALIZER = new SpecialRecipeSerializer<>(SawdoffShotgunRecipe::new);
    public static final ItemStack RESULT = new ItemStack(AntiqueShotgunMod.SAWD_OFF_SHOTGUN);

    public SawdoffShotgunRecipe(ResourceLocation id) {
        super(id, "", null, null);
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        ItemStack stack = inv.getStackInSlot(0);
        return stack != null && !stack.isEmpty()
            && stack.getItem() == AntiqueShotgunMod.HANDMADE_SHOTGUN
            && HandmadeShotgunItem.getAmmoInChamber(stack) == AmmoType.NONE
            && HandmadeShotgunItem.getAmmoInMagazineCount(stack) == 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return RESULT;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack input = inv.getStackInSlot(0);
        ItemStack result = RESULT.copy();
        double fraction = (float)input.getDamage() / input.getMaxDamage();
        result.setDamage((int)Math.ceil(fraction * result.getMaxDamage()));
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
