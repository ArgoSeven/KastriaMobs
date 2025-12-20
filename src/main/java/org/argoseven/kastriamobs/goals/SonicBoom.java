package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;

import java.util.List;

public class SonicBoom extends Goal {
    private final MobEntity caster;
    private int cooldown;
    private final int maxCooldown;
    private final float maxRange;
    private final float damage;
    private final float vertialKnocConstant;
    private final float horiziontalKnocConstant;



    public SonicBoom(MobEntity caster, int maxCooldown, float maxRange, float damage, float verticalKnocConstant, float horizontalKnocConstant) {
        this.caster = caster;
        this.maxCooldown = maxCooldown;
        this.maxRange = maxRange;
        this.damage = damage;
        this.vertialKnocConstant = verticalKnocConstant;
        this.horiziontalKnocConstant = horizontalKnocConstant;
    }


    public SonicBoom(MobEntity caster, Config.SonicAttackConfig sonicAttack){
        this.caster = caster;
        this.maxCooldown = sonicAttack.max_cooldown;
        this.maxRange = sonicAttack.max_range;
        this.damage = sonicAttack.damage;
        this.vertialKnocConstant = sonicAttack.vertical_knock_constant;
        this.horiziontalKnocConstant = sonicAttack.horizontal_knock_constant;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target) && this.caster.canSee(target) && (caster.distanceTo(target) < maxRange + 1);
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
        Vec3d startPos = caster.getPos().add(0,1.5,0);

        Box searchBox = new Box(startPos.x - maxRange, startPos.y - 1, startPos.z - maxRange, startPos.x + maxRange, startPos.y + 1, startPos.z + maxRange);

        KastriaMobs.debugvisualizeBox(serverWorld , searchBox);

        List<LivingEntity> hits = serverWorld.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e.getClass() !=caster.getClass()
        );

        if (!caster.world.isClient) {
            serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM,caster.getX(), caster.getY() + 1.5, caster.getZ(), 1, (double)0.0F, (double)0.0F, (double)0.0F, (double)0.0F);
        }

        caster.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, 1.0F, 1.0F);
        for (LivingEntity hit : hits) {
            Vec3d direction = hit.getEyePos().subtract(startPos).normalize();
            hit.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 1, false, false));
            hit.damage(DamageSource.sonicBoom(caster), damage);
            double knockResistance = target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
            double verticalKnock = (double)vertialKnocConstant * ((double)1.0F - knockResistance);
            double horizontalKnock = (double) horiziontalKnocConstant * ((double)1.0F - knockResistance);
            hit.addVelocity(0 * horizontalKnock, direction.getY() * verticalKnock, direction.getZ() * horizontalKnock);
            hit.velocityModified = true;
        }
    }
}

