package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.KastriaParticles;
import org.argoseven.kastriamobs.entity.AbyssalCountess;

import java.util.List;

public class AbyssalCountessRayGoal extends Goal {

    private final MobEntity caster;
    private int cooldown = 0;
    private final int maxCooldown;
    private double maxRange = 20;
    private double minRange = 3;

    public <T extends MobEntity> AbyssalCountessRayGoal(PathAwareEntity caster) {
        this.caster = caster;
        this.maxCooldown = 40;
    }

    @Override
    public boolean canStart() {
        return caster instanceof AbyssalCountess entity
            && entity.getTurretTarget() != null;
    }

    @Override
    public void tick() {
        if (--cooldown > 0) {return;}
        AbyssalCountess entity = (AbyssalCountess) caster;
        LivingEntity target = entity.getTurretTarget();

        if (target == null || !target.isAlive()) {
            entity.setTurretTarget(null);
            return;
        }


        if (caster.squaredDistanceTo(target) > maxRange * maxRange) {
            entity.setTurretTarget(null);
            return;
        }

        if (caster.squaredDistanceTo(target) < minRange * minRange) {
            return;
        }

        if (caster.world instanceof  ServerWorld serverWorld){
            spawnBeamParticles(serverWorld, caster.getEyePos().add(0, 0.6, 0), target.getPos().add(0,0.5,0));

            Vec3d lookVec = caster.getRotationVec(1.0F);
            Vec3d eyePos = caster.getCameraPosVec(1.0F);


            List<LivingEntity> hits = findEntitiesInBeam(serverWorld, lookVec, eyePos, 10);
            Vec3d startPos = caster.getPos().add(0.0, 0.6, 0.0);
            applyDamageAndAttraction(hits, startPos);
        }

        cooldown = maxCooldown;
    }

    private void spawnBeamParticles(ServerWorld world, Vec3d startPos, Vec3d endPos) {
        Vec3d d = endPos.subtract(startPos).normalize();
        for (int i = 1; i <= endPos.subtract(startPos).length() + 3; i++) {
            Vec3d particlePos = startPos.add(d.multiply(i));
            world.spawnParticles(KastriaParticles.BLOOD_BEAM_PARTICLE,
                    particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void applyDamageAndAttraction(List<LivingEntity> hits, Vec3d startPos) {
        caster.playSound(SoundEvents.BLOCK_SCULK_SHRIEKER_STEP, 3, 1);

        for (LivingEntity hit : hits) {
            hit.damage(DamageSource.sonicBoom(caster), 2);

            Vec3d attraction = startPos.subtract(hit.getEyePos()).normalize();
            hit.addVelocity(
                    attraction.getX() *  1.5,
                    attraction.getY() *  1.5,
                    attraction.getZ() *  1.5
            );
            hit.velocityModified = true;
        }
    }

    private boolean isEntityInCone(Vec3d eyePos, Vec3d lookVec, LivingEntity target) {
        Vec3d toEntity = target.getPos().subtract(eyePos);
        double dot = lookVec.dotProduct(toEntity);
        double lengthSquared = toEntity.lengthSquared();

        if (dot < 1.0E-4) {
            return false;
        }

        return dot * dot > lengthSquared * (0.60 * 1.0E-4);
    }

    private List<LivingEntity> findEntitiesInBeam(ServerWorld world, Vec3d lookVec, Vec3d eyePos, int attackRange) {
        Box searchBox = caster.getBoundingBox().stretch(lookVec.multiply(attackRange));

        return world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != caster
                        && !entity.isTeammate(caster)
                        && isEntityInCone(eyePos, lookVec, entity)
                        && !entity.isSpectator()
        );
    }

}
