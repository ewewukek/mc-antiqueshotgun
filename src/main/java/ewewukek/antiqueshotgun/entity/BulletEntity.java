package ewewukek.antiqueshotgun.entity;

import java.util.Optional;
import java.util.function.Predicate;

import ewewukek.antiqueshotgun.AmmoType;
import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.DamageQueue;
import ewewukek.antiqueshotgun.item.AmmoItem;
import ewewukek.antiqueshotgun.item.RubberAmmoItem;
import ewewukek.antiqueshotgun.item.ThermiteAmmoItem;
import ewewukek.antiqueshotgun.item.WitherAmmoItem;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class BulletEntity extends ThrowableEntity implements IEntityAdditionalSpawnData {
    static final double GRAVITY = 0.05;
    static final double AIR_FRICTION = 0.99;
    static final double WATER_FRICTION = 0.6;
    static final short LIFETIME = 30;

    public short ticksLeft;
    public double distanceLeft;
    public AmmoType ammoType;
    public float damageMultiplier;

    public BulletEntity(World world) {
        super(AntiqueShotgunMod.BULLET_ENTITY_TYPE, world);
        ticksLeft = LIFETIME;
    }

    public BulletEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(world);
    }

    @Override
    public void tick() {
        if (!level.isClientSide && processCollision()) {
            remove();
            return;
        }

        Vector3d motion = getDeltaMovement();

        distanceLeft -= motion.length();
        if (--ticksLeft <= 0 || distanceLeft <= 0) {
            remove();
            return;
        }

        double posX = getX() + motion.x;
        double posY = getY() + motion.y;
        double posZ = getZ() + motion.z;

        motion = motion.subtract(0, GRAVITY, 0);

        double friction = AIR_FRICTION;
        if (isInWater()) {
            final int count = 4;
            for (int i = 0; i != count; ++i) {
                double t = (i + 1.0) / count;
                level.addParticle(
                    ParticleTypes.BUBBLE,
                    posX - motion.x * t,
                    posY - motion.y * t,
                    posZ - motion.z * t,
                    motion.x,
                    motion.y,
                    motion.z
                );
            }
            friction = WATER_FRICTION;
        }

        setDeltaMovement(motion.scale(friction));
        setPos(posX, posY, posZ);
        checkInsideBlocks();
    }

    public DamageSource getDamageSource(BulletEntity bullet, Entity attacker) {
        return (new IndirectEntityDamageSource("shotgun", bullet, attacker)).setProjectile();
    }

    private boolean processCollision() {
        Vector3d motion = getDeltaMovement();
        if (motion.length() > distanceLeft) {
            motion = motion.normalize().scale(distanceLeft);
        }
        Vector3d from = position();
        Vector3d to = from.add(motion);

        BlockRayTraceResult collision = level.clip(
            new RayTraceContext(from, to, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));

        // prevents hitting entities behind an obstacle
        if (collision.getType() != RayTraceResult.Type.MISS) {
            to = collision.getLocation();
        }

        Entity target = closestEntityOnPath(from, to);
        if (target != null) {
            if (target instanceof PlayerEntity) {
                Entity shooter = getOwner();
                if (shooter instanceof PlayerEntity && !((PlayerEntity)shooter).canHarmPlayer((PlayerEntity)target)) {

                    target = null;
                }
            }
            if (target != null) {
                hitEntity(target);
                return true;
            }
        }

        if (collision.getType() != RayTraceResult.Type.BLOCK) return false;

        BlockState blockstate = level.getBlockState(collision.getBlockPos());
        blockstate.onProjectileHit(level, blockstate, collision, this);

        AmmoItem ammoItem = ammoType.toItem();
        int impactParticleCount = (int)(0.5f * ammoItem.damage() / ammoItem.pelletCount());
        ((ServerWorld)level).sendParticles(
            new BlockParticleData(ParticleTypes.BLOCK, blockstate),
            to.x, to.y, to.z,
            impactParticleCount + 1,
            0, 0, 0, 0.01
        );

        return true;
    }

    private void hitEntity(Entity target) {
        Entity shooter = getOwner();
        DamageSource damagesource = getDamageSource(this, shooter != null ? shooter : this);

        AmmoItem ammoItem = ammoType.toItem();
        float damage = damageMultiplier * ammoItem.damage() / ammoItem.pelletCount();

        if (ammoItem.pelletCount() == 1) {
            target.hurt(damagesource, damage);
        } else {
            DamageQueue.add(target, damagesource, damage);
        }

        if (target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)target;
            if (ammoType == AmmoType.RUBBER) {
                Vector3d knockback = getDeltaMovement().multiply(1, 0, 1).normalize().scale(RubberAmmoItem.knockbackForce);
                if (knockback.lengthSqr() > 0) {
                    livingEntity.push(knockback.x, 0.1, knockback.z);
                }

                livingEntity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, (int)(RubberAmmoItem.slownessDuration * 20), RubberAmmoItem.slownessLevel - 1));
                livingEntity.addEffect(new EffectInstance(Effects.WEAKNESS, (int)(RubberAmmoItem.weaknessDuration * 20)));
                livingEntity.addEffect(new EffectInstance(Effects.CONFUSION, (int)(RubberAmmoItem.nauseaDuration * 20)));

            } else if (ammoType == AmmoType.THERMITE) {
                int fireTicks = ProtectionEnchantment.getFireAfterDampener(livingEntity, (int)(ThermiteAmmoItem.secondsOnFire * 20));
                int maxFireTicks = fireTicks * ThermiteAmmoItem.pelletCount;
                int prevFireTicks = livingEntity.getRemainingFireTicks();
                fireTicks = Math.min(prevFireTicks + fireTicks, maxFireTicks);
                if (prevFireTicks < fireTicks) {
                    livingEntity.setRemainingFireTicks(fireTicks);
                }

            } else if (ammoType == AmmoType.WITHER) {
                int level = WitherAmmoItem.effectLevel;
                int maxLevel = level * WitherAmmoItem.pelletCount;
                EffectInstance effect = livingEntity.getEffect(Effects.WITHER);
                if (effect != null) {
                    level = Math.min(level + effect.getAmplifier() + 1, maxLevel);
                }
                livingEntity.addEffect(new EffectInstance(Effects.WITHER, (int)(WitherAmmoItem.effectDuration * 20), level - 1));
            }
        }
    }

    private Predicate<Entity> getTargetPredicate() {
        Entity shooter = getOwner();
        return (entity) -> {
            return !entity.isSpectator() && entity.isAlive() && entity.isPickable() && entity != shooter;
        };
    }

    private Entity closestEntityOnPath(Vector3d start, Vector3d end) {
        Vector3d motion = getDeltaMovement();

        Entity result = null;
        double result_dist = 0;

        AxisAlignedBB aabbSelection = getBoundingBox().expandTowards(motion).inflate(0.5);
        for (Entity entity : level.getEntities(this, aabbSelection, getTargetPredicate())) {
            AxisAlignedBB aabb = entity.getBoundingBox();
            Optional<Vector3d> optional = aabb.clip(start, end);
            if (!optional.isPresent()) {
                aabb = aabb.move( // previous tick position
                    entity.xOld - entity.getX(),
                    entity.yOld - entity.getY(),
                    entity.zOld - entity.getZ()
                );
                optional = aabb.clip(start, end);
            }
            if (optional.isPresent()) {
                double dist = start.distanceToSqr(optional.get());
                if (dist < result_dist || result == null) {
                    result = entity;
                    result_dist = dist;
                }
            }
        }

        return result;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        ammoType = AmmoType.fromByte(compound.getByte("type"));
        distanceLeft = compound.getFloat("distanceLeft");
        damageMultiplier = compound.getFloat("damageMultiplier");
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("type", ammoType.toByte());
        compound.putFloat("distanceLeft", (float)distanceLeft);
        compound.putFloat("damageMultiplier", damageMultiplier);
    }

// Forge {
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer data) {
        data.writeByte(ammoType.toByte());
        Vector3d motion = getDeltaMovement();
        data.writeFloat((float)motion.x);
        data.writeFloat((float)motion.y);
        data.writeFloat((float)motion.z);
    }

    @Override
    public void readSpawnData(PacketBuffer data) {
        ammoType = AmmoType.fromByte(data.readByte());
        distanceLeft = ammoType.toItem().range();
        Vector3d motion = new Vector3d(data.readFloat(), data.readFloat(), data.readFloat());
        setDeltaMovement(motion);
    }
// }
}
