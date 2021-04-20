package ewewukek.antiqueshotgun;

import java.util.Optional;
import java.util.function.Predicate;

import ewewukek.antiqueshotgun.item.AmmoItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
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

    public BulletEntity(World world) {
        super(AntiqueShotgunMod.BULLET_ENTITY_TYPE, world);
        ticksLeft = LIFETIME;
    }

    public BulletEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(world);
    }

    @Override
    public void tick() {
        if (!world.isRemote && processCollision()) {
            remove();
            return;
        }

        Vector3d motion = getMotion();

        distanceLeft -= motion.length();
        if (--ticksLeft <= 0 || distanceLeft <= 0) {
            remove();
            return;
        }

        double posX = getPosX() + motion.x;
        double posY = getPosY() + motion.y;
        double posZ = getPosZ() + motion.z;

        motion = motion.subtract(0, GRAVITY, 0);

        double friction = AIR_FRICTION;
        if (isInWater()) {
            final int count = 4;
            for (int i = 0; i != count; ++i) {
                double t = (i + 1.0) / count;
                world.addParticle(
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

        setMotion(motion.scale(friction));
        setPosition(posX, posY, posZ);
        doBlockCollisions();
    }

    public DamageSource getDamageSource(BulletEntity bullet, Entity attacker) {
        return (new IndirectEntityDamageSource("shotgun", bullet, attacker)).setProjectile();
    }

    private boolean processCollision() {
        Vector3d motion = getMotion();
        if (motion.length() > distanceLeft) {
            motion = motion.normalize().scale(distanceLeft);
        }
        Vector3d from = getPositionVec();
        Vector3d to = from.add(motion);

        BlockRayTraceResult collision = world.rayTraceBlocks(
            new RayTraceContext(from, to, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));

        // prevents hitting entities behind an obstacle
        if (collision.getType() != RayTraceResult.Type.MISS) {
            to = collision.getHitVec();
        }

        Entity target = closestEntityOnPath(from, to);
        if (target != null) {
            if (target instanceof PlayerEntity) {
                Entity shooter = func_234616_v_();
                if (shooter instanceof PlayerEntity && !((PlayerEntity)shooter).canAttackPlayer((PlayerEntity)target)) {

                    target = null;
                }
            }
            if (target != null) {
                hitEntity(target);
                return true;
            }
        }

        if (collision.getType() != RayTraceResult.Type.BLOCK) return false;

        BlockState blockstate = world.getBlockState(collision.getPos());
        blockstate.onProjectileCollision(world, blockstate, collision, this);

        AmmoItem ammoItem = ammoType.toItem();
        int impactParticleCount = (int)(0.5f * ammoItem.damage() / ammoItem.pelletCount());
        ((ServerWorld)world).spawnParticle(
            new BlockParticleData(ParticleTypes.BLOCK, blockstate),
            to.x, to.y, to.z,
            impactParticleCount + 1,
            0, 0, 0, 0.01
        );

        return true;
    }

    private void hitEntity(Entity target) {
        Entity shooter = func_234616_v_();
        DamageSource damagesource = getDamageSource(this, shooter != null ? shooter : this);

        AmmoItem ammoItem = ammoType.toItem();
        float damage = ammoItem.damage() / ammoItem.pelletCount();

        if (ammoItem.pelletCount() == 1) {
            target.attackEntityFrom(damagesource, damage);
        } else {
            DamageQueue.add(target, damagesource, damage);
        }
    }

    private Predicate<Entity> getTargetPredicate() {
        Entity shooter = func_234616_v_();
        return (entity) -> {
            return !entity.isSpectator() && entity.isAlive() && entity.canBeCollidedWith() && entity != shooter;
        };
    }

    private Entity closestEntityOnPath(Vector3d start, Vector3d end) {
        Vector3d motion = getMotion();

        Entity result = null;
        double result_dist = 0;

        AxisAlignedBB aabbSelection = getBoundingBox().expand(motion).grow(0.5);
        for (Entity entity : world.getEntitiesInAABBexcluding(this, aabbSelection, getTargetPredicate())) {
            AxisAlignedBB aabb = entity.getBoundingBox();
            Optional<Vector3d> optional = aabb.rayTrace(start, end);
            if (optional.isPresent()) {
                double dist = start.squareDistanceTo(optional.get());
                if (dist < result_dist || result == null) {
                    result = entity;
                    result_dist = dist;
                }
            }
        }

        return result;
    }

    @Override
    protected void registerData() {}

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        ammoType = AmmoType.fromByte(compound.getByte("type"));
        distanceLeft = compound.getFloat("distanceLeft");
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putByte("type", ammoType.toByte());
        compound.putFloat("distanceLeft", (float)distanceLeft);
    }

// Forge {
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer data) {
        data.writeByte(ammoType.toByte());
        Vector3d motion = getMotion();
        data.writeFloat((float)motion.x);
        data.writeFloat((float)motion.y);
        data.writeFloat((float)motion.z);
    }

    @Override
    public void readSpawnData(PacketBuffer data) {
        ammoType = AmmoType.fromByte(data.readByte());
        distanceLeft = ammoType.toItem().range();
        Vector3d motion = new Vector3d(data.readFloat(), data.readFloat(), data.readFloat());
        setMotion(motion);
    }
// }
}
