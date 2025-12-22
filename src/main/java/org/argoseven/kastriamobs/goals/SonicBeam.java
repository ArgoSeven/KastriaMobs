package org.argoseven.kastriamobs.goals;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;

import java.util.List;

public class SonicBeam extends Goal {
    private final MobEntity caster;
    private int cooldown = 0;
    private final int maxCooldown;
    private final float maxRange;
    private final float damage;
    private final float vertialKnocConstant;
    private final float horiziontalKnocConstant;


    public SonicBeam(MobEntity caster, int maxCooldown, float maxRange, float damage, float verticalKnocConstant, float horizontalKnocConstant) {
        this.caster = caster;
        this.maxCooldown = maxCooldown;
        this.maxRange = maxRange;
        this.damage = damage;
        this.vertialKnocConstant = verticalKnocConstant;
        this.horiziontalKnocConstant = horizontalKnocConstant;
    }


    public SonicBeam(MobEntity caster, Config.SonicAttackConfig beamConfig){
        this.caster = caster;
        this.maxCooldown = beamConfig.max_cooldown;
        this.maxRange = beamConfig.max_range;
        this.damage = beamConfig.damage;
        this.vertialKnocConstant = beamConfig.vertical_knock_constant;
        this.horiziontalKnocConstant = beamConfig.horizontal_knock_constant;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target) && this.caster.canSee(target) && (caster.squaredDistanceTo(target) < KastriaMobs.getSquared(maxRange) + 1);
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
            fireSonicBoom();
            cooldown = maxCooldown;
        }
    }

    protected void fireSonicBoom() {
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

        for (int i = 1; i <= maxRange; i++) {
            Vec3d particlePos = startPos.add(direction.multiply(i));
            if (!caster.world.isClient) {serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);}
        }

        Box searchBox = caster.getBoundingBox().stretch(lookVec.multiply(maxRange));

        //wKastriaMobs.debugvisualizeBox(serverWorld, searchBox);

        List<LivingEntity> hits = serverWorld.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e != caster && !e.isTeammate(caster) && isEntityInTheCone(eyePos, lookVec, e)
        );


        caster.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 3.0F, 1.0F);
        for (LivingEntity hit : hits) {
            hit.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 1, false, false));
            hit.damage(DamageSource.sonicBoom(caster), damage);
            double knockResistance = target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
            double verticalKnock = (double)vertialKnocConstant * ((double)1.0F - knockResistance);
            double horizontalKnock = (double)horiziontalKnocConstant * ((double)1.0F - knockResistance);
            Vec3d d = hit.getEyePos().subtract(startPos).normalize();
            hit.addVelocity(d.getX() * horizontalKnock, d.getY() * verticalKnock, d.getZ() * horizontalKnock);
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

