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

    public int getCycleBackDelay() {
        return 8;
    }

    public int getCycleForwardDelay() {
        return 6;
    }

    public int getShellPreInsertDelay() {
        return 6;
    }

    public int getShellPostInsertDelay() {
        return 6;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        if (hand != Hand.MAIN_HAND && !canBeUsedFromOffhand()) {
            return super.onItemRightClick(worldIn, player, hand);
        }

        ItemStack stack = player.getHeldItem(hand);
        byte ammoType = getAmmoInChamber(stack);
        if (ammoType != AMMO_NONE) {
            player.playSound(AntiqueShotgunMod.SOUND_SHOTGUN_FIRE, 1.5f, 1);

            setAmmoInChamber(stack, AMMO_NONE);
            resetLastActionTime(stack, worldIn);

            return ActionResult.resultConsume(stack);
        }

        ItemStack ammoStack = findAmmo(player);
        if (getAmmoInMagazineCount(stack) > 0 || !ammoStack.isEmpty()) {
            player.setActiveHand(hand);
            return ActionResult.resultConsume(stack);
        }

        return ActionResult.resultFail(stack);
    }

    public void update(PlayerEntity player, ItemStack stack) {
        World world = player.world;

        double posX = player.getPosX();
        double posY = player.getPosY();
        double posZ = player.getPosZ();

        long ticksFromLastAction = getTicksFromLastAction(stack, world);
        boolean reload = false;

        if (getAmmoInChamber(stack) == AMMO_NONE) {
            if (!isSlideBack(stack)) {
                if (ticksFromLastAction >= getCycleBackDelay()) {
                    world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_BACK, SoundCategory.PLAYERS, 0.5F, 1.0F);

                    setSlideBack(stack, true);
                    resetLastActionTime(stack, world);
                }
            } else if (isSlideBack(stack)) {
                if (getAmmoInMagazineCount(stack) > 0) {
                    if (ticksFromLastAction >= getCycleForwardDelay()) {
                        world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_FORWARD, SoundCategory.PLAYERS, 0.5F, 1.0F);

                        setSlideBack(stack, false);
                        setAmmoInChamber(stack, extractAmmoFromMagazine(stack));
                        resetLastActionTime(stack, world);
                    }
                } else {
                    reload = true;
                }
            }
        } else if (getAmmoInMagazineCount(stack) < getMagazineCapacity()) {
            reload = true;
        }

        if (reload) {
            ItemStack ammoStack = findAmmo(player);
            if (!ammoStack.isEmpty()) {
                if (!isReloading(stack)) {
                    if (ticksFromLastAction >= getShellPreInsertDelay()) {
                        world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_INSERTING_SHELL, SoundCategory.PLAYERS, 0.5F, 1.0F);

                        setReloading(stack, true);
                        resetLastActionTime(stack, world);
                    }
                } else {
                    if (ticksFromLastAction >= getShellPostInsertDelay()) {
                        addAmmoToMagazine(stack, consumeAmmoStack(ammoStack));
                        setReloading(stack, false);
                        resetLastActionTime(stack, world);
                    }
                }
            }
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

    public long getTicksFromLastAction(ItemStack stack, World world) {
        CompoundNBT tag = stack.getTag();
        long lastActionTime = tag != null ? tag.getLong("last_action_time") : 0;
        return world.getGameTime() - lastActionTime;
    }

    public void resetLastActionTime(ItemStack stack, World world) {
        stack.getOrCreateTag().putLong("last_action_time", world.getGameTime());
    }

    public boolean isSlideBack(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("slide_back") != 0;
    }

    public void setSlideBack(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("slide_back", (byte) (value ? 1 : 0));
    }

    // synthetic state to add a delay between inserting and moving pump forward
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
