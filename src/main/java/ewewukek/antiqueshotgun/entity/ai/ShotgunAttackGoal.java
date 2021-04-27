package ewewukek.antiqueshotgun.entity.ai;

import java.util.EnumSet;

import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class ShotgunAttackGoal extends Goal {
    private ElderHunterEntity shooter;
    private int aimTime;
    private boolean fire;
    private int fireTime;

    public ShotgunAttackGoal(ElderHunterEntity shooter) {
        this.shooter = shooter;
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
        shooter.setAggroed(false);
        shooter.setAttackTarget(null);
    }

    @Override
    public void tick() {
        LivingEntity target = shooter.getAttackTarget();
        if (target == null || !target.isAlive()) return;

        boolean seesEnemy = shooter.getEntitySenses().canSee(target);

        if (seesEnemy) {
            shooter.getLookController().setLookPositionWithEntity(target, 30, 30);
            if (shooter.isWeaponReady()) {
                aimTime++;
                if (aimTime > ElderHunterEntity.aimDuration) {
                    fire = true;
                }
            }
        } else {
            aimTime = 0;
        }

        if (fire) {
            fireTime++;
            if (fireTime > ElderHunterEntity.fireDelay) {
                shooter.fireWeapon(target);
                fire = false;
                fireTime = 0;
            }
        }

        if (!shooter.isWeaponReady()) {
            shooter.reloadWeapon();
        }
    }
}
