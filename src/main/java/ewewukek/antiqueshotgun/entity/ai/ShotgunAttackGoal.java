package ewewukek.antiqueshotgun.entity.ai;

import java.util.EnumSet;

import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

public class ShotgunAttackGoal extends Goal {
    private ElderHunterEntity shooter;
    private int aimTime;

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

        if (seesEnemy) {
            shooter.getLookController().setLookPositionWithEntity(target, 30, 30);
            if (shooter.isWeaponReady()) {
                aimTime++;
                if (aimTime > ElderHunterEntity.aimDuration) {
                    shooter.fireWeapon(target);
                    aimTime = 0;
                }
            }
        } else {
            aimTime = 0;
        }
    }
}
