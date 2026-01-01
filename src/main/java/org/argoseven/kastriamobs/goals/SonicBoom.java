package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.ConfigProvider;
import org.argoseven.kastriamobs.network.DebugShapePackets;

import java.util.List;

public class SonicBoom extends AbstractSonicAttack {
    
    private static final double BOOM_START_HEIGHT = 1.5;
    private static final double BOOM_VERTICAL_RANGE = 1.0;

    public <T extends MobEntity & ConfigProvider.SonicBoomProvider> SonicBoom(T caster) {
        super(caster, caster.getSonicBoomConfig());
    }

    @Override
    protected void handleMovement(LivingEntity target) {
    }

    @Override
    protected void executeAttack() {
        LivingEntity target = caster.getTarget();
        if (target == null || caster.squaredDistanceTo(target) > KastriaMobs.getSquared(attackRange)) {
            return;
        }

        ServerWorld serverWorld = getServerWorld();
        Vec3d startPos = caster.getPos().add(0, BOOM_START_HEIGHT, 0);

        prepareCasterForAttack(target);
        spawnBoomParticle(serverWorld);
        
        List<LivingEntity> hits = findEntitiesInRadius(serverWorld, startPos);
        applyRadialDamageAndKnockback(hits, startPos);
    }

    private void spawnBoomParticle(ServerWorld world) {
        world.spawnParticles(ParticleTypes.SONIC_BOOM,
                caster.getX(), caster.getY() + BOOM_START_HEIGHT, caster.getZ(),
                1, 0.0, 0.0, 0.0, 0.0);
    }

    private List<LivingEntity> findEntitiesInRadius(ServerWorld world, Vec3d startPos) {
        Box searchBox = new Box(
                startPos.x - attackRange, startPos.y - BOOM_VERTICAL_RANGE, startPos.z - attackRange,
                startPos.x + attackRange, startPos.y + BOOM_VERTICAL_RANGE, startPos.z + attackRange
        );

        if (DebugShapePackets.isDebugEnabled()) {
            DebugShapePackets.sendDebugBox(world, searchBox, 0.0f, 1.0f, 0.0f, 0.5f, 20);
        }

        return world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> entity.getClass() != caster.getClass() && !entity.isTeammate(caster)
        );
    }

    private void applyRadialDamageAndKnockback(List<LivingEntity> hits, Vec3d startPos) {
        caster.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 1.0F, 1.0F);
        
        for (LivingEntity hit : hits) {
            Vec3d direction = hit.getEyePos().subtract(startPos).normalize();
            hit.damage(net.minecraft.entity.damage.DamageSource.sonicBoom(caster), damage);
            applyKnockback(hit, direction);
        }
    }
}
