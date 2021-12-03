package ewewukek.antiqueshotgun.entity.ai;

import java.util.EnumSet;
import java.util.Random;

import ewewukek.antiqueshotgun.entity.ElderHunterEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;

public class ShotgunAttackGoal extends Goal {
    public static final int MELEE_COOLDOWN = 20;

    private final Random random = new Random();
    private ElderHunterEntity shooter;
    private float speed;
    private float minRange;
    private float attackRange;
    private boolean isAiming;
    private int aimTime;
    private boolean doMeleeAttack;
    private int meleeTimer;

    public ShotgunAttackGoal(ElderHunterEntity shooter, float speed, float minRange, float attackRange) {
        this.shooter = shooter;
        this.speed = speed;
        this.minRange = minRange;
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
        double distanceSqr = shooter.distanceToSqr(target);
        boolean inAttackRange = distanceSqr < attackRange * attackRange;
        boolean inMeleeRange = distanceSqr < getMeleeRangeSqr(target);

        if (seesEnemy) {
            shooter.getLookControl().setLookAt(target, 30, 30);
            if (doMeleeAttack && inMeleeRange && meleeTimer <= 0) {
                shooter.lookAt(target, 30, 30);
                shooter.swing(Hand.MAIN_HAND);
                shooter.doHurtTarget(target);
                isAiming = false;
                doMeleeAttack = false;
                meleeTimer = MELEE_COOLDOWN;
            }
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
                doMeleeAttack = random.nextFloat() < ElderHunterEntity.meleeChance;
            } else if (seesEnemy && distanceSqr < minRange * minRange) {
                shooter.getNavigation().stop();
            } else {
                shooter.getNavigation().moveTo(target, shooter.isWeaponReady() ? speed : speed * 0.5f);
            }
        }

        if (meleeTimer > 0) {
            meleeTimer--;
        }
    }

    public float getMeleeRangeSqr(LivingEntity target) {
        return shooter.getBbWidth() * shooter.getBbWidth() * 4 + target.getBbWidth();
    }
}
