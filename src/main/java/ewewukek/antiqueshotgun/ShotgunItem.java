package ewewukek.antiqueshotgun;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ShotgunItem extends Item {
    public static byte AMMO_NONE = 0;
    public static byte AMMO_HANDMADE = 1;
    public static byte AMMO_BUCKSHOT = 2;
    public static byte AMMO_SLUG = 3;
    public static byte AMMO_RUBBER = 4;

    public ShotgunItem(Properties properties) {
        super(properties);
    }

    public boolean canBeUsedFromOffhand() {
        return false;
    }

    public int getCycleBackDuration() {
        return 6;
    }

    public int getCycleForwardDuration() {
        return 6;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND && !canBeUsedFromOffhand()) {
            return super.onItemRightClick(worldIn, player, hand);
        }

        ItemStack stack = player.getHeldItem(hand);

        if (isReady(stack)) {
            byte ammoType = getAmmoInChamber(stack);
            if (ammoType != AMMO_NONE) {
                System.out.println("PEW!");
            } else {
                System.out.println("click");
            }

            setReady(stack, false);
            setAmmoInChamber(stack, AMMO_NONE);

            return ActionResult.resultConsume(stack);

        } else {
            player.setActiveHand(hand);
            return ActionResult.resultConsume(stack);
        }
    }

    @Override
    public void onUse(World world, LivingEntity entity, ItemStack stack, int timeLeft) {
        if (world.isRemote || !(entity instanceof PlayerEntity)) return;

        if (!isReady(stack)) {
            int usingDuration = getUseDuration(stack) - timeLeft;
            if (!isSlideBack(stack) && usingDuration > getCycleBackDuration()) {
                setSlideBack(stack, true);
            } else if (isSlideBack(stack) && usingDuration > getCycleBackDuration() + getCycleForwardDuration()) {
                setSlideBack(stack, false);
                setReady(stack, true);
                setAmmoInChamber(stack, AMMO_BUCKSHOT);
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public boolean isReady(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("ready") != 0;
    }

    public void setReady(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("ready", (byte) (value ? 1 : 0));
    }

    public boolean isSlideBack(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("slide_back") != 0;
    }

    public void setSlideBack(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("slide_back", (byte) (value ? 1 : 0));
    }

    public byte getAmmoInChamber(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getByte("chamber") : AMMO_NONE;
    }

    public void setAmmoInChamber(ItemStack stack, byte ammoType) {
        stack.getOrCreateTag().putByte("chamber", ammoType);

    }
}
