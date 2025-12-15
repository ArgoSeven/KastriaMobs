package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SonicBoom extends Goal {
    private final MobEntity caster;
    private int cooldown;
    private int maxCooldown =  60;


    public SonicBoom(MobEntity caster) {
        this.caster = caster;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target);
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
        Vec3d vec3d3 = target.getEyePos().subtract(vec3d).normalize();


        if (!caster.world.isClient) {
            ServerWorld serverWorld = (ServerWorld) caster.world;
            serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM,caster.getX(), caster.getY() + 1.5, caster.getZ(), 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
        }

        caster.playSound(SoundEvents.ENTITY_CAT_HISS, 1.0F, 1.0F);
        target.damage(DamageSource.sonicBoom(caster), 10.0F);
        Vec3d direction = target.getPos().subtract(caster.getPos()).normalize();

        target.addVelocity(direction.x * 3, direction.y, direction.z * 3);
        target.velocityModified = true;
    }
}

