package org.argoseven.kastriamobs.goals;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.KastriaParticles;
import org.argoseven.kastriamobs.KastriaUtils;

import java.util.EnumSet;
import java.util.List;


public class AbyssalCountessExplosionGoal extends Goal {

    private final MobEntity caster;
    private int cooldown = 0;
    private final int maxCooldown;
    private int explosionRange = 7;


public <T extends MobEntity> AbyssalCountessExplosionGoal(T caster) {
            this.caster = caster;
            this.maxCooldown = 20;
            //this.setControls(EnumSet.of(Control.LOOK, Control.MOVE));
}

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.caster.canTarget(target) && this.caster.canSee(target) && this.caster.distanceTo(target) < explosionRange;
    }


    @Override
    public void start() {
        cooldown =  20;
    }

    @Override
    public void tick() {
        if (--cooldown <= 0) {
            explode();
            cooldown = maxCooldown;
        }
    }

    private void explode() {
        LivingEntity target = caster.getTarget();
        if (target == null) {
            return;
        }
        if (caster instanceof MutipleAttack ){
            ((MutipleAttack) caster).setAttackAnimation("explosion_p2");
        }
        caster.swingHand(Hand.MAIN_HAND);


        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
        Vec3d startPos = caster.getPos().add(0, 0.6, 0);
        Vec3d lookVec = caster.getRotationVec(1.0F);
        Vec3d eyePos = caster.getCameraPosVec(1.0F);
        ServerWorld serverworld = (ServerWorld) caster.world;

        serverworld.spawnParticles(ParticleTypes.EXPLOSION, caster.getX(), caster.getY(), caster.getZ(), 100, 1.0, 1.0, 1.0, 0.3);
        List<LivingEntity> hits = KastriaUtils.findEntitiesInBeam(caster, lookVec, eyePos, 10D);
        for (LivingEntity hit : hits) {
            Vec3d direction = hit.getEyePos().subtract(startPos).normalize();
            KastriaUtils.applyDamageAndKnockback(caster , hit, direction,1D, 1);
        }
    }

}
