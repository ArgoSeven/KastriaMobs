package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

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

            serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK,caster.getX(), caster.getY() + 1, caster.getZ(), 3, 0.0, 1, 0.0, 0.3);
            this.mob.tryAttack(target);
        }
    }
}
