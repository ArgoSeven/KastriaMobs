package org.argoseven.kastriamobs.goals;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Difficulty;
import org.argoseven.kastriamobs.Config;
import org.argoseven.kastriamobs.KastriaMobs;
import org.argoseven.kastriamobs.entity.CursedBullet;

public class SummonCursedBullet extends Goal {
    private final MobEntity caster;
    private int cooldown;
    private final int maxCooldown;
    private final float activationRange;
    private final float maxRangeAttack;



    public SummonCursedBullet(MobEntity caster, int maxCooldown , int activationRange, int maxRangeAttack) {
        this.caster = caster;
        this.maxCooldown = maxCooldown;
        this.activationRange = activationRange;
        this.maxRangeAttack = maxRangeAttack;
    }

    public SummonCursedBullet(MobEntity caster, Config.CursedBulletConfig cursedBulletConfig) {
        this.caster = caster;
        this.maxCooldown = cursedBulletConfig.max_cooldown;
        this.activationRange = cursedBulletConfig.range_of_activation;
        this.maxRangeAttack = cursedBulletConfig.max_range_of_attack;
    }




    @Override
    public boolean canStart() {
        LivingEntity target = this.caster.getTarget();
        return target != null && target.isAlive() && this.caster.canTarget(target) && this.caster.squaredDistanceTo(target) > KastriaMobs.getSquared(activationRange);
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
            summonBullets();
            cooldown = maxCooldown;
        }
    }

    protected void summonBullets() {
        if (caster.world.getDifficulty() != Difficulty.PEACEFUL) {
            LivingEntity livingEntity = caster.getTarget();
            if (livingEntity != null) {
                caster.getLookControl().lookAt(livingEntity, 180.0F, 180.0F);
                double d = caster.squaredDistanceTo(livingEntity);
                if (d < KastriaMobs.getSquared(maxRangeAttack)) {
                    if (this.cooldown <= 0) {
                        this.cooldown = maxCooldown + caster.getRandom().nextInt() * maxCooldown / 2;
                        caster.world.spawnEntity(new CursedBullet(caster.world, caster, livingEntity, caster.getMovementDirection().getAxis(), new StatusEffectInstance(StatusEffects.LEVITATION, 10)));
                        caster.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F + 1.0F);
                    }
                } else {
                    caster.setTarget((LivingEntity)null);
                }
                super.tick();
            }
        }
    }
}

