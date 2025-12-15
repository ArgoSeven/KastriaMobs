package org.argoseven.kastriamobs.goals;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.KastriaMobs;

import java.util.List;

public class SonicBeam extends Goal {
    private final MobEntity caster;
    private int cooldown = 0;
    private int maxCooldown =  60;
    private int maxRange = 7;


    public SonicBeam(MobEntity caster) {
        this.caster = caster;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target) && this.caster.canSee(target);
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
        if (target == null) return;

        Vec3d vec3d = caster.getPos().add((double)0.0F, (double)1.6F, (double)0.0F);
        Vec3d vec3d2 = target.getEyePos().subtract(vec3d);
        Vec3d vec3d3 = vec3d2.normalize();
        List<LivingEntity> hits = List.of();

        Vec3d lookVec = caster.getRotationVec(1.0F);
        caster.swingHand(Hand.MAIN_HAND);
        //caster.addVelocity(vec3d3.getX() * 0.2, 0.0, vec3d3.getZ() * 0.2);
        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());

        for(int i = 1; i < maxRange + 1; ++i) {
            Vec3d vec3d4 = vec3d.add(vec3d3.multiply((double)i));
            if (!caster.world.isClient) {
                ServerWorld serverWorld = (ServerWorld) caster.world;
                serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM,vec3d4.x, vec3d4.y, vec3d4.z, 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

        double radius = 1.0; // beam thickness
        Vec3d eyePos = caster.getCameraPosVec(1.0F);
        // direction the entity is looking
        Vec3d endPos = eyePos.add(lookVec.multiply(7));
        Box searchBox = new Box(eyePos, endPos).expand(radius);


        if (!caster.world.isClient) {
            ServerWorld serverWorld = (ServerWorld) caster.world;
            hits = serverWorld.getEntitiesByClass(LivingEntity.class, searchBox, e -> {
                Vec3d toEntity = e.getPos().subtract(eyePos).normalize();
                return e != caster && lookVec.dotProduct(toEntity) > 0.9;
            });
            KastriaMobs.debugvisualizeBox(serverWorld, searchBox);
        }



        if (!hits.isEmpty()) {
            for (LivingEntity hit : hits) {
                hit.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20,  1, false,false));
            }
        }

        caster.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 3.0F, 1.0F);
        //target.damage(DamageSource.sonicBoom(caster), 10.0F);
        //double d = (double)0.5F * ((double)1.0F - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
        //double e = (double)2.5F * ((double)1.0F - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
        //target.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
        //target.velocityModified = true;
    }

}

