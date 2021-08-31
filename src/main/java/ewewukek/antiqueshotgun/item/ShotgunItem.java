package ewewukek.antiqueshotgun.item;

import java.util.Arrays;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import ewewukek.antiqueshotgun.AmmoType;
import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.ReloadAction;
import ewewukek.antiqueshotgun.enchantment.BruteEnchantment;
import ewewukek.antiqueshotgun.entity.BulletEntity;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class ShotgunItem extends Item {
    public ShotgunItem(Properties properties) {
        super(properties);
    }

    public static final int JAMMED_SOUND_REPEAT_INTERVAL = 10;

    public static boolean enableMagazine;

    public abstract int getMagazineCapacity();
    public abstract int getReloadDuration();
    public abstract int getShellInsertDuration();

    public boolean canBeUsedFromOffhand() {
        return false;
    }

    public float getMisfireChance() {
        return 0;
    }

    public float getSpreadStdDevAdd() {
        return 0;
    }

    public float getDamageMultiplier() {
        return 1;
    }

    public boolean canBeUsedFromOffhand(PlayerEntity player) {
        return canBeUsedFromOffhand()
            && !(player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof ShotgunItem);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hand == Hand.OFF_HAND && !canBeUsedFromOffhand(player)) {
            return ActionResult.pass(stack);
        }

        if (!worldIn.isClientSide) {
            long currentTime = worldIn.getGameTime();
            if (!hasTimerExpired(stack, currentTime)) {
                return ActionResult.fail(stack);
            }

            double posX = player.getX();
            double posY = player.getY();
            double posZ = player.getZ();

            if (isJammed(stack)) {
                worldIn.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_JAMMED, SoundCategory.PLAYERS, 0.8f, 1);

                setTimerExpiryTime(stack, currentTime + JAMMED_SOUND_REPEAT_INTERVAL);
                return ActionResult.fail(stack);
            }

            AmmoType ammoType = getAmmoInChamber(stack);
            if (ammoType != AmmoType.NONE) {
                AmmoItem ammoItem = ammoType.toItem();
                boolean misfire = random.nextFloat() < getMisfireChance() + ammoItem.misfireChance();

                if (!misfire) {
                    final float deg2rad = 0.017453292f;
                    Vector3d direction = new Vector3d(0, 0, 1).xRot(-deg2rad * player.xRot).yRot(-deg2rad * player.yRot);
                    fireBullets(worldIn, player, direction, ammoType);

                    if (ammoType == AmmoType.SLUG) {
                        worldIn.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_FIRE_SLUG, SoundCategory.PLAYERS, 3.5f, 1);
                    } else {
                        worldIn.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_FIRE, SoundCategory.PLAYERS, 3.5f, 1);
                    }

                    damageItem(stack, ammoItem.durabilityDamage(), player);

                } else {
                    worldIn.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_DRY_FIRE, SoundCategory.PLAYERS, 0.8f, 1);
                }

                setAmmoInChamber(stack, AmmoType.NONE);
                setTimerExpiryTime(stack, currentTime + postFireDelay() + ammoItem.postFireDelay());
            }
        } else {
            ReloadAction.breakFullReload();
        }

        return ActionResult.fail(stack);
    }

    public void update(PlayerEntity player, ItemStack stack) {
        World world = player.level;
        if (world.isClientSide) return;

        if (getId(stack) == 0) genId(stack);

        if (isJammed(stack)) {
            return;
        }

        long currentTime = world.getGameTime();
        if (!hasTimerExpired(stack, currentTime)) {
            return;
        }
        removeTimer(stack);

        ItemStack ammoStack = findAmmo(player);
        boolean chamberEmpty = getAmmoInChamber(stack) == AmmoType.NONE;
        boolean magazineEmpty;
        boolean isReloading;

        if (enableMagazine) {
            magazineEmpty = getAmmoInMagazineCount(stack) == 0;
            boolean canReload = getAmmoInMagazineCount(stack) < getMagazineCapacity() && !ammoStack.isEmpty();
            isReloading = canReload && (isInsertingShell(stack) || ReloadAction.isReloading(player));

        } else {
            magazineEmpty = ammoStack.isEmpty();
            isReloading = false;
        }

        double posX = player.getX();
        double posY = player.getY();
        double posZ = player.getZ();

        if (chamberEmpty) {
            if (!isSlideBack(stack)) {
                if (!isReloading && !magazineEmpty) {
                    world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_BACK, SoundCategory.PLAYERS, 0.8f, 1);

                    setSlideBack(stack, true);
                    setTimerExpiryTime(stack, currentTime + midCycleDelay());

                    return;
                }

            } else {
                AmmoType ammoType;
                if (enableMagazine) {
                    ammoType = extractAmmoFromMagazine(stack);

                } else {
                    ammoType = ammoTypeFromStack(ammoStack);
                    if (!player.abilities.instabuild) {
                        ammoStack.shrink(1);
                    }
                }

                boolean jammed = random.nextFloat() < ammoType.toItem().jamChance();

                if (!jammed) {
                    world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_FORWARD, SoundCategory.PLAYERS, 0.8f, 1);

                    setSlideBack(stack, false);
                    setAmmoInChamber(stack, ammoType);
                    setTimerExpiryTime(stack, currentTime + postCycleDelay());

                } else {
                    world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_JAMMED, SoundCategory.PLAYERS, 0.8f, 1);

                    setJammed(stack, true);
                    setTimerExpiryTime(stack, currentTime + JAMMED_SOUND_REPEAT_INTERVAL);
                }

                return;
            }
        }

        if (isReloading) {
            player.setSprinting(false);

            if (!isInsertingShell(stack)) {
                setInsertingShell(stack, true);
                setTimerExpiryTime(stack, currentTime + shellPreInsertDelay());

            } else {
                world.playSound(null, posX, posY, posZ, AntiqueShotgunMod.SOUND_SHOTGUN_INSERTING_SHELL, SoundCategory.PLAYERS, 0.8f, 1);

                addAmmoToMagazine(stack, ammoTypeFromStack(ammoStack));
                if (!player.abilities.instabuild) {
                    ammoStack.shrink(1);
                }
                setInsertingShell(stack, false);
                setTimerExpiryTime(stack, currentTime + shellPostInsertDelay());
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        float extraDamage = BruteEnchantment.extraDamageBase + BruteEnchantment.extraDamagePerLevel * EnchantmentHelper.getItemEnchantmentLevel(AntiqueShotgunMod.BRUTE_ENCHANTMENT, stack);
        if (slot != EquipmentSlotType.MAINHAND || extraDamage == 0) {
            return super.getAttributeModifiers(slot, stack);
        }
        Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Damage modifier", extraDamage, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    @Override
    public boolean mineBlock(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!worldIn.isClientSide && entityLiving instanceof PlayerEntity && state.getDestroySpeed(worldIn, pos) != 0.0f) {
            damageItem(stack, 1, (PlayerEntity) entityLiving);
        }
        return false;
    }

    public static void damageItem(ItemStack stack, int amount, PlayerEntity player) {
        stack.hurtAndBreak(amount, player, (entity) -> {
            entity.broadcastBreakEvent(player.getUsedItemHand());
        });
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, boolean slotChanged) {
        if (to.isEmpty() || to.getItem() != this) return true;
        return slotChanged || getId(from) != getId(to);
    }

    public void fireBullets(World world, LivingEntity shooter, Vector3d direction, AmmoType ammoType) {
        direction = direction.normalize();
        Vector3d pos = new Vector3d(shooter.getX(), shooter.getY() + shooter.getEyeHeight(), shooter.getZ());
        Vector3d playerMotion = shooter.getDeltaMovement();

        AmmoItem ammoItem = ammoType.toItem();
        for (int i = 0; i < ammoItem.pelletCount(); ++i) {
            float angle = (float) Math.PI * 2 * random.nextFloat();
            float gaussian = Math.abs((float) random.nextGaussian());
            if (gaussian > 4) gaussian = 4;

            float spread = (ammoItem.spreadStdDev() + getSpreadStdDevAdd()) * gaussian;

            // a plane perpendicular to direction
            Vector3d n1;
            Vector3d n2;
            if (Math.abs(direction.x) < 1e-5 && Math.abs(direction.z) < 1e-5) {
                n1 = new Vector3d(1, 0, 0);
                n2 = new Vector3d(0, 0, 1);
            } else {
                n1 = new Vector3d(-direction.z, 0, direction.x).normalize();
                n2 = direction.cross(n1);
            }

            Vector3d motion = direction.scale(MathHelper.cos(spread))
                .add(n1.scale(MathHelper.sin(spread) * MathHelper.sin(angle))) // signs are not important
                .add(n2.scale(MathHelper.sin(spread) * MathHelper.cos(angle)));

            motion = motion.scale(ammoItem.speed())
                .add(playerMotion.x, shooter.isOnGround() ? 0 : playerMotion.y, playerMotion.z);

            BulletEntity bullet = new BulletEntity(world);
            bullet.ammoType = ammoType;
            bullet.distanceLeft = ammoItem.range();
            bullet.damageMultiplier = getDamageMultiplier();
            bullet.setOwner(shooter);
            bullet.setPos(pos.x, pos.y, pos.z);
            bullet.setDeltaMovement(motion);

            world.addFreshEntity(bullet);
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

    public boolean isAmmo(ItemStack stack) {
        return ammoTypeFromStack(stack) != AmmoType.NONE;
    }

    public ItemStack findAmmo(PlayerEntity player) {
        for (int i = 0; i != player.inventory.items.size(); ++i) {
            ItemStack itemstack = player.inventory.items.get(i);
            if (isAmmo(itemstack)) return itemstack;
        }
        return ItemStack.EMPTY;
    }

    public static AmmoType ammoTypeFromStack(ItemStack ammoStack) {
        return AmmoType.fromItem(ammoStack.getItem());
    }

    public static boolean hasTimerExpired(ItemStack stack, long currentTime) {
        return currentTime > stack.getOrCreateTag().getLong("timer_expiry_time");
    }

    public static void setTimerExpiryTime(ItemStack stack, long time) {
        stack.getOrCreateTag().putLong("timer_expiry_time", time);
    }

    public static void removeTimer(ItemStack stack) {
        stack.getOrCreateTag().remove("timer_expiry_time");
    }

    public static boolean isSlideBack(ItemStack stack) {
        return stack.getOrCreateTag().getByte("slide_back") != 0;
    }

    public static void setSlideBack(ItemStack stack, boolean value) {
        setBoolTag(stack, "slide_back", value);
    }

    public static boolean isJammed(ItemStack stack) {
        return stack.getOrCreateTag().getByte("jammed") != 0;
    }

    public static void setJammed(ItemStack stack, boolean value) {
        setBoolTag(stack, "jammed", value);
    }

    // synthetic state to add a delay before playing the shell insertion sound
    public static boolean isInsertingShell(ItemStack stack) {
        return stack.getOrCreateTag().getByte("inserting_shell") != 0;
    }

    public static void setInsertingShell(ItemStack stack, boolean value) {
        setBoolTag(stack, "inserting_shell", value);
    }

    public static AmmoType getAmmoInChamber(ItemStack stack) {
        return AmmoType.fromByte(stack.getOrCreateTag().getByte("chamber"));
    }

    public static void setAmmoInChamber(ItemStack stack, AmmoType ammoType) {
        if (ammoType != AmmoType.NONE) {
            stack.getOrCreateTag().putByte("chamber", ammoType.toByte());
        } else {
            stack.getOrCreateTag().remove("chamber");
        }
    }

    public static int getAmmoInMagazineCount(ItemStack stack) {
        return stack.getOrCreateTag().getByteArray("magazine").length;
    }

    public static AmmoType extractAmmoFromMagazine(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        byte[] magazine = tag.getByteArray("magazine");
        if (magazine.length == 0) return AmmoType.NONE;

        AmmoType ammoType = AmmoType.fromByte(magazine[magazine.length - 1]);
        if (magazine.length > 1) {
            tag.putByteArray("magazine", Arrays.copyOf(magazine, magazine.length - 1));
        } else {
            tag.remove("magazine");
        }
        return ammoType;
    }

    public static void addAmmoToMagazine(ItemStack stack, AmmoType ammoType) {
        CompoundNBT tag = stack.getOrCreateTag();
        byte[] magazine = tag.getByteArray("magazine");
        byte[] newMagazine = Arrays.copyOf(magazine, magazine.length + 1);
        newMagazine[newMagazine.length - 1] = ammoType.toByte();
        tag.putByteArray("magazine", newMagazine);
    }

    public static ItemStack unload(ItemStack stack) {
        ItemStack ammoStack = ItemStack.EMPTY;

        AmmoType chamberAmmoType = getAmmoInChamber(stack);
        if (chamberAmmoType != AmmoType.NONE) {
            ammoStack = new ItemStack(chamberAmmoType.toItem());
            setAmmoInChamber(stack, AmmoType.NONE);
        }

        CompoundNBT tag = stack.getOrCreateTag();
        byte[] magazine = tag.getByteArray("magazine");
        int length = magazine.length;
        while (length > 0) {
            Item ammoItem = AmmoType.fromByte(magazine[length - 1]).toItem();
            if (ammoStack.isEmpty()) {
                ammoStack = new ItemStack(ammoItem);
            } else if (ammoItem == ammoStack.getItem()) {
                ammoStack.grow(1);
            } else {
                break;
            }
            --length;
        }
        if (length < magazine.length) {
            if (length > 0) {
                tag.putByteArray("magazine", Arrays.copyOf(magazine, length));
            } else {
                tag.remove("magazine");
            }
        }

        return ammoStack;
    }

    public static void setBoolTag(ItemStack stack, String key, boolean value) {
        if (value) {
            stack.getOrCreateTag().putByte(key, (byte)1);
        } else {
            stack.getOrCreateTag().remove(key);
        }
    }

    // hack to avoid reequip animation on tag change
    public static int getId(ItemStack stack) {
        return stack.getOrCreateTag().getInt("id");
    }

    private static void genId(ItemStack stack) {
        stack.getOrCreateTag().putInt("id", Long.hashCode(System.currentTimeMillis()));
    }
}
