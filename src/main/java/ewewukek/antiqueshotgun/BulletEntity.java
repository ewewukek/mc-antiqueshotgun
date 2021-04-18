package ewewukek.antiqueshotgun;

import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

public class BulletEntity extends ThrowableEntity implements IEntityAdditionalSpawnData {
    static final double GRAVITY = 0.05;
    static final double AIR_FRICTION = 0.99;
    static final double WATER_FRICTION = 0.6;
    static final short LIFETIME = 30;

    public short ticksLeft;
    public Vector3d origin;
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
        if (origin == null) origin = getPositionVec();
        double distanceTravelled = getPositionVec().subtract(origin).length();

        if (--ticksLeft <= 0 || distanceTravelled > ammoType.toItem().range()) {
            remove();
            return;
        }

        Vector3d motion = getMotion();
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

    @Override
    protected void registerData() {}

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        ammoType = AmmoType.fromByte(compound.getByte("type"));
        CompoundNBT originTag = compound.getCompound("origin");
        if (originTag != null) {
            origin = new Vector3d(
                originTag.getFloat("x"),
                originTag.getFloat("y"),
                originTag.getFloat("z")
            );
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putByte("type", ammoType.toByte());
        CompoundNBT originTag = new CompoundNBT();
        originTag.putFloat("x", (float)origin.x);
        originTag.putFloat("y", (float)origin.y);
        originTag.putFloat("z", (float)origin.z);
        compound.put("origin", originTag);
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
        Vector3d motion = new Vector3d(data.readFloat(), data.readFloat(), data.readFloat());
        setMotion(motion);
    }
// }
}
