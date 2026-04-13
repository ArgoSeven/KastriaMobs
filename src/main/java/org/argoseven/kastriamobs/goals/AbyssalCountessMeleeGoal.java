package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.argoseven.kastriamobs.KastriaParticles;

public class AbyssalCountessMeleeGoal extends MeleeAttackGoal {

    private final MobEntity caster;

    public <T extends MobEntity> AbyssalCountessMeleeGoal(PathAwareEntity caster) {
        super(caster, 1, false);
        this.caster = caster;
    }


    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double d = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= d && this.getCooldown() <= 0) {
            this.resetCooldown();
            this.mob.swingHand(Hand.MAIN_HAND);
            ServerWorld serverWorld = (ServerWorld) caster.world;

            serverWorld.spawnParticles(KastriaParticles.CLAW_SWEEP,caster.getX(), caster.getEyeY(), caster.getZ(), 0, 0, 0, 0.0, 0);
            this.mob.tryAttack(target);

            if (caster instanceof  MutipleAttack) {
                ((MutipleAttack) caster).setAttackAnimation("melee");
            }

            caster.swingHand(Hand.MAIN_HAND);
        }
    }
}
