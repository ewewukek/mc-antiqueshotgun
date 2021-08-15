package ewewukek.antiqueshotgun.entity;

import ewewukek.antiqueshotgun.AmmoType;
import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.entity.ai.ShotgunAttackGoal;
import ewewukek.antiqueshotgun.item.ShotgunItem;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ElderHunterEntity extends AbstractIllagerEntity {
    public static int aimDuration;
    public static int reloadDuration;
    public static int shellInsertDuration;
    public static int magazineCapacity;
    public static float shotgunDropChance;
    public static float raidSpawnChance;
    public static float patrolSpawnChance;

    public boolean isReloading;

    public ElderHunterEntity(EntityType<? extends ElderHunterEntity> type, World worldIn) {
        super(type, worldIn);
        ItemStack stack = new ItemStack(AntiqueShotgunMod.ANTIQUE_SHOTGUN);
        for (int i = 0; i < magazineCapacity; ++i) {
            ShotgunItem.addAmmoToMagazine(stack, AmmoType.BUCKSHOT);
        }
        setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
        setDropChance(EquipmentSlotType.MAINHAND, shotgunDropChance);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();

        goalSelector.addGoal(0, new SwimGoal(this));

        final float findRange = 16;
        final float attackRange = 12;

        goalSelector.addGoal(1, new AbstractRaiderEntity.FindTargetGoal(this, findRange));
        goalSelector.addGoal(2, new ShotgunAttackGoal(this, 1, attackRange));
        goalSelector.addGoal(3, new AbstractIllagerEntity.RaidOpenDoorGoal(this));

        goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
        goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, findRange, 1));
        goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, findRange));

        targetSelector.addGoal(1, (new HurtByTargetGoal(this, AbstractRaiderEntity.class)).setCallsForHelp());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillagerEntity.class, true));
    }

    public static AttributeModifierMap createEntityAttributes() {
        return MonsterEntity.func_234295_eP_()
            .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35)
            .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0)
            .createMutableAttribute(Attributes.ATTACK_DAMAGE, 10.0)
            .createMutableAttribute(Attributes.MAX_HEALTH, 24.0)
            .create();
    }

    @Override
    public void applyWaveBonus(int wave, boolean p_213660_2_) {
    }

    @Override
    public boolean isOnSameTeam(Entity entityIn) {
        if (super.isOnSameTeam(entityIn)) {
            return true;
        }
        if (entityIn instanceof LivingEntity && ((LivingEntity)entityIn).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
            return getTeam() == null && entityIn.getTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    @Override
    public SoundEvent getRaidLossSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        compound.put("weapon", stack.write(new CompoundNBT()));
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        ItemStack stack = ItemStack.read(compound.getCompound("weapon"));
        if (!stack.isEmpty()) setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
    }

    public boolean isWeaponReady() {
        ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);

        return ShotgunItem.getAmmoInChamber(stack) != AmmoType.NONE
            && ShotgunItem.hasTimerExpired(stack, world.getGameTime());
    }

    public void fireWeapon(LivingEntity target) {
        ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);

        Vector3d direction = new Vector3d(
            target.getPosX() - getPosX(),
            target.getBoundingBox().minY + target.getHeight() * 0.7f - getPosY() - getEyeHeight(),
            target.getPosZ() - getPosZ()
        );
        AmmoType ammoType = ShotgunItem.getAmmoInChamber(stack);

        ((ShotgunItem)stack.getItem()).fireBullets(world, this, direction, ammoType);
        world.playSound(null, getPosX(), getPosY(), getPosZ(), AntiqueShotgunMod.SOUND_SHOTGUN_FIRE, SoundCategory.HOSTILE, 1.5F, 1);

        ShotgunItem.setAmmoInChamber(stack, AmmoType.NONE);
        ShotgunItem.setTimerExpiryTime(stack, world.getGameTime() + postFireDelay());
    }

    @Override
    public void tick() {
        update();
        super.tick();
    }

    private void update() {
        ItemStack stack = getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        if (ShotgunItem.getAmmoInChamber(stack) != AmmoType.NONE) return;

        long currentTime = world.getGameTime();
        if (!ShotgunItem.hasTimerExpired(stack, currentTime)) return;

        boolean doReload = getAttackTarget() != null ? isReloading : ShotgunItem.getAmmoInMagazineCount(stack) < magazineCapacity;

        if (!doReload) {
            if (!ShotgunItem.isSlideBack(stack)) {
                world.playSound(null, getPosX(), getPosY(), getPosZ(), AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_BACK, SoundCategory.HOSTILE, 0.5F, 1.0F);

                ShotgunItem.setSlideBack(stack, true);
                ShotgunItem.setTimerExpiryTime(stack, currentTime + midCycleDelay());

            } else {
                world.playSound(null, getPosX(), getPosY(), getPosZ(), AntiqueShotgunMod.SOUND_SHOTGUN_PUMP_FORWARD, SoundCategory.HOSTILE, 0.5F, 1.0F);

                ShotgunItem.setSlideBack(stack, false);
                ShotgunItem.setAmmoInChamber(stack, ShotgunItem.extractAmmoFromMagazine(stack));
                ShotgunItem.setTimerExpiryTime(stack, currentTime + postCycleDelay());

                if (ShotgunItem.getAmmoInMagazineCount(stack) == 0) {
                    isReloading = true;
                }
                if (getAttackTarget() == null) {
                    setAggroed(false);
                }
            }
        } else {
            if (ShotgunItem.getAmmoInMagazineCount(stack) < magazineCapacity) {
                if (!ShotgunItem.isInsertingShell(stack)) {
                    ShotgunItem.setInsertingShell(stack, true);
                    ShotgunItem.setTimerExpiryTime(stack, currentTime + shellPreInsertDelay());

                } else {
                    world.playSound(null, getPosX(), getPosY(), getPosZ(), AntiqueShotgunMod.SOUND_SHOTGUN_INSERTING_SHELL, SoundCategory.HOSTILE, 0.5F, 1.0F);

                    ShotgunItem.addAmmoToMagazine(stack, AmmoType.BUCKSHOT);
                    ShotgunItem.setInsertingShell(stack, false);
                    ShotgunItem.setTimerExpiryTime(stack, currentTime + shellPostInsertDelay());

                    if (ShotgunItem.getAmmoInMagazineCount(stack) == magazineCapacity) {
                        isReloading = false;
                    }
                }
            }
        }
    }

    // helper methods to ensure that sum of each stage delay equals reloading and shell adding durations
    private static int postFireDelay() {
        return (reloadDuration - postCycleDelay()) / 2;
    }

    private static int midCycleDelay() {
        return reloadDuration - postFireDelay() - postCycleDelay();
    }

    private static int postCycleDelay() {
        return 2;
    }

    private static int shellPreInsertDelay() {
        return (int)(shellInsertDuration * 0.35);
    }

    private static int shellPostInsertDelay() {
        return shellInsertDuration - shellPreInsertDelay();
    }
}
