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
        setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = shooter.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        super.stop();
        aimTime = 0;
        shooter.setTarget(null);
    }

    @Override
    public void start() {
        super.start();
        shooter.setAggressive(true);
    }

    @Override
    public void tick() {
        LivingEntity target = shooter.getTarget();
        if (target == null || !target.isAlive()) return;

        boolean seesEnemy = shooter.getSensing().canSee(target);
        boolean inAttackRange = shooter.distanceToSqr(target) < attackRange * attackRange;

        if (seesEnemy) {
            shooter.getLookControl().setLookAt(target, 30, 30);
            shooter.yRot = shooter.yRot;
            if (isAiming) {
                aimTime++;
                if (aimTime > ElderHunterEntity.aimDuration) {
                    shooter.fireWeapon(target);
                    isAiming = false;
                }
            }
        } else {
            isAiming = false;
        }

        if (!isAiming) {
            if (seesEnemy && inAttackRange && shooter.isWeaponReady()) {
                shooter.getNavigation().stop();
                aimTime = 0;
                isAiming = true;
            } else {
                shooter.getNavigation().moveTo(target, shooter.isWeaponReady() ? speed : speed * 0.5f);
            }
        }
    }
}
