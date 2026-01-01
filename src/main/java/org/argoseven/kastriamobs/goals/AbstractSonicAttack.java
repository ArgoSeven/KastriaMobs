package org.argoseven.kastriamobs.goals;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.KastriaParticles;

import java.util.List;

public abstract class AbstractSonicAttack extends Goal {
    
    private static final float SOUND_VOLUME = 3.0F;
    private static final float SOUND_PITCH = 1.0F;
    protected static final double CONE_ANGLE_FACTOR = 0.60;
    protected static final double DOT_PRODUCT_THRESHOLD = 1.0E-4;
    
    protected final MobEntity caster;
    protected int cooldown = 0;
    protected final int maxCooldown;
    protected final float activationRange;
    protected final float attackRange;
    protected final float damage;
    protected final float verticalKnockback;
    protected final float horizontalKnockback;

    private int particleTimer = 0;
    private final int particleInterval = 10;

    protected AbstractSonicAttack(MobEntity caster, Config.SonicAttackConfig config) {
        this.caster = caster;
        this.maxCooldown = config.max_cooldown;
        this.attackRange = config.attack_range;
        this.activationRange = config.aggro_range;
        this.damage = config.damage;
        this.verticalKnockback = config.vertical_knock_constant;
        this.horizontalKnockback = config.horizontal_knock_constant;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.caster.canTarget(target) 
                && this.caster.canSee(target) 
                && caster.squaredDistanceTo(target) < KastriaMobs.getSquared(activationRange) + 1;
    }

    @Override
    public void start() {
        cooldown = maxCooldown;
    }

    @Override
    public void tick() {
        LivingEntity target = caster.getTarget();
        if (target != null) {
            handleMovement(target);
        }
        if (--cooldown <= 0) {
            executeAttack();
            cooldown = maxCooldown;
        }

        particleTimer++;
        if(particleTimer >= particleInterval){
            particleTimer = 0;
            if (caster.world instanceof ServerWorld serverWorld) { serverWorld.spawnParticles(KastriaParticles.SONIC_CHARGE, caster.getX(), caster.getRandomBodyY(), caster.getZ(), 1, 0.1, 0.3, 0.1, 0.1 ); }
        }

    }

    protected void handleMovement(LivingEntity target) {
        KastriaMobs.moveAndRetreat(caster, target, attackRange);
    }

    protected abstract void executeAttack();

    protected void applyKnockback(LivingEntity hit, Vec3d direction) {
        double knockResistance = hit.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        double verticalKnock = verticalKnockback * (1.0 - knockResistance);
        double horizontalKnock = horizontalKnockback * (1.0 - knockResistance);
        
        hit.addVelocity(
                direction.getX() * horizontalKnock, 
                direction.getY() * verticalKnock, 
                direction.getZ() * horizontalKnock
        );
        hit.velocityModified = true;
    }

    protected void dealDamageAndKnockback(List<LivingEntity> hits, Vec3d startPos) {
        caster.playSound(SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SOUND_VOLUME, SOUND_PITCH);
        
        for (LivingEntity hit : hits) {
            hit.damage(DamageSource.sonicBoom(caster), damage);
            Vec3d direction = hit.getEyePos().subtract(startPos).normalize();
            applyKnockback(hit, direction);
        }
    }

    protected void prepareCasterForAttack(LivingEntity target) {
        caster.swingHand(Hand.MAIN_HAND);
        caster.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, target.getEyePos());
    }

    protected boolean isEntityInCone(Vec3d eyePos, Vec3d lookVec, LivingEntity target) {
        Vec3d toEntity = target.getPos().subtract(eyePos);
        double dot = lookVec.dotProduct(toEntity);
        double lengthSquared = toEntity.lengthSquared();
        
        if (dot < DOT_PRODUCT_THRESHOLD) {
            return false;
        }
        
        return dot * dot > lengthSquared * (CONE_ANGLE_FACTOR * CONE_ANGLE_FACTOR);
    }

    protected ServerWorld getServerWorld() {
        return (ServerWorld) caster.world;
    }
}
