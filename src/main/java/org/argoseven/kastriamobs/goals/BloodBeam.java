package org.argoseven.kastriamobs.goals;

import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.KastriaParticles;
import org.argoseven.kastriamobs.entity.ConfigProvider;
import org.argoseven.kastriamobs.network.DebugShapePackets;

import java.util.EnumSet;
import java.util.List;

public class BloodBeam extends Goal {
    
    private static final double BEAM_START_HEIGHT = 1.6;
    private static final double PARTICLE_STEP = 0.5;
    private static final double CONE_ANGLE_FACTOR = 0.60;
    private static final double DOT_PRODUCT_THRESHOLD = 1.0E-4;
    private static final float SOUND_VOLUME = 3.0F;
    private static final float SOUND_PITCH = 1.0F;
    
    private final MobEntity caster;
    private int cooldown = 0;
    private final int maxCooldown;
    private final float maxRange;
    private final float damage;
    private final float attractionStrength;

    public <T extends MobEntity & ConfigProvider.BloodBeamProvider> BloodBeam(T caster) {
        Config.BloodBeamConfig config = caster.getBloodBeamConfig();
        this.caster = caster;
        this.maxCooldown = config.max_cooldown;
        this.maxRange = config.max_range;
        this.damage = config.damage;
        this.attractionStrength = config.attraction_strength;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.caster.canTarget(target) 
                && this.caster.canSee(target) 
                && caster.squaredDistanceTo(target) < maxRange + 1;
    }

    @Override
    public void start() {
        cooldown = maxCooldown;
    }

    @Override
    public void tick() {
        KastriaMobs.moveAndRetreat(caster, caster.getTarget(), maxRange);
        
        if (--cooldown <= 0) {
            fireBloodBeam();
            cooldown = maxCooldown;
        }
    }

    private void fireBloodBeam() {
        LivingEntity target = caster.getTarget();
        if (target == null) {
            return;
        }

        ServerWorld serverWorld = (ServerWorld) caster.world;
        Vec3d startPos = caster.getPos().add(0.0, BEAM_START_HEIGHT, 0.0);
        Vec3d direction = target.getEyePos().subtract(startPos).normalize();
        Vec3d lookVec = caster.getRotationVec(1.0F);
        Vec3d eyePos = caster.getCameraPosVec(1.0F);

        prepareCasterForAttack(target);
        spawnBeamParticles(serverWorld, startPos, direction);
        
        List<LivingEntity> hits = findEntitiesInBeam(serverWorld, lookVec, eyePos);
        applyDamageAndAttraction(hits, startPos);
    }

    private void prepareCasterForAttack(LivingEntity target) {
        caster.swingHand(Hand.MAIN_HAND);
        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
    }

    private void spawnBeamParticles(ServerWorld world, Vec3d startPos, Vec3d direction) {
        BlockStateParticleEffect bloodEffect = new BlockStateParticleEffect(
                ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.getDefaultState());
        
        for (double i = 1; i <= maxRange; i += PARTICLE_STEP) {
            Vec3d particlePos = startPos.add(direction.multiply(i));
            world.spawnParticles(KastriaParticles.BLOOD_BEAM_PARTICLE, 
                    particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            world.spawnParticles(bloodEffect, 
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

    private void applyDamageAndAttraction(List<LivingEntity> hits, Vec3d startPos) {
        caster.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SOUND_VOLUME, SOUND_PITCH);
        
        for (LivingEntity hit : hits) {
            hit.damage(DamageSource.sonicBoom(caster), damage);
            
            Vec3d attraction = startPos.subtract(hit.getEyePos()).normalize();
            hit.addVelocity(
                    attraction.getX() * attractionStrength,
                    attraction.getY() * attractionStrength,
                    attraction.getZ() * attractionStrength
            );
            hit.velocityModified = true;
        }
    }

    private boolean isEntityInCone(Vec3d eyePos, Vec3d lookVec, LivingEntity target) {
        Vec3d toEntity = target.getPos().subtract(eyePos);
        double dot = lookVec.dotProduct(toEntity);
        double lengthSquared = toEntity.lengthSquared();
        
        if (dot < DOT_PRODUCT_THRESHOLD) {
            return false;
        }
        
        return dot * dot > lengthSquared * (CONE_ANGLE_FACTOR * CONE_ANGLE_FACTOR);
    }

    private boolean isEntityInBeam(Vec3d eyePos, Vec3d lookVec, LivingEntity target, boolean debugVisualize) {
        Box entityBox = target.getBoundingBox().expand(0.5); // expand for leniency
        
        if (debugVisualize && caster.world instanceof ServerWorld serverWorld) {
            DebugShapePackets.sendDebugBox(serverWorld, entityBox, 1.0f, 0.5f, 0.0f, 1.0f, 20);
        }
        
        return entityBox.raycast(eyePos, eyePos.add(lookVec.multiply(maxRange))).isPresent();
    }
}
