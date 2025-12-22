package org.argoseven.kastriamobs.goals;

import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import org.argoseven.kastriamobs.ModParticles;

import java.util.List;

public class BloodBeam extends Goal {
    private final MobEntity caster;
    private int cooldown = 0;
    private final int maxCooldown;
    private final float maxRange;
    private final float damage;
    private final float attractionStrength;


    public BloodBeam(MobEntity caster, int maxCooldown, float maxRange, float damage, float attractionStrength) {
        this.caster = caster;
        this.maxCooldown = maxCooldown;
        this.maxRange = maxRange;
        this.damage = damage;
        this.attractionStrength = attractionStrength;
    }


    public BloodBeam(MobEntity caster, Config.BloodBeamConfig beamConfig){
        this.caster = caster;
        this.maxCooldown = beamConfig.max_cooldown;
        this.maxRange = beamConfig.max_range;
        this.damage = beamConfig.damage;
        this.attractionStrength = beamConfig.attraction_strength;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target) && this.caster.canSee(target) && (caster.squaredDistanceTo(target) < maxRange + 1);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target);
    }

    @Override
    public void start() {
        cooldown = 0;
    }

    @Override
    public void tick() {
        if (--cooldown <= 0) {
            fireBloodBeam();
            cooldown = maxCooldown;
        }
    }

    protected void fireBloodBeam() {
        LivingEntity target = caster.getTarget();
        float distanceFromTarget = caster.distanceTo(target);
        if (target == null || distanceFromTarget > maxRange + 1) return;

        ServerWorld serverWorld = (ServerWorld) caster.world;
        Vec3d startPos = caster.getPos().add(0.0, 1.6, 0.0);
        Vec3d direction = target.getEyePos().subtract(startPos).normalize();
        Vec3d lookVec = caster.getRotationVec(1.0F);
        Vec3d eyePos = caster.getCameraPosVec(1.0F);

        caster.swingHand(Hand.MAIN_HAND);
        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());

        BlockStateParticleEffect bloodEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.getDefaultState());
        for (double i = 1; i <= maxRange; i+= 0.5) {
            Vec3d particlePos = startPos.add(direction.multiply(i));
            if (!caster.world.isClient) {
                serverWorld.spawnParticles(ModParticles.BLOOD_BEAM_PARTICLE, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
                serverWorld.spawnParticles(bloodEffect, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }

        Box searchBox = caster.getBoundingBox().stretch(lookVec.multiply(maxRange));


        List<LivingEntity> hits = serverWorld.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e != caster && !e.isTeammate(caster) && isEntityInTheCone(eyePos, lookVec, e)
        );


        caster.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 3.0F, 1.0F);
        for (LivingEntity hit : hits) {
            hit.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 1, false, false));
            hit.damage(DamageSource.sonicBoom(caster), damage);

            // Calculate the attraction vector
            Vec3d attraction = startPos.subtract(hit.getEyePos()).normalize();
            hit.addVelocity(attraction.getX() * attractionStrength, attraction.getY() * attractionStrength, attraction.getZ() * attractionStrength);
            hit.velocityModified = true;
        }

    }


    private boolean isEntityInTheCone(Vec3d eyePos, Vec3d lookVec, LivingEntity target) {
        Vec3d toEntity = target.getPos().subtract(eyePos);
        double dot = lookVec.dotProduct(toEntity);
        double lenSq = toEntity.lengthSquared();
        if (dot < 1.0E-4) return false;
        // 0.70 ~45° 0.50 60°
        return dot * dot > lenSq * (0.60 * 0.60);
    }

    private boolean isEntityInTheBeam(Vec3d eyePos, Vec3d lookVec, LivingEntity target) {
        Box entityBox = target.getBoundingBox().expand(0.5); // expand for leniency
        KastriaMobs.debugvisualizeBox((ServerWorld) target.world, entityBox);
        return entityBox.raycast(eyePos, eyePos.add(lookVec.multiply(maxRange))).isPresent();
    }

}

