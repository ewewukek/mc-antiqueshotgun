package ewewukek.antiqueshotgun.item.crafting;

import ewewukek.antiqueshotgun.AmmoType;
import ewewukek.antiqueshotgun.item.ShotgunItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class UnloadShotgunRecipe extends SpecialRecipe {
    public static final SpecialRecipeSerializer<UnloadShotgunRecipe> SERIALIZER = new SpecialRecipeSerializer<>(UnloadShotgunRecipe::new);

    public UnloadShotgunRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack stack = findShotgun(inv);
        return !stack.isEmpty() && (ShotgunItem.getAmmoInChamber(stack) != AmmoType.NONE || ShotgunItem.getAmmoInMagazineCount(stack) > 0);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack stack = findShotgun(inv);
        return ShotgunItem.unload(stack.copy());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < items.size(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem() instanceof ShotgunItem) {
                stack = stack.copy();
                ShotgunItem.unload(stack);
            }
            items.set(i, stack);
        }

        return items;
    }

    private ItemStack findShotgun(CraftingInventory inv) {
        ItemStack result = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ShotgunItem) {
                if (!result.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                result = stack;
            }
        }
        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}
