package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.entity.ConfigProvider;

import java.util.List;

public class SonicBeam extends AbstractSonicAttack {
    
    private static final double BEAM_START_HEIGHT = 1.6;

    public <T extends MobEntity & ConfigProvider.SonicBeamProvider> SonicBeam(T caster) {
        super(caster, caster.getSonicBeamConfig());
    }

    @Override
    protected void executeAttack() {
        LivingEntity target = caster.getTarget();
        if (target == null || caster.distanceTo(target) > maxRange + 1) {
            return;
        }

        ServerWorld serverWorld = getServerWorld();
        Vec3d startPos = caster.getPos().add(0.0, BEAM_START_HEIGHT, 0.0);
        Vec3d direction = target.getEyePos().subtract(startPos).normalize();
        Vec3d lookVec = caster.getRotationVec(1.0F);
        Vec3d eyePos = caster.getCameraPosVec(1.0F);

        prepareCasterForAttack(target);
        spawnBeamParticles(serverWorld, startPos, direction);
        
        List<LivingEntity> hits = findEntitiesInBeam(serverWorld, lookVec, eyePos);
        dealDamageAndKnockback(hits, startPos);
    }

    private void spawnBeamParticles(ServerWorld world, Vec3d startPos, Vec3d direction) {
        for (int i = 1; i <= maxRange; i++) {
            Vec3d particlePos = startPos.add(direction.multiply(i));
            world.spawnParticles(ParticleTypes.SONIC_BOOM, 
                    particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private List<LivingEntity> findEntitiesInBeam(ServerWorld world, Vec3d lookVec, Vec3d eyePos) {
        Box searchBox = caster.getBoundingBox().stretch(lookVec.multiply(maxRange));
        
        return world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != caster 
                        && !entity.isTeammate(caster) 
                        && isEntityInCone(eyePos, lookVec, entity)
        );
    }
}
