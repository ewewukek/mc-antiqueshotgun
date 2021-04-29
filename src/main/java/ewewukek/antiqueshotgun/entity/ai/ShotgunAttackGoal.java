package ewewukek.antiqueshotgun.entity.ai;

import java.util.EnumSet;

import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class ShotgunAttackGoal extends Goal {
    private ElderHunterEntity shooter;
    private int aimTime;
    private float speed;
    private float attackRange;
    private boolean isAiming;

    public ShotgunAttackGoal(ElderHunterEntity shooter, float speed, float attackRange) {
        this.shooter = shooter;
        this.speed = speed;
        this.attackRange = attackRange;
        setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity target = shooter.getAttackTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void resetTask() {
        super.resetTask();
        aimTime = 0;
        shooter.setAttackTarget(null);
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        shooter.setAggroed(true);
    }

    @Override
    public void tick() {
        LivingEntity target = shooter.getAttackTarget();
        if (target == null || !target.isAlive()) return;

        boolean seesEnemy = shooter.getEntitySenses().canSee(target);
        boolean inAttackRange = shooter.getDistanceSq(target) < attackRange * attackRange;

        if (!isAiming && seesEnemy && inAttackRange && shooter.isWeaponReady()) {
            shooter.getNavigator().clearPath();
            aimTime = 0;
            isAiming = true;
        }

        if (isAiming) {
            if (seesEnemy) {
                shooter.getLookController().setLookPositionWithEntity(target, 30, 30);
                aimTime++;
                if (aimTime > ElderHunterEntity.aimDuration) {
                    shooter.fireWeapon(target);
                    isAiming = false;
                }
            } else {
                isAiming = false;
            }
        } else {
            shooter.getNavigator().tryMoveToEntityLiving(target, shooter.isWeaponReady() ? speed : speed * 0.5f);
        }
    }
}
