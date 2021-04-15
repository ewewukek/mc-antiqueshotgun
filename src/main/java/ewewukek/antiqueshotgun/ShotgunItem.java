package ewewukek.antiqueshotgun;

import java.util.Arrays;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
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
                player.playSound(AntiqueShotgunMod.SOUND_SHOTGUN_FIRE, 1.5f, 1);
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

        PlayerEntity player = (PlayerEntity) entity;
        double posX = player.getPosX();
        double posY = player.getPosY();
        double posZ = player.getPosZ();

        if (!isReady(stack)) {
            int usingDuration = getUseDuration(stack) - timeLeft;
            if (!isSlideBack(stack) && usingDuration > getCycleBackDuration()) {
                world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_BACK, SoundCategory.PLAYERS, 0.5F, 1.0F);
                setSlideBack(stack, true);
            } else if (isSlideBack(stack) && usingDuration > getCycleBackDuration() + getCycleForwardDuration()) {
                world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_FORWARD, SoundCategory.PLAYERS, 0.5F, 1.0F);
                setSlideBack(stack, false);
                setReady(stack, true);
                setAmmoInChamber(stack, extractAmmoFromMagazine(stack));
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

    public int getAmmoInMagazineCount(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getByteArray("magazine").length : 0;
    }

    public byte extractAmmoFromMagazine(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) return AMMO_NONE;
        byte[] magazine = tag.getByteArray("magazine");
        if (magazine.length == 0) return AMMO_NONE;
        byte ammoType = magazine[magazine.length - 1];
        tag.putByteArray("magazine", Arrays.copyOf(magazine, magazine.length - 1));
        return ammoType;
    }

    public void addAmmoToMagazine(ItemStack stack, byte ammoType) {
        CompoundNBT tag = stack.getOrCreateTag();
        byte[] magazine = tag.getByteArray("magazine");
        byte[] newMagazine = Arrays.copyOf(magazine, magazine.length + 1);
        newMagazine[newMagazine.length - 1] = ammoType;
        tag.putByteArray("magazine", newMagazine);
    }
}
