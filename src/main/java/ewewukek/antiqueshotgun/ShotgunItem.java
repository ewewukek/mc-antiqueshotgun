package ewewukek.antiqueshotgun;

import java.util.Arrays;

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

    public int getMagazineCapacity() {
        return 4;
    }

    public int getReloadDuration() {
        return 14;
    }

    public int getShellInsertDuration() {
        return 12;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND && !canBeUsedFromOffhand()) {
            return super.onItemRightClick(worldIn, player, hand);
        }

        ItemStack stack = player.getHeldItem(hand);
        if (isReloading(stack)) {
            setReloading(stack, false);
        }

        long currentTime = worldIn.getGameTime();
        byte ammoType = getAmmoInChamber(stack);
        if (hasTimerExpired(stack, currentTime) && ammoType != AMMO_NONE) {
            player.playSound(AntiqueShotgunMod.SOUND_SHOTGUN_FIRE, 1.5f, 1);

            setAmmoInChamber(stack, AMMO_NONE);
            setTimerExpiryTime(stack, currentTime + (long)(getReloadDuration() * 0.5));
        }

        return ActionResult.resultConsume(stack);
    }

    public void update(PlayerEntity player, ItemStack stack) {
        World world = player.world;

        long currentTime = world.getGameTime();
        if (!hasTimerExpired(stack, currentTime)) {
            return;
        }

        double posX = player.getPosX();
        double posY = player.getPosY();
        double posZ = player.getPosZ();

        boolean insertShell = false;

        if (getAmmoInChamber(stack) == AMMO_NONE) {
            if (!isSlideBack(stack)) {
                world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_BACK, SoundCategory.PLAYERS, 0.5F, 1.0F);

                setSlideBack(stack, true);
                setTimerExpiryTime(stack, currentTime + (long)(getReloadDuration() * 0.5));

                return;

            } else if (isSlideBack(stack)) {
                if (getAmmoInMagazineCount(stack) > 0) {
                    world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_FORWARD, SoundCategory.PLAYERS, 0.5F, 1.0F);

                    setSlideBack(stack, false);
                    setAmmoInChamber(stack, extractAmmoFromMagazine(stack));

                    return;

                } else {
                    insertShell = true;
                }
            }
        }

        ItemStack ammoStack = findAmmo(player);

        if (isReloading(stack)) {
            if (getAmmoInMagazineCount(stack) < getMagazineCapacity() && !ammoStack.isEmpty()) {
                insertShell = true;

            } else {
                setReloading(stack, false);
            }
        }

        if (insertShell && !ammoStack.isEmpty()) {
            if (!isInsertingShell(stack)) {
                setInsertingShell(stack, true);
                setTimerExpiryTime(stack, currentTime + (long)(getShellInsertDuration() * 0.35));

            } else {
                world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_INSERTING_SHELL, SoundCategory.PLAYERS, 0.5F, 1.0F);

                addAmmoToMagazine(stack, consumeAmmoStack(ammoStack));
                setInsertingShell(stack, false);
                setTimerExpiryTime(stack, currentTime + (long)(getShellInsertDuration() * 0.65));
            }
        }
    }

    public void reloadKeyUpdated(PlayerEntity player, ItemStack stack, boolean isDown) {
        if (isDown && !findAmmo(player).isEmpty()) {
            setReloading(stack, true);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    private static byte consumeAmmoStack(ItemStack ammoStack) {
        Item ammoItem = ammoStack.getItem();
        ammoStack.shrink(1);
        if (ammoItem == AntiqueShotgunMod.HANDMADE_SHELL) {
            return AMMO_HANDMADE;
        } else if (ammoItem == AntiqueShotgunMod.BUCKSHOT_SHELL) {
            return AMMO_BUCKSHOT;
        } else if (ammoItem == AntiqueShotgunMod.SLUG_SHELL) {
            return AMMO_SLUG;
        } else if (ammoItem == AntiqueShotgunMod.RUBBER_SHELL) {
            return AMMO_RUBBER;
        }
        return AMMO_NONE;
    }

    private boolean isAmmo(ItemStack stack) {
        Item item = stack.getItem();
        return item == AntiqueShotgunMod.HANDMADE_SHELL
            || item == AntiqueShotgunMod.BUCKSHOT_SHELL
            || item == AntiqueShotgunMod.SLUG_SHELL
            || item == AntiqueShotgunMod.RUBBER_SHELL;
    }

    private ItemStack findAmmo(PlayerEntity player) {
        for (int i = 0; i != player.inventory.mainInventory.size(); ++i) {
            ItemStack itemstack = player.inventory.mainInventory.get(i);
            if (isAmmo(itemstack)) return itemstack;
        }
        return ItemStack.EMPTY;
    }

    public boolean hasTimerExpired(ItemStack stack, long currentTime) {
        CompoundNBT tag = stack.getTag();
        long storedTime = tag != null ? tag.getLong("timer_expiry_time") : 0;
        return currentTime > storedTime;
    }

    public void setTimerExpiryTime(ItemStack stack, long time) {
        stack.getOrCreateTag().putLong("timer_expiry_time", time);
    }

    public boolean isSlideBack(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("slide_back") != 0;
    }

    public void setSlideBack(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("slide_back", (byte) (value ? 1 : 0));
    }

    // synthetic state to add a delay before playing the shell insertion sound
    public boolean isInsertingShell(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("inserting_shell") != 0;
    }

    public void setInsertingShell(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("inserting_shell", (byte) (value ? 1 : 0));
    }

    public boolean isReloading(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("reloading") != 0;
    }

    public void setReloading(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("reloading", (byte) (value ? 1 : 0));
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
