package ewewukek.antiqueshotgun.item;

import java.util.Arrays;

import ewewukek.antiqueshotgun.AmmoType;
import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.KeyState;
import ewewukek.antiqueshotgun.entity.BulletEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class ShotgunItem extends Item {
    public ShotgunItem(Properties properties) {
        super(properties);
    }

    public abstract boolean canBeUsedFromOffhand();
    public abstract int getMagazineCapacity();
    public abstract int getReloadDuration();
    public abstract int getShellInsertDuration();

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (hand == Hand.OFF_HAND) {
            ItemStack mainHandStack = player.getHeldItem(Hand.MAIN_HAND);
            if (!canBeUsedFromOffhand() || mainHandStack.getItem() instanceof ShotgunItem) {
                return ActionResult.resultPass(stack);
            }
        }

        if (!worldIn.isRemote) {
            if (isReloading(stack)) {
                setReloading(stack, false);
            }

            long currentTime = worldIn.getGameTime();
            AmmoType ammoType = getAmmoInChamber(stack);
            if (hasTimerExpired(stack, currentTime) && ammoType != AmmoType.NONE) {
                final float deg2rad = 0.017453292f;
                Vector3d direction = new Vector3d(0, 0, 1).rotatePitch(-deg2rad * player.rotationPitch).rotateYaw(-deg2rad * player.rotationYaw);
                fireBullets(worldIn, player, direction, ammoType);
                worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), AntiqueShotgunMod.SOUND_SHOTGUN_FIRE, SoundCategory.PLAYERS, 1.5F, 1);

                setAmmoInChamber(stack, AmmoType.NONE);
                setTimerExpiryTime(stack, currentTime + postFireDelay());
            }
        }

        return ActionResult.resultFail(stack);
    }

    public void update(PlayerEntity player, ItemStack stack) {
        World world = player.world;
        if (world.isRemote) return;

        long currentTime = world.getGameTime();
        if (!hasTimerExpired(stack, currentTime)) {
            return;
        }

        if (KeyState.isReloadKeyDown(player)) {
            if (!isReloading(stack) && !findAmmo(player).isEmpty()) {
                setReloading(stack, true);
            }
        } else {
            if (isReloading(stack) && !isInsertingShell(stack)) {
                setReloading(stack, false);
            }
        }

        double posX = player.getPosX();
        double posY = player.getPosY();
        double posZ = player.getPosZ();

        boolean insertShell = false;

        if (getAmmoInChamber(stack) == AmmoType.NONE) {
            if (!isSlideBack(stack)) {
                world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_BACK, SoundCategory.PLAYERS, 0.5F, 1.0F);

                setSlideBack(stack, true);
                setTimerExpiryTime(stack, currentTime + midCycleDelay());

                return;

            } else if (isSlideBack(stack)) {
                if (getAmmoInMagazineCount(stack) > 0) {
                    world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_FORWARD, SoundCategory.PLAYERS, 0.5F, 1.0F);

                    setSlideBack(stack, false);
                    setAmmoInChamber(stack, extractAmmoFromMagazine(stack));
                    setTimerExpiryTime(stack, currentTime + postCycleDelay());

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
                setTimerExpiryTime(stack, currentTime + shellPreInsertDelay());

            } else {
                world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_INSERTING_SHELL, SoundCategory.PLAYERS, 0.5F, 1.0F);

                addAmmoToMagazine(stack, consumeAmmoStack(ammoStack));
                setInsertingShell(stack, false);
                setTimerExpiryTime(stack, currentTime + shellPostInsertDelay());
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public void fireBullets(World world, LivingEntity shooter, Vector3d direction, AmmoType ammoType) {
        direction = direction.normalize();
        Vector3d pos = new Vector3d(shooter.getPosX(), shooter.getPosY() + shooter.getEyeHeight(), shooter.getPosZ());
        Vector3d playerMotion = shooter.getMotion();

        AmmoItem ammoItem = ammoType.toItem();
        for (int i = 0; i < ammoItem.pelletCount(); ++i) {
            float angle = (float) Math.PI * 2 * random.nextFloat();
            float gaussian = Math.abs((float) random.nextGaussian());
            if (gaussian > 4) gaussian = 4;

            Vector3d motion = direction.rotatePitch(ammoItem.spreadStdDev() * gaussian * MathHelper.sin(angle))
                .rotateYaw(ammoItem.spreadStdDev() * gaussian * MathHelper.cos(angle))
                .scale(ammoItem.speed());

            motion.add(playerMotion.x, shooter.isOnGround() ? 0 : playerMotion.y, playerMotion.z);

            BulletEntity bullet = new BulletEntity(world);
            bullet.ammoType = ammoType;
            bullet.distanceLeft = ammoItem.range();
            bullet.setShooter(shooter);
            bullet.setPosition(pos.x, pos.y, pos.z);
            bullet.setMotion(motion);

            world.addEntity(bullet);
        }
    }

    // helper methods to ensure that sum of each stage delay equals reloading and shell adding durations
    private int postFireDelay() {
        return (getReloadDuration() - postCycleDelay()) / 2;
    }

    private int midCycleDelay() {
        return getReloadDuration() - postFireDelay() - postCycleDelay();
    }

    private int postCycleDelay() {
        return 2;
    }

    private int shellPreInsertDelay() {
        return (int)(getShellInsertDuration() * 0.35);
    }

    private int shellPostInsertDelay() {
        return getShellInsertDuration() - shellPreInsertDelay();
    }

    private static AmmoType consumeAmmoStack(ItemStack ammoStack) {
        AmmoType ammoType = AmmoType.fromItem(ammoStack.getItem());
        ammoStack.shrink(1);
        return ammoType;
    }

    private static boolean isAmmo(ItemStack stack) {
        return AmmoType.fromItem(stack.getItem()) != AmmoType.NONE;
    }

    private static ItemStack findAmmo(PlayerEntity player) {
        for (int i = 0; i != player.inventory.mainInventory.size(); ++i) {
            ItemStack itemstack = player.inventory.mainInventory.get(i);
            if (isAmmo(itemstack)) return itemstack;
        }
        return ItemStack.EMPTY;
    }

    public static boolean hasTimerExpired(ItemStack stack, long currentTime) {
        CompoundNBT tag = stack.getTag();
        long storedTime = tag != null ? tag.getLong("timer_expiry_time") : 0;
        return currentTime > storedTime;
    }

    public static void setTimerExpiryTime(ItemStack stack, long time) {
        stack.getOrCreateTag().putLong("timer_expiry_time", time);
    }

    public static boolean isSlideBack(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("slide_back") != 0;
    }

    public static void setSlideBack(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("slide_back", (byte) (value ? 1 : 0));
    }

    // synthetic state to add a delay before playing the shell insertion sound
    public static boolean isInsertingShell(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("inserting_shell") != 0;
    }

    public static void setInsertingShell(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("inserting_shell", (byte) (value ? 1 : 0));
    }

    public static boolean isReloading(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getByte("reloading") != 0;
    }

    public static void setReloading(ItemStack stack, boolean value) {
        stack.getOrCreateTag().putByte("reloading", (byte) (value ? 1 : 0));
    }

    public static AmmoType getAmmoInChamber(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return AmmoType.fromByte(tag != null ? tag.getByte("chamber") : 0);
    }

    public static void setAmmoInChamber(ItemStack stack, AmmoType ammoType) {
        stack.getOrCreateTag().putByte("chamber", ammoType.toByte());
    }

    public static int getAmmoInMagazineCount(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        return tag != null ? tag.getByteArray("magazine").length : 0;
    }

    public static AmmoType extractAmmoFromMagazine(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) return AmmoType.NONE;
        byte[] magazine = tag.getByteArray("magazine");
        if (magazine.length == 0) return AmmoType.NONE;
        AmmoType ammoType = AmmoType.fromByte(magazine[magazine.length - 1]);
        tag.putByteArray("magazine", Arrays.copyOf(magazine, magazine.length - 1));
        return ammoType;
    }

    public static void addAmmoToMagazine(ItemStack stack, AmmoType ammoType) {
        CompoundNBT tag = stack.getOrCreateTag();
        byte[] magazine = tag.getByteArray("magazine");
        byte[] newMagazine = Arrays.copyOf(magazine, magazine.length + 1);
        newMagazine[newMagazine.length - 1] = ammoType.toByte();
        tag.putByteArray("magazine", newMagazine);
    }
}
